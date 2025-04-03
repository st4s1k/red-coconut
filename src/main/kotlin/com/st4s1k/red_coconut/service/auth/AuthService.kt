package com.st4s1k.red_coconut.service.auth

import org.springframework.security.core.Authentication
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.oauth2.core.user.OAuth2User
import org.springframework.stereotype.Component

@Component
class AuthService {
    val authentication: Authentication
        get() = SecurityContextHolder.getContext().authentication

    val principal: OAuth2User
        get() = authentication.principal as OAuth2User

    val userIdentifier: String
        get() = authentication.name

    val userAttributes: Map<String, Any>
        get() = principal.attributes

}