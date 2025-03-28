package com.st4s1k.red_coconut.controller

import com.st4s1k.red_coconut.controller.api.ApiResponse
import com.st4s1k.red_coconut.service.storage.StorageException
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

    @ExceptionHandler(StorageException::class)
    fun handleStorageException(ex: StorageException): ResponseEntity<ApiResponse> {
        return ResponseEntity
            .status(HttpStatus.INTERNAL_SERVER_ERROR)
            .body(ApiResponse.Error(
                message = "Storage error: ${ex.message}",
                errorCode = "STORAGE_ERROR"
            ))
    }

    @ExceptionHandler(ResponseStatusException::class)
    fun handleResponseStatusException(ex: ResponseStatusException): ResponseEntity<ApiResponse> {
        return ResponseEntity
            .status(ex.statusCode)
            .body(ApiResponse.Error(
                message = ex.reason ?: "Request error",
                errorCode = "REQUEST_ERROR"
            ))
    }

    @ExceptionHandler(MaxUploadSizeExceededException::class)
    fun handleMaxUploadSizeExceededException(ex: MaxUploadSizeExceededException): ResponseEntity<ApiResponse> {
        return ResponseEntity
            .status(HttpStatus.PAYLOAD_TOO_LARGE)
            .body(ApiResponse.Error(
                message = "The uploaded file exceeds the maximum allowed size",
                errorCode = "FILE_TOO_LARGE"
            ))
    }

    @ExceptionHandler(IllegalArgumentException::class)
    fun handleIllegalArgumentException(ex: IllegalArgumentException): ResponseEntity<ApiResponse> {
        return ResponseEntity
            .status(HttpStatus.BAD_REQUEST)
            .body(ApiResponse.Error(
                message = ex.message ?: "Invalid request parameters",
                errorCode = "INVALID_ARGUMENT"
            ))
    }

    @ExceptionHandler(Exception::class)
    fun handleGenericException(ex: Exception): ResponseEntity<ApiResponse> {
        return ResponseEntity
            .status(HttpStatus.INTERNAL_SERVER_ERROR)
            .body(ApiResponse.Error(
                message = "An unexpected error occurred",
                errorCode = "INTERNAL_ERROR"
            ))
    }
}
