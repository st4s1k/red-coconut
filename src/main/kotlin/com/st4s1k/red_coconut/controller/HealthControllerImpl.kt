package com.st4s1k.red_coconut.controller

import com.st4s1k.red_coconut.api.SystemApi
import com.st4s1k.red_coconut.model.HealthResponse
import org.springframework.boot.actuate.health.Health
import org.springframework.boot.actuate.health.HealthIndicator
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.RestController

@RestController
class HealthControllerImpl(
    private val healthIndicators: List<HealthIndicator>
) : SystemApi {

    override fun healthCheck(): ResponseEntity<HealthResponse> {
        val healthStatus = mutableMapOf<String, Any>()
        
        healthIndicators.forEach { indicator ->
            val health = indicator.health()
            val name = indicator.javaClass.simpleName.replace("HealthIndicator", "")
            healthStatus[name.lowercase()] = mapOf(
                "status" to health.status.toString(),
                "details" to (health.details ?: emptyMap<String, Any>())
            )
        }
        
        val responseData = HealthResponse.Data(
            status = "UP",
            components = healthStatus
        )
        
        val response = HealthResponse(
            success = true,
            message = "Service health status",
            data = responseData
        )
        
        return ResponseEntity.ok(response)
    }
}
