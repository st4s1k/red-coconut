package com.st4s1k.red_coconut.service.storage.config

/**
 * Base configuration interface for storage providers.
 */
interface StorageConfig {
    /**
     * Location identifier for the storage provider.
     * Could be a container name, bucket name, directory path, etc.
     */
    val location: String

    /**
     * Whether to use chunked/multipart uploads
     */
    val useChunkedUploads: Boolean

    /**
     * Size threshold in bytes for using chunked uploads.
     * Only applicable if useChunkedUploads is true.
     */
    val chunkThreshold: Long

    /**
     * Target size in bytes for each chunk.
     * Only applicable if useChunkedUploads is true.
     */
    val chunkSize: Long
}
