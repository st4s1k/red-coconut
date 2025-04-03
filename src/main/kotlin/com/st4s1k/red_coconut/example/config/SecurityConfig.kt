package com.st4s1k.red_coconut.example.config

import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.autoconfigure.security.oauth2.client.OAuth2ClientProperties
import org.springframework.boot.autoconfigure.security.oauth2.client.OAuth2ClientPropertiesMapper
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.security.config.Customizer
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.oauth2.client.registration.ClientRegistration
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository
import org.springframework.security.oauth2.client.registration.InMemoryClientRegistrationRepository
import org.springframework.security.web.SecurityFilterChain
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider
import software.amazon.awssdk.regions.Region
import software.amazon.awssdk.services.s3.S3Client

/**
 * Security configuration for the application.
 *
 * This class configures the security filter chain, client registration for AWS Cognito,
 * and S3 client.
 */
@Configuration
class SecurityConfig(
    @Value("\${AWS_COGNITO_CLIENT_ID:undefined}")
    private val awsCognitoClientId: String,
    @Value("\${AWS_COGNITO_CLIENT_SECRET:undefined}")
    private val awsCognitoClientSecret: String,
    @Value("\${AWS_COGNITO_ISSUER_URI:undefined}")
    private val awsCognitoIssuerUri: String,
    @Value("\${AWS_REGION:undefined}")
    private val awsRegion: String,
    @Value("\${AWS_ACCESS_KEY_ID:undefined}")
    private val awsAccessKeyId: String,
    @Value("\${AWS_SECRET_ACCESS_KEY:undefined}")
    private val awsSecretAccessKey: String
) {

    @Bean
    fun securityFilterChain(
        http: HttpSecurity,
        cognitoLogoutHandler: CognitoLogoutHandler
    ): SecurityFilterChain =
        http
            .csrf { it.disable() }
            .authorizeHttpRequests {
                it.requestMatchers("/login/**").permitAll()
                it.anyRequest().authenticated()
            }
            .oauth2Login(Customizer.withDefaults())
            .logout {
                it.logoutSuccessHandler(cognitoLogoutHandler)
            }
            .build()

    @Bean
    fun clientRegistrationRepository(): ClientRegistrationRepository =
        InMemoryClientRegistrationRepository(cognitoClientRegistration())

    private fun cognitoClientRegistration(): ClientRegistration {
        val properties = OAuth2ClientProperties().apply {
            registration["cognito"] = OAuth2ClientProperties.Registration()
                .apply {
                    clientId = awsCognitoClientId
                    clientSecret = awsCognitoClientSecret
                    scope = setOf("openid", "email", "phone")
                    redirectUri = "http://localhost:8080/login/oauth2/code/cognito"
                }
            provider["cognito"] = OAuth2ClientProperties.Provider()
                .apply {
                    issuerUri = awsCognitoIssuerUri
                    userNameAttribute = "username"
                }
        }
        val propertiesMapper = OAuth2ClientPropertiesMapper(properties)
        val clientRegistration: ClientRegistration? = propertiesMapper.asClientRegistrations()["cognito"]
        return clientRegistration!!
    }

    @Bean
    fun s3Client(): S3Client = S3Client.builder()
        .region(Region.of(awsRegion))
        .credentialsProvider(
            StaticCredentialsProvider.create(
                AwsBasicCredentials.create(
                    awsAccessKeyId,
                    awsSecretAccessKey
                )
            )
        )
        .build()

}
