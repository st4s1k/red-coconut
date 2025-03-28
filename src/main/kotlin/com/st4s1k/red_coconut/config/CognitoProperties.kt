package com.st4s1k.red_coconut.config

import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.stereotype.Component

@Component
@ConfigurationProperties(prefix = "app.cognito")
class CognitoProperties {
    lateinit var clientId: String
    lateinit var logoutUri: String
    lateinit var logoutRedirectUri: String
}
