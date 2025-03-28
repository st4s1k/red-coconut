package com.st4s1k.red_coconut.service

import com.st4s1k.red_coconut.config.S3Properties
import io.awspring.cloud.s3.S3Resource
import io.awspring.cloud.s3.S3Template
import io.github.oshai.kotlinlogging.KotlinLogging
import org.springframework.stereotype.Service
import org.springframework.web.multipart.MultipartFile
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

@Service
class FileService(
    private val s3Properties: S3Properties,
    private val s3Template: S3Template
) {
    companion object {
        private val log = KotlinLogging.logger {}
    }

    fun upload(
        multipartFile: MultipartFile,
        email: String?
    ): S3Resource? {
        if (email.isNullOrBlank()) {
            log.error { "Email is null or blank, cannot upload file" }
            return null
        }

        val key = buildKey(multipartFile.originalFilename, email)

        multipartFile.inputStream.use { inputStream ->
            return s3Template.upload(s3Properties.bucketName, key, inputStream).also {
                log.info { "File uploaded: '$key'" }
            }
        }
    }

    private fun buildKey(
        originalFilename: String?,
        email: String
    ): String {
        val formattedDateTime = DateTimeFormatter.ofPattern("yyyy-MM-dd-HH-mm-ss")
        val dateTime = formattedDateTime.format(LocalDateTime.now())
        val filename = originalFilename ?: "file-${dateTime}"
        return "$email/$filename"
    }
}
