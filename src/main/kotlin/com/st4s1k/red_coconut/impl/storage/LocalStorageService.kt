package com.st4s1k.red_coconut.impl.storage

import com.st4s1k.red_coconut.impl.storage.config.LocalStorageConfig
import com.st4s1k.red_coconut.service.storage.StorageException
import com.st4s1k.red_coconut.service.storage.StorageService
import com.st4s1k.red_coconut.service.storage.UploadProgressListener
import io.github.oshai.kotlinlogging.KotlinLogging
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty
import org.springframework.core.io.FileSystemResource
import org.springframework.core.io.Resource
import org.springframework.stereotype.Component
import org.springframework.web.multipart.MultipartFile
import java.io.FileOutputStream
import java.net.URI
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.Paths
import java.nio.file.StandardCopyOption

/**
 * Local filesystem implementation of the StorageService interface.
 */
@Component
@ConditionalOnProperty(name = ["app.storage.provider"], havingValue = "local")
class LocalStorageService(
    private val config: LocalStorageConfig
) : StorageService {

    companion object {
        private val log = KotlinLogging.logger {}
    }

    override fun upload(
        file: MultipartFile,
        path: String,
        listener: UploadProgressListener?
    ): URI {
        val targetLocation = getTargetLocation(path)

        // Ensure target directory exists
        Files.createDirectories(targetLocation.parent)

        return if (file.size > config.chunkThreshold && config.useChunkedUploads) {
            // Use chunked upload for large files
            uploadChunked(file, targetLocation, listener)
        } else {
            // Use simple copy for smaller files
            file.inputStream.use { inputStream ->
                Files.copy(inputStream, targetLocation, StandardCopyOption.REPLACE_EXISTING)
                log.info { "File copied to local storage: $targetLocation" }
            }
            targetLocation.toUri()
        }
    }

    /**
     * Uploads a file in chunks to handle large files more efficiently.
     */
    private fun uploadChunked(
        file: MultipartFile,
        targetLocation: Path,
        listener: UploadProgressListener?
    ): URI {
        try {
            val totalSize = file.size
            var bytesWritten = 0L

            FileOutputStream(targetLocation.toFile()).use { outputStream ->
                file.inputStream.use { inputStream ->
                    val buffer = ByteArray(config.chunkSize.toInt())
                    var bytesRead: Int

                    while (inputStream.read(buffer).also { bytesRead = it } > 0) {
                        outputStream.write(buffer, 0, bytesRead)
                        bytesWritten += bytesRead
                        listener?.onProgress(bytesWritten, totalSize)
                    }

                    outputStream.flush()
                }
            }

            log.info { "File uploaded to local storage using chunked write: $targetLocation" }
            return targetLocation.toUri()

        } catch (e: Exception) {
            log.error(e) { "Failed to upload file to $targetLocation" }
            throw StorageException("Failed to store file locally", e)
        }
    }

    /**
     * Downloads a file from local storage.
     */
    override fun download(path: String): Resource {
        val targetLocation = getTargetLocation(path)
        
        if (!Files.exists(targetLocation)) {
            throw StorageException("File not found: $path")
        }
        
        try {
            log.info { "Serving file from local storage: $targetLocation" }
            return FileSystemResource(targetLocation)
        } catch (e: Exception) {
            log.error(e) { "Failed to retrieve file from local storage: $path" }
            throw StorageException("Failed to download file from local storage", e)
        }
    }

    /**
     * Checks if a file exists in local storage.
     */
    override fun exists(path: String): Boolean {
        return try {
            val targetLocation = getTargetLocation(path)
            Files.exists(targetLocation)
        } catch (e: Exception) {
            log.error(e) { "Error checking if file exists: $path" }
            false
        }
    }

    /**
     * Deletes a file from local storage.
     */
    override fun delete(path: String) {
        val targetLocation = getTargetLocation(path)
        
        try {
            if (Files.exists(targetLocation)) {
                Files.delete(targetLocation)
                log.info { "Deleted file from local storage: $targetLocation" }
            } else {
                log.warn { "File not found for deletion: $targetLocation" }
            }
        } catch (e: Exception) {
            log.error(e) { "Failed to delete file from local storage: $path" }
            throw StorageException("Failed to delete file", e)
        }
    }

    /**
     * Resolves the target file path from the relative path.
     */
    private fun getTargetLocation(relativePath: String): Path {
        val path = Paths.get(config.absolutePath.path, relativePath)

        // Security check: ensure the resolved path is within the storage directory
        if (!path.normalize().startsWith(config.absolutePath.toPath())) {
            throw StorageException("Cannot store file outside the designated storage directory: $relativePath")
        }

        return path
    }
}
