package com.st4s1k.red_coconut.controller.file

import com.st4s1k.red_coconut.api.FileControllerApi
import com.st4s1k.red_coconut.model.FileUploadResponse
import com.st4s1k.red_coconut.model.UploadStatusResponse
import com.st4s1k.red_coconut.service.FileService
import com.st4s1k.red_coconut.service.auth.AuthService
import com.st4s1k.red_coconut.util.UploadUtils
import org.springframework.core.io.Resource
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.multipart.MultipartFile
import org.springframework.web.server.ResponseStatusException

/**
 * Controller for file operations.
 */
@RestController
@RequestMapping("/api/files")
class FileController(
    private val fileService: FileService,
    private val authService: AuthService
) : FileControllerApi {

    @PostMapping("/upload")
    override fun upload(file: MultipartFile?): ResponseEntity<FileUploadResponse> {
        val file = file
            ?: throw ResponseStatusException(HttpStatus.BAD_REQUEST, "File is required")
        val uploadId = UploadUtils.createUploadId(file)
        val progress = UploadUtils.trackProgress(uploadId, file)
        val uploadProgressListener = UploadUtils.createProgressListener(progress)
        val fileUri = fileService.uploadFile(file, authService.userIdentifier, uploadProgressListener)
        return ResponseEntity.created(fileUri).body(
            FileUploadResponse(
                fileUrl = fileUri.toString(),
                uploadId = uploadId,
                fileName = file.originalFilename ?: "unknown",
                propertySize = file.size
            )
        )
    }

    @GetMapping("/status/{uploadId}")
    override fun getUploadStatus(
        @PathVariable("uploadId") uploadId: String
    ): ResponseEntity<UploadStatusResponse> {
        val progress = UploadUtils.getProgress(uploadId)
            ?: throw ResponseStatusException(
                HttpStatus.NOT_FOUND,
                "Upload progress not found for ID: $uploadId"
            )
        return ResponseEntity.ok(
            UploadStatusResponse(
                id = progress.id,
                fileName = progress.fileName,
                status = UploadStatusResponse.Status.forValue(progress.status.name),
                totalBytes = progress.totalBytes,
                bytesUploaded = progress.bytesUploaded,
                percentComplete = progress.percentComplete,
                fileUri = progress.fileUri,
                errorMessage = progress.errorMessage
            )
        )
    }

    @GetMapping("/download")
    override fun downloadFile(
        @RequestParam("fileName") fileName: String
    ): ResponseEntity<Resource> {
        val fileResource = fileService.downloadFile(fileName, authService.userIdentifier)
        return ResponseEntity.ok(fileResource)
    }

    @GetMapping("/exists")
    override fun checkFileExists(
        @RequestParam("fileName") fileName: String
    ): ResponseEntity<Boolean> {
        val exists = fileService.existsFile(fileName, authService.userIdentifier)
        return ResponseEntity.ok(exists)
    }

    @DeleteMapping("/delete")
    override fun deleteFile(
        @RequestParam("fileName") fileName: String
    ): ResponseEntity<Unit> {
        fileService.deleteFile(fileName, authService.userIdentifier)
        return ResponseEntity.noContent().build()
    }
}
