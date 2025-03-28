package com.st4s1k.red_coconut.config

import com.st4s1k.red_coconut.service.auth.LogoutService
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.security.config.Customizer
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.core.Authentication
import org.springframework.security.web.SecurityFilterChain
import org.springframework.security.web.authentication.logout.SimpleUrlLogoutSuccessHandler

/**
 * Security configuration for the application.
 */
@Configuration
class SecurityConfig(
    private val logoutService: LogoutService
) {

    @Bean
    fun securityFilterChain(http: HttpSecurity): SecurityFilterChain {
        return http
            .csrf { it.disable() }
            .authorizeHttpRequests {
                it.requestMatchers("/login/**", "/error/**", "/api/health/**").permitAll()
                it.anyRequest().authenticated()
            }
            .oauth2Login(Customizer.withDefaults())
            .logout { it.logoutSuccessHandler(logoutSuccessHandler()) }
            .build()
    }

    @Bean
    fun logoutSuccessHandler(): SimpleUrlLogoutSuccessHandler {
        return object : SimpleUrlLogoutSuccessHandler() {
            override fun determineTargetUrl(
                request: HttpServletRequest?,
                response: HttpServletResponse?,
                authentication: Authentication?
            ): String {
                val redirectUri = request?.requestURL?.toString()?.substringBefore("/logout") + "/login"
                return logoutService.getLogoutUrl(redirectUri)
            }
        }
    }
}
