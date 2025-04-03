package com.st4s1k.red_coconut.example.controller.user

import com.st4s1k.red_coconut.api.UserControllerApi
import com.st4s1k.red_coconut.service.auth.AuthService
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

/**
 * Controller for user operations.
 */
@RestController
@RequestMapping("/api/users")
class UserController(
    private val authService: AuthService,
) : UserControllerApi {

    @GetMapping("/me")
    override fun getCurrentUser(): ResponseEntity<Map<String, Any>> =
        ResponseEntity.ok(authService.userAttributes)

}
