package com.st4s1k.red_coconut.service.auth

/**
 * Interface for logout services.
 * Handles provider-specific logout requirements.
 */
fun interface LogoutService {
    /**
     * Generates a logout URL for the authentication provider
     *
     * @param redirectUri The URI to redirect to after logout
     * @return The fully formed logout URL
     */
    fun getLogoutUrl(redirectUri: String): String
}
