package com.st4s1k.red_coconut.service

import com.st4s1k.red_coconut.service.storage.StorageException
import com.st4s1k.red_coconut.service.storage.StorageService
import com.st4s1k.red_coconut.service.storage.UploadProgressListener
import com.st4s1k.red_coconut.util.PathUtils
import io.github.oshai.kotlinlogging.KotlinLogging
import org.springframework.core.io.Resource
import org.springframework.stereotype.Service
import org.springframework.web.multipart.MultipartFile
import java.net.URI

/**
 * Service for handling file operations.
 * Uses the StorageService interface for storage operations.
 */
@Service
class FileService(
    private val storageService: StorageService
) {
    companion object {
        private val log = KotlinLogging.logger {}
    }

    /**
     * Uploads a file to the storage system
     *
     * @param file The file to upload
     * @param userIdentifier Identifier for the user (typically email)
     * @param listener Optional listener for tracking progress updates
     * @return URI for the uploaded file
     * @throws IllegalArgumentException if userIdentifier is blank
     * @throws StorageException if the upload fails
     */
    fun uploadFile(
        file: MultipartFile,
        userIdentifier: String?,
        listener: UploadProgressListener? = null
    ): URI {
        require(!userIdentifier.isNullOrBlank()) { "User identifier cannot be null or blank" }

        val path = PathUtils.buildFilePath(userIdentifier, file.originalFilename)

        log.info { "Starting upload for '${file.originalFilename}' (${file.size} bytes) to path '$path'" }

        try {
            val uri = storageService.upload(file, path, listener)
            log.info { "File uploaded successfully to '$path'" }
            return uri
        } catch (e: Exception) {
            log.error(e) { "Failed to upload file '${file.originalFilename}' to '$path'" }
            if (e is StorageException) throw e
            throw StorageException("Failed to upload file", e)
        }
    }

    /**
     * Downloads a file from the storage system
     *
     * @param fileName The name of the file to download
     * @param userIdentifier Identifier for the user (typically email)
     * @return Resource for the downloaded file
     * @throws IllegalArgumentException if userIdentifier is blank
     * @throws StorageException if the download fails
     */
    fun downloadFile(fileName: String, userIdentifier: String?): Resource {
        require(!userIdentifier.isNullOrBlank()) { "User identifier cannot be null or blank" }

        val filePath = PathUtils.buildFilePath(userIdentifier, fileName)

        if (!filePath.startsWith("$userIdentifier/")) {
            throw SecurityException("Access denied to file: $filePath")
        }

        log.info { "Downloading file: $filePath" }

        try {
            return storageService.download(filePath)
        } catch (e: Exception) {
            log.error(e) { "Failed to download file: $filePath" }
            if (e is StorageException) throw e
            throw StorageException("Failed to download file", e)
        }
    }

    /**
     * Checks if a file exists in the storage system
     *
     * @param fileName The name of the file to check
     * @param userIdentifier Identifier for the user (typically email)
     * @return true if the file exists, false otherwise
     * @throws IllegalArgumentException if userIdentifier is blank
     * @throws StorageException if the check fails
     */
    fun existsFile(fileName: String, userIdentifier: String?): Boolean {
        require(!userIdentifier.isNullOrBlank()) { "User identifier cannot be null or blank" }

        val filePath = PathUtils.buildFilePath(userIdentifier, fileName)

        if (!filePath.startsWith("$userIdentifier/")) {
            throw SecurityException("Access denied to file: $filePath")
        }

        log.info { "Checking existence of file: $filePath" }

        return try {
            storageService.exists(filePath)
        } catch (e: Exception) {
            log.error(e) { "Failed to check existence of file: $filePath" }
            if (e is StorageException) throw e
            throw StorageException("Failed to check existence of file", e)
        }
    }

    /**
     * Deletes a file from the storage system
     *
     * @param fileName The name of the file to delete
     * @param userIdentifier Identifier for the user (typically email)
     * @throws IllegalArgumentException if userIdentifier is blank
     * @throws StorageException if the deletion fails
     */
    fun deleteFile(fileName: String, userIdentifier: String?) {
        require(!userIdentifier.isNullOrBlank()) { "User identifier cannot be null or blank" }

        val filePath = PathUtils.buildFilePath(userIdentifier, fileName)

        if (!filePath.startsWith("$userIdentifier/")) {
            throw SecurityException("Access denied to file: $filePath")
        }

        log.info { "Deleting file: $filePath" }

        try {
            storageService.delete(filePath)
            log.info { "File deleted successfully: $filePath" }
        } catch (e: Exception) {
            log.error(e) { "Failed to delete file: $filePath" }
            if (e is StorageException) throw e
            throw StorageException("Failed to delete file", e)
        }
    }
}
