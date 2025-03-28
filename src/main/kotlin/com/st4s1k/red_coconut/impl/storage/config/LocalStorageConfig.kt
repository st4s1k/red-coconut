package com.st4s1k.red_coconut.impl.storage.config

import com.st4s1k.red_coconut.service.storage.config.StorageConfig
import com.st4s1k.red_coconut.service.storage.config.StorageProperties
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty
import org.springframework.stereotype.Component
import java.io.File

/**
 * Local filesystem storage configuration.
 */
@Component
@ConditionalOnProperty(name = ["app.storage.provider"], havingValue = "local")
class LocalStorageConfig(properties: StorageProperties) : StorageConfig {
    /**
     * Base directory path for local storage
     */
    override val location: String = properties.location

    /**
     * Whether to use chunked file writing
     */
    override val useChunkedUploads: Boolean = properties.useChunkedUploads

    /**
     * Size threshold for chunked writes
     */
    override val chunkThreshold: Long = properties.chunkThreshold

    /**
     * Size of each chunk for writing
     */
    override val chunkSize: Long = properties.chunkSize

    /**
     * Absolute path to the storage directory
     */
    val absolutePath: File = File(location).absoluteFile

    /**
     * Create base directory if it doesn't exist
     */
    val createIfMissing: Boolean = true

    /**
     * Permissions to set on the files (POSIX format)
     */
    val filePermissions: String = "rw-r--r--"

    init {
        // Ensure the storage directory exists
        if (createIfMissing && !absolutePath.exists()) {
            absolutePath.mkdirs()
        }
    }
}
