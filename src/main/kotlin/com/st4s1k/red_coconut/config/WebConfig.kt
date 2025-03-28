package com.st4s1k.red_coconut.config

import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Configuration
import org.springframework.web.servlet.config.annotation.CorsRegistry
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer

@Configuration
class WebConfig : WebMvcConfigurer {

    @Value("\${app.cors.allowed-origins:*}")
    private lateinit var allowedOrigins: String

    @Value("\${app.cors.allowed-methods:GET,POST,PUT,DELETE}")
    private lateinit var allowedMethods: String

    override fun addCorsMappings(registry: CorsRegistry) {
        registry.addMapping("/api/**")
            .allowedOrigins(*allowedOrigins.split(",").toTypedArray())
            .allowedMethods(*allowedMethods.split(",").toTypedArray())
            .allowCredentials(true)
            .maxAge(3600)
    }
}
