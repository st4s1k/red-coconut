package com.st4s1k.red_coconut.service.auth

/**
 * Interface for authentication services.
 * Focused solely on user identity and authentication concerns.
 */
interface AuthenticationService {
    /**
     * Extracts user identifier from the authentication principal.
     * This is the primary identity method used for operations that need user context.
     *
     * @param principal The authentication principal from the security context
     * @return User identifier or null if unavailable
     */
    fun getUserIdentifier(principal: Any): String?

    /**
     * Validates if the provided principal is valid and authenticated.
     *
     * @param principal The authentication principal to check
     * @return true if authenticated, false otherwise
     */
    fun isAuthenticated(principal: Any): Boolean
}
