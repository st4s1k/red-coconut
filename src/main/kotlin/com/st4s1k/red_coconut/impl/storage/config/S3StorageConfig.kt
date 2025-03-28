package com.st4s1k.red_coconut.impl.storage.config

import com.st4s1k.red_coconut.service.storage.config.StorageConfig
import com.st4s1k.red_coconut.service.storage.config.StorageProperties
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty
import org.springframework.stereotype.Component

/**
 * Cloud storage specific configuration.
 */
@Component
@ConditionalOnProperty(name = ["app.storage.provider"], havingValue = "s3")
class S3StorageConfig(properties: StorageProperties) : StorageConfig {
    /**
     * Container/bucket name
     */
    override val location: String = properties.location

    /**
     * Use multipart uploads for cloud storage
     */
    override val useChunkedUploads: Boolean = properties.useChunkedUploads

    /**
     * Threshold for multipart uploads
     */
    override val chunkThreshold: Long = properties.chunkThreshold

    /**
     * Size of each part in multipart uploads
     */
    override val chunkSize: Long = properties.chunkSize

    /**
     * Maximum number of parts allowed (for S3 compatibility)
     */
    val maxParts: Int = 10000

    /**
     * Region for the cloud provider (optional)
     */
    var region: String? = null

    /**
     * Endpoint URL (for non-standard S3 endpoints)
     */
    var endpointUrl: String? = null
}
