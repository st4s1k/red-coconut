package com.st4s1k.red_coconut.controller

import com.st4s1k.red_coconut.model.ErrorResponse
import com.st4s1k.red_coconut.service.storage.StorageException
import io.github.oshai.kotlinlogging.KotlinLogging
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.ControllerAdvice
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.multipart.MaxUploadSizeExceededException
import org.springframework.web.server.ResponseStatusException

/**
 * Global exception handler for all controllers.
 */
@ControllerAdvice
class GlobalExceptionHandler {

    companion object {
        private val log = KotlinLogging.logger {}
    }

    @ExceptionHandler(StorageException::class)
    fun handleStorageException(ex: StorageException): ResponseEntity<ErrorResponse> {
        log.error(ex) { "Storage error: ${ex.message}" }
        return ResponseEntity
            .status(HttpStatus.INTERNAL_SERVER_ERROR)
            .body(
                ErrorResponse(
                    message = "Storage error: ${ex.message}",
                    errorCode = "STORAGE_ERROR"
                )
            )
    }

    @ExceptionHandler(ResponseStatusException::class)
    fun handleResponseStatusException(ex: ResponseStatusException): ResponseEntity<ErrorResponse> {
        log.error(ex) { "Response status error: ${ex.message}" }
        return ResponseEntity
            .status(ex.statusCode)
            .body(
                ErrorResponse(
                    message = ex.reason ?: "Request error",
                    errorCode = "REQUEST_ERROR"
                )
            )
    }

    @ExceptionHandler(MaxUploadSizeExceededException::class)
    fun handleMaxUploadSizeExceededException(ex: MaxUploadSizeExceededException): ResponseEntity<ErrorResponse> {
        log.error(ex) { "File too large: ${ex.message}" }
        return ResponseEntity
            .status(HttpStatus.PAYLOAD_TOO_LARGE)
            .body(
                ErrorResponse(
                    message = "The uploaded file exceeds the maximum allowed size",
                    errorCode = "FILE_TOO_LARGE"
                )
            )
    }

    @ExceptionHandler(IllegalArgumentException::class)
    fun handleIllegalArgumentException(ex: IllegalArgumentException): ResponseEntity<ErrorResponse> {
        log.error(ex) { "Invalid argument: ${ex.message}" }
        return ResponseEntity
            .status(HttpStatus.BAD_REQUEST)
            .body(
                ErrorResponse(
                    message = ex.message ?: "Invalid request parameters",
                    errorCode = "INVALID_ARGUMENT"
                )
            )
    }

    @ExceptionHandler(Exception::class)
    fun handleGenericException(ex: Exception): ResponseEntity<ErrorResponse> {
        log.error(ex) { "Unexpected error: ${ex.message}" }
        return ResponseEntity
            .status(HttpStatus.INTERNAL_SERVER_ERROR)
            .body(
                ErrorResponse(
                    message = "An unexpected error occurred",
                    errorCode = "INTERNAL_ERROR"
                )
            )
    }
}
