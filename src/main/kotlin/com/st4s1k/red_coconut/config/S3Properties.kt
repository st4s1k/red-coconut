package com.st4s1k.red_coconut.config

import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.stereotype.Component

@Component
@ConfigurationProperties(prefix = "app.s3")
class S3Properties {
    lateinit var bucketName: String
}
