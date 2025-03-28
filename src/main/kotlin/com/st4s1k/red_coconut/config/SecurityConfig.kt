package com.st4s1k.red_coconut.config

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.security.config.Customizer.withDefaults
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.web.SecurityFilterChain


@Configuration
class SecurityConfig {

    @Bean
    fun securityFilterChain(
        http: HttpSecurity,
        cognitoLogoutHandler: CognitoLogoutHandler
    ): SecurityFilterChain {
        return http
            .csrf { it.disable() }
            .authorizeHttpRequests {
                it.requestMatchers("/login/**").permitAll()
                it.anyRequest().authenticated()
            }
            .oauth2Login(withDefaults())
            .logout { it.logoutSuccessHandler(cognitoLogoutHandler) }
            .build()
    }

}
