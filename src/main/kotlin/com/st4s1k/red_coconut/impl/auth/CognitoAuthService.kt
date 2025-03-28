package com.st4s1k.red_coconut.impl.auth

import com.st4s1k.red_coconut.impl.auth.config.CognitoAuthConfig
import com.st4s1k.red_coconut.service.auth.AuthenticationService
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty
import org.springframework.security.oauth2.core.user.OAuth2User
import org.springframework.stereotype.Component

/**
 * OAuth2 implementation of the AuthenticationService interface.
 */
@Component
@ConditionalOnProperty(name = ["app.auth.provider"], havingValue = "cognito")
class CognitoAuthService(
    private val config: CognitoAuthConfig
) : AuthenticationService {

    override fun getUserIdentifier(principal: Any): String? {
        val attributes = getUserAttributes(principal)
        return attributes[config.userIdentifierField] as? String
    }

    override fun isAuthenticated(principal: Any): Boolean {
        return principal is OAuth2User
    }

    /**
     * Gets all user attributes from the OAuth principal.
     */
    private fun getUserAttributes(principal: Any): Map<String, Any> {
        if (principal !is OAuth2User) {
            return emptyMap()
        }
        return principal.attributes
    }
}
