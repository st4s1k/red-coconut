package com.st4s1k.red_coconut.impl.auth

import com.st4s1k.red_coconut.impl.auth.config.CognitoAuthConfig
import com.st4s1k.red_coconut.impl.auth.config.CognitoLogoutConfig
import com.st4s1k.red_coconut.service.auth.LogoutService
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty
import org.springframework.stereotype.Component
import org.springframework.web.util.UriComponentsBuilder
import java.net.URI
import java.nio.charset.StandardCharsets

/**
 * OAuth implementation of the LogoutService interface.
 */
@Component
@ConditionalOnProperty(name = ["app.auth.provider"], havingValue = "cognito")
class CognitoLogoutService(
    private val authConfig: CognitoAuthConfig,
    private val logoutConfig: CognitoLogoutConfig
) : LogoutService {

    override fun getLogoutUrl(redirectUri: String): String {
        return UriComponentsBuilder
            .fromUri(URI.create(logoutConfig.uri))
            .queryParam(logoutConfig.clientIdParam, authConfig.clientId)
            .queryParam(logoutConfig.redirectUriParam, redirectUri)
            .encode(StandardCharsets.UTF_8)
            .build()
            .toUriString()
    }
}
