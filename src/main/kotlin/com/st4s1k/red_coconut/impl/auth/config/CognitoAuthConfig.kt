package com.st4s1k.red_coconut.impl.auth.config

import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.context.annotation.Configuration

/**
 * Configuration properties for OAuth authentication.
 */
@Configuration
@ConfigurationProperties(prefix = "auth.cognito")
class CognitoAuthConfig {
    /**
     * Client ID for the OAuth provider
     */
    var clientId: String = ""

    /**
     * User attribute field that contains the user identifier (typically email)
     */
    var userIdentifierField: String = "email"
}
