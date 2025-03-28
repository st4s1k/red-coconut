package com.st4s1k.red_coconut.controller.file

import com.st4s1k.red_coconut.service.FileService
import io.awspring.cloud.s3.S3Resource
import org.springframework.http.ResponseEntity
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.security.oauth2.core.user.OAuth2User
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.multipart.MultipartFile


@RestController
@RequestMapping("/api/files")
class FileController(private val service: FileService) {

    @PostMapping("/upload")
    fun upload(
        @AuthenticationPrincipal principal: OAuth2User,
        @RequestParam("file") file: MultipartFile
    ): ResponseEntity<String> {
        return try {
            val email: String? = principal.getAttribute("email")
            val s3Resource: S3Resource? = service.upload(file, email)
            ResponseEntity.created(s3Resource!!.uri).body("File uploaded successfully")
        } catch (e: Exception) {
            ResponseEntity.internalServerError().body("Failed to upload file: ${e.message}")
        }
    }
}
