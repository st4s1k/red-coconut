package com.st4s1k.red_coconut.util

import com.st4s1k.red_coconut.service.storage.UploadProgressListener
import org.springframework.web.multipart.MultipartFile
import java.util.concurrent.ConcurrentHashMap

/**
 * Utility class for file upload operations.
 */
object UploadUtils {
    // Stores progress information for active uploads
    private val progressTracker = ConcurrentHashMap<String, UploadProgress>()

    /**
     * Generates a unique upload ID.
     */
    fun createUploadId(file: MultipartFile): String {
        return "upload-${System.currentTimeMillis()}-${file.originalFilename?.hashCode() ?: 0}"
    }

    /**
     * Creates and stores progress tracking for an upload.
     */
    fun trackProgress(uploadId: String, file: MultipartFile): UploadProgress {
        val progress = UploadProgress(
            id = uploadId,
            fileName = file.originalFilename ?: "unknown",
            totalBytes = file.size
        )
        progressTracker[uploadId] = progress
        return progress
    }

    /**
     * Creates a progress listener for the given upload and progress tracker.
     */
    fun createProgressListener(progress: UploadProgress): UploadProgressListener {
        return UploadProgressListener { bytesUploaded, totalBytes ->
            progress.bytesUploaded = bytesUploaded
            progress.percentComplete = ((bytesUploaded.toDouble() / totalBytes) * 100).toInt()
        }
    }

    /**
     * Gets progress information for an upload.
     */
    fun getProgress(uploadId: String): UploadProgress? {
        return progressTracker[uploadId]
    }

    /**
     * Updates progress for a completed upload.
     */
    fun completeUpload(uploadId: String, fileUri: String) {
        progressTracker[uploadId]?.let {
            it.status = UploadStatus.COMPLETED
            it.fileUri = fileUri
        }
    }

    /**
     * Updates progress for a failed upload.
     */
    fun failUpload(uploadId: String, errorMessage: String) {
        progressTracker[uploadId]?.let {
            it.status = UploadStatus.FAILED
            it.errorMessage = errorMessage
        }
    }

    /**
     * Removes progress tracking for an upload.
     */
    fun removeProgress(uploadId: String) {
        progressTracker.remove(uploadId)
    }

    /**
     * Class to track upload progress.
     */
    data class UploadProgress(
        val id: String,
        val fileName: String,
        val totalBytes: Long,
        var bytesUploaded: Long = 0,
        var percentComplete: Int = 0,
        var status: UploadStatus = UploadStatus.IN_PROGRESS,
        var fileUri: String? = null,
        var errorMessage: String? = null
    )

    /**
     * Enum for upload status.
     */
    enum class UploadStatus {
        IN_PROGRESS, COMPLETED, FAILED
    }
}
