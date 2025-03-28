package com.st4s1k.red_coconut.service.storage

/**
 * Interface for monitoring upload progress.
 */
fun interface UploadProgressListener {
    /**
     * Called periodically during upload to report progress.
     *
     * @param bytesUploaded The number of bytes uploaded so far
     * @param totalBytes The total number of bytes to upload
     */
    fun onProgress(bytesUploaded: Long, totalBytes: Long)
}
