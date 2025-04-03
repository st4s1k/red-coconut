package com.st4s1k.red_coconut.example.storage

import com.st4s1k.red_coconut.config.StorageProperties
import com.st4s1k.red_coconut.service.storage.StorageException
import com.st4s1k.red_coconut.service.storage.StorageService
import com.st4s1k.red_coconut.service.storage.UploadProgressListener
import io.awspring.cloud.s3.S3Template
import io.github.oshai.kotlinlogging.KotlinLogging
import org.springframework.core.io.Resource
import org.springframework.stereotype.Component
import org.springframework.web.multipart.MultipartFile
import software.amazon.awssdk.core.sync.RequestBody
import software.amazon.awssdk.services.s3.S3Client
import software.amazon.awssdk.services.s3.model.AbortMultipartUploadRequest
import software.amazon.awssdk.services.s3.model.CompleteMultipartUploadRequest
import software.amazon.awssdk.services.s3.model.CompletedMultipartUpload
import software.amazon.awssdk.services.s3.model.CompletedPart
import software.amazon.awssdk.services.s3.model.CreateMultipartUploadRequest
import software.amazon.awssdk.services.s3.model.DeleteObjectRequest
import software.amazon.awssdk.services.s3.model.HeadObjectRequest
import software.amazon.awssdk.services.s3.model.S3Exception
import software.amazon.awssdk.services.s3.model.UploadPartRequest
import java.net.URI

/**
 * Cloud storage implementation of the StorageService interface.
 * Works with S3-compatible storage providers.
 */
@Component
class S3StorageService(
    private val config: StorageProperties,
    private val s3Template: S3Template,
    private val s3Client: S3Client
) : StorageService {

    companion object {
        private val log = KotlinLogging.logger {}
        private const val MIN_PART_SIZE = 5 * 1024 * 1024L  // 5MB minimum (AWS requirement)
    }

    override fun upload(
        file: MultipartFile,
        path: String,
        listener: UploadProgressListener?
    ): URI {
        // Check if the file exceeds the chunk threshold and chunked uploads are enabled
        return if (file.size > config.chunkThreshold && config.useChunkedUploads) {
            log.info { "File size (${file.size} bytes) exceeds chunk threshold (${config.chunkThreshold} bytes), using multipart upload" }
            uploadMultipart(file, path, listener)
        } else {
            // For smaller files, use the simpler S3Template
            file.inputStream.use { inputStream ->
                val s3Resource = s3Template.upload(config.location, path, inputStream)
                log.info { "File uploaded using single-part upload: '$path'" }
                s3Resource.uri
            }
        }
    }

    /**
     * Uploads a file using multipart upload.
     */
    private fun uploadMultipart(
        file: MultipartFile,
        path: String,
        listener: UploadProgressListener?
    ): URI {
        val totalSize = file.size

        // Create a multipart upload
        val createRequest = CreateMultipartUploadRequest.builder()
            .bucket(config.location)
            .key(path)
            .build()

        val uploadId = s3Client.createMultipartUpload(createRequest).uploadId()
        log.info { "Started multipart upload for $path with ID: $uploadId" }

        val partSize = calculatePartSize(totalSize)
        val completedParts = mutableListOf<CompletedPart>()
        var partNumber = 1
        var bytesUploaded = 0L

        try {
            file.inputStream.use { inputStream ->
                val buffer = ByteArray(partSize.toInt())
                var bytesRead: Int

                while (inputStream.read(buffer).also { bytesRead = it } > 0) {
                    val partContent = if (bytesRead < buffer.size) {
                        buffer.copyOf(bytesRead)
                    } else {
                        buffer
                    }

                    val uploadPartRequest = UploadPartRequest.builder()
                        .bucket(config.location)
                        .key(path)
                        .uploadId(uploadId)
                        .partNumber(partNumber)
                        .build()

                    val eTag = s3Client.uploadPart(
                        uploadPartRequest,
                        RequestBody.fromBytes(partContent)
                    ).eTag()

                    completedParts.add(
                        CompletedPart.builder()
                            .partNumber(partNumber)
                            .eTag(eTag)
                            .build()
                    )

                    bytesUploaded += bytesRead
                    listener?.onProgress(bytesUploaded, totalSize)
                    log.debug { "Uploaded part $partNumber for $path: $bytesUploaded of $totalSize bytes" }

                    partNumber++
                }
            }

            // Complete the multipart upload
            val completeRequest = CompleteMultipartUploadRequest.builder()
                .bucket(config.location)
                .key(path)
                .uploadId(uploadId)
                .multipartUpload(
                    CompletedMultipartUpload.builder()
                        .parts(completedParts)
                        .build()
                )
                .build()

            s3Client.completeMultipartUpload(completeRequest)
            log.info { "Completed multipart upload for $path with ID: $uploadId" }

            return URI.create("s3://${config.location}/$path")

        } catch (e: Exception) {
            log.error(e) { "Error during multipart upload for $path with ID: $uploadId" }

            try {
                // Abort the multipart upload on error
                val abortRequest = AbortMultipartUploadRequest.builder()
                    .bucket(config.location)
                    .key(path)
                    .uploadId(uploadId)
                    .build()

                s3Client.abortMultipartUpload(abortRequest)
                log.info { "Aborted multipart upload for $path with ID: $uploadId" }

            } catch (abortEx: Exception) {
                log.error(abortEx) { "Failed to abort multipart upload for $path with ID: $uploadId" }
            }

            throw StorageException("Failed to upload file", e)
        }
    }

    /**
     * Calculates the optimal part size based on the file size and configuration.
     */
    private fun calculatePartSize(totalSize: Long): Long {
        // Calculate minimum required part size based on max parts limit
        val minRequiredPartSize = (totalSize / config.maxParts) + 1

        // Part size must be at least the minimum (5MB) and default to the configured chunk size
        return maxOf(minRequiredPartSize, MIN_PART_SIZE, config.chunkSize)
    }

    /**
     * Downloads a file from S3 storage.
     */
    override fun download(path: String): Resource {
        log.info { "Downloading file from S3: $path" }
        try {
            // Use S3Template to get the resource
            val resource = s3Template.download(config.location, path)
            if (!resource.exists()) {
                throw StorageException("File not found: $path")
            }
            return resource
        } catch (e: Exception) {
            log.error(e) { "Failed to download file from S3: $path" }
            throw StorageException("Failed to download file from S3", e)
        }
    }

    /**
     * Checks if a file exists in S3 storage.
     */
    override fun exists(path: String): Boolean {
        try {
            val headObjectRequest = HeadObjectRequest.builder()
                .bucket(config.location)
                .key(path)
                .build()

            s3Client.headObject(headObjectRequest)
            return true
        } catch (e: S3Exception) {
            if (e.statusCode() == 404) {
                log.info { "File not found in S3: $path" }
                return false
            }
            log.error(e) { "Error checking if file exists in S3: $path" }
            throw StorageException("Failed to check if file exists in S3", e)
        } catch (e: Exception) {
            log.error(e) { "Error checking if file exists in S3: $path" }
            throw StorageException("Failed to check if file exists in S3", e)
        }
    }

    /**
     * Deletes a file from S3 storage.
     */
    override fun delete(path: String) {
        try {
            val deleteObjectRequest = DeleteObjectRequest.builder()
                .bucket(config.location)
                .key(path)
                .build()

            s3Client.deleteObject(deleteObjectRequest)
            log.info { "Deleted file from S3: $path" }
        } catch (e: Exception) {
            log.error(e) { "Failed to delete file from S3: $path" }
            throw StorageException("Failed to delete file from S3", e)
        }
    }
}