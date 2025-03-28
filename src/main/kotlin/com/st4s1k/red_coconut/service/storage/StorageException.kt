package com.st4s1k.red_coconut.service.storage

/**
 * Base exception class for storage operations.
 */
class StorageException(message: String, cause: Throwable? = null) :
    RuntimeException(message, cause)
