package com.st4s1k.red_coconut.impl.auth.config

import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.context.annotation.Configuration


/**
 * Configuration properties for OAuth logout.
 */
@Configuration
@ConfigurationProperties(prefix = "auth.cognito.logout")
class CognitoLogoutConfig {
    /**
     * Base URI for the logout endpoint
     */
    var uri: String = ""

    /**
     * Parameter name for the client ID
     */
    var clientIdParam: String = "client_id"

    /**
     * Parameter name for the redirect URI
     */
    var redirectUriParam: String = "logout_uri"
}
