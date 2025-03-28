package com.st4s1k.red_coconut.controller

import com.st4s1k.red_coconut.controller.api.ApiResponse
import org.springframework.boot.actuate.health.Health
import org.springframework.boot.actuate.health.HealthIndicator
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/health")
class HealthController(
    private val healthIndicators: List<HealthIndicator>
) {
    @GetMapping
    fun healthCheck(): ApiResponse.Success<Map<String, Any>> {
        val healthStatus = mutableMapOf<String, Any>()
        
        healthIndicators.forEach { indicator ->
            val health = indicator.health()
            val name = indicator.javaClass.simpleName.replace("HealthIndicator", "")
            healthStatus[name.lowercase()] = mapOf(
                "status" to health.status.toString(),
                "details" to (health.details ?: emptyMap<String, Any>())
            )
        }
        
        return ApiResponse.Success(
            message = "Service health status",
            data = mapOf(
                "status" to "UP",
                "components" to healthStatus
            )
        )
    }
}
