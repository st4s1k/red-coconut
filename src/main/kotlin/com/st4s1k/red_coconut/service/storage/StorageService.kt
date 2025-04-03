package com.st4s1k.red_coconut.service.storage

import org.springframework.core.io.Resource
import org.springframework.web.multipart.MultipartFile
import java.net.URI

/**
 * Interface for file storage services.
 * Provides a clean, minimal API for file operations.
 */
interface StorageService {
    /**
     * Uploads a file to the storage system.
     * The implementation handles all details including chunked uploads if needed.
     *
     * @param file The file to upload
     * @param path The path where to store the file
     * @param listener Optional listener for upload progress updates
     * @return The URI to access the uploaded file
     */
    fun upload(
        file: MultipartFile,
        path: String,
        listener: UploadProgressListener?
    ): URI
    
    /**
     * Downloads a file from the storage system.
     *
     * @param path The path to the file in storage
     * @return Resource for the downloaded file
     */
    fun download(path: String): Resource
    
    /**
     * Checks if a file exists in the storage system.
     *
     * @param path The path to check
     * @return true if file exists, false otherwise
     */
    fun exists(path: String): Boolean
    
    /**
     * Deletes a file from the storage system.
     *
     * @param path The path to the file to delete
     */
    fun delete(path: String)
}
