package com.st4s1k.red_coconut.config

import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.context.annotation.Configuration

/**
 * Common storage configuration properties.
 */
@Configuration
@ConfigurationProperties(prefix = "red-coconut.storage")
class StorageProperties {
    /**
     * Root location for storage
     */
    var location: String = ""

    /**
     * Whether to use chunked uploads
     */
    var useChunkedUploads: Boolean = true

    /**
     * Size threshold for chunked uploads (default: 100MB)
     */
    var chunkThreshold: Long = 100 * 1024 * 1024

    /**
     * Size of each chunk (default: 10MB)
     */
    var chunkSize: Long = 10 * 1024 * 1024

    /**
     * Maximum number of parts allowed
     */
    val maxParts: Int = 10000

}