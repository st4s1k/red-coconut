package com.st4s1k.red_coconut.controller.user

import com.st4s1k.red_coconut.api.UsersApi
import com.st4s1k.red_coconut.service.auth.AuthenticationService
import org.springframework.http.ResponseEntity
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.security.oauth2.core.user.OAuth2User
import org.springframework.web.bind.annotation.RestController

@RestController
class UserControllerImpl(
    private val authService: AuthenticationService
) : UsersApi {

    override fun getCurrentUser(principal: OAuth2User): ResponseEntity<String> {
        return ResponseEntity.ok(authService.getUserIdentifier(principal))
    }
}
