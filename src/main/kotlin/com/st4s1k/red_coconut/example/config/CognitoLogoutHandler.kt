package com.st4s1k.red_coconut.example.config

import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.beans.factory.annotation.Value
import org.springframework.security.core.Authentication
import org.springframework.security.web.authentication.logout.SimpleUrlLogoutSuccessHandler
import org.springframework.stereotype.Component
import org.springframework.web.util.UriComponentsBuilder
import java.net.URI
import java.nio.charset.StandardCharsets

@Component
class CognitoLogoutHandler(
    @Value("\${AWS_COGNITO_LOGOUT_URI:undefined}")
    private val logoutUri: String,
    @Value("\${AWS_COGNITO_CLIENT_ID:undefined}")
    private val clientId: String,
    @Value("\${AWS_COGNITO_LOGOUT_REDIRECT_URI:undefined}")
    private val logoutRedirectUri: String
) : SimpleUrlLogoutSuccessHandler() {

    override fun determineTargetUrl(
        request: HttpServletRequest,
        response: HttpServletResponse,
        authentication: Authentication?
    ): String {
        return UriComponentsBuilder
            .fromUri(URI.create(logoutUri))
            .queryParam("client_id", clientId)
            .queryParam("logout_uri", logoutRedirectUri)
            .encode(StandardCharsets.UTF_8)
            .build()
            .toUriString()
    }
}
