package com.st4s1k.red_coconut.util

import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

object PathUtils {
    private val dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd-HH-mm-ss")

    fun buildFilePath(userIdentifier: String, originalFilename: String?): String {
        val dateTime = dateTimeFormatter.format(LocalDateTime.now())
        val filename = originalFilename ?: "file-${dateTime}"
        return "$userIdentifier/$filename"
    }

    fun sanitizeFilename(filename: String?): String {
        if (filename == null) return "unknown-file"

        return filename
            .replace(Regex("[.]{2,}"), ".")
            .replace(Regex("[^a-zA-Z0-9._-]"), "_")
    }
}
