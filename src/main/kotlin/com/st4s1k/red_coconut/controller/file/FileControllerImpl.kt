package com.st4s1k.red_coconut.controller.file

import com.st4s1k.red_coconut.api.FilesApi
import com.st4s1k.red_coconut.model.ErrorResponse
import com.st4s1k.red_coconut.model.FileUploadResponse
import com.st4s1k.red_coconut.model.UploadStatusResponse
import com.st4s1k.red_coconut.service.FileService
import com.st4s1k.red_coconut.service.auth.AuthenticationService
import com.st4s1k.red_coconut.util.UploadUtils
import org.springframework.core.io.Resource
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.security.oauth2.core.user.OAuth2User
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.multipart.MultipartFile
import org.springframework.web.server.ResponseStatusException
import java.nio.file.Files

@RestController
class FileControllerImpl(
    private val fileService: FileService,
    private val authService: AuthenticationService
) : FilesApi {

    override fun uploadFile(file: MultipartFile, principal: OAuth2User): ResponseEntity<FileUploadResponse> {
        // Get user identifier
        val userIdentifier = authService.getUserIdentifier(principal)
            ?: throw ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid user identity")

        // Create progress tracking
        val uploadId = UploadUtils.createUploadId(file)
        val progress = UploadUtils.trackProgress(uploadId, file)

        // Upload the file
        val fileUri = fileService.uploadFile(
            file,
            userIdentifier,
            UploadUtils.createProgressListener(progress)
        )

        // Create response data
        val responseData = FileUploadResponse.Data(
            fileUrl = fileUri.toString(),
            uploadId = uploadId,
            fileName = file.originalFilename ?: "unknown",
            size = file.size
        )

        // Create response
        val response = FileUploadResponse(
            success = true,
            message = "File uploaded successfully",
            data = responseData
        )

        return ResponseEntity.created(fileUri).body(response)
    }

    override fun getUploadStatus(uploadId: String): ResponseEntity<Any> {
        val progress = UploadUtils.getProgress(uploadId)
            ?: return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(ErrorResponse(
                    success = false,
                    message = "Upload not found",
                    errorCode = "NOT_FOUND"
                ))

        val responseData = UploadStatusResponse.Data(
            id = progress.id,
            fileName = progress.fileName,
            status = progress.status.name,
            totalBytes = progress.totalBytes,
            bytesUploaded = progress.bytesUploaded,
            percentComplete = progress.percentComplete,
            fileUri = progress.fileUri,
            errorMessage = progress.errorMessage
        )

        val response = UploadStatusResponse(
            success = true,
            message = "Upload status retrieved",
            data = responseData
        )

        return ResponseEntity.ok(response)
    }

    override fun downloadFile(filePath: String, principal: OAuth2User): ResponseEntity<Resource> {
        // Get user identifier
        val userIdentifier = authService.getUserIdentifier(principal)
            ?: throw ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid user identity")

        // Download file
        val fileResource = fileService.downloadFile(filePath, userIdentifier)
        
        // Determine content type
        val contentType = try {
            fileResource.file.let { file ->
                val contentType = Files.probeContentType(file.toPath())
                contentType ?: MediaType.APPLICATION_OCTET_STREAM_VALUE
            }
        } catch (e: Exception) {
            MediaType.APPLICATION_OCTET_STREAM_VALUE
        }

        return ResponseEntity.ok()
            .contentType(MediaType.parseMediaType(contentType))
            .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"${fileResource.filename}\"")
            .body(fileResource)
    }
}
