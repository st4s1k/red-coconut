package com.st4s1k.red_coconut.config

import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.security.core.Authentication
import org.springframework.security.web.authentication.logout.SimpleUrlLogoutSuccessHandler
import org.springframework.stereotype.Component
import org.springframework.web.util.UriComponentsBuilder
import java.net.URI
import java.nio.charset.StandardCharsets

@Component
class CognitoLogoutHandler(
    private val cognitoProperties: CognitoProperties
) : SimpleUrlLogoutSuccessHandler() {

    override fun determineTargetUrl(
        request: HttpServletRequest?,
        response: HttpServletResponse?,
        authentication: Authentication?
    ): String {
        return UriComponentsBuilder
            .fromUri(URI.create(cognitoProperties.logoutUri))
            .queryParam("client_id", cognitoProperties.clientId)
            .queryParam("logout_uri", cognitoProperties.logoutRedirectUri)
            .encode(StandardCharsets.UTF_8)
            .build()
            .toUriString()
    }
}
