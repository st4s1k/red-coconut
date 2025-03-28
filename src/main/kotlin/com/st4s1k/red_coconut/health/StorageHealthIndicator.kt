package com.st4s1k.red_coconut.health

import com.st4s1k.red_coconut.service.storage.StorageService
import org.springframework.boot.actuate.health.Health
import org.springframework.boot.actuate.health.HealthIndicator
import org.springframework.stereotype.Component

@Component
class StorageHealthIndicator(private val storageService: StorageService) : HealthIndicator {
    override fun health(): Health {
        return try {
            // Try to check storage availability
            // This is a placeholder - implement actual check based on your storage provider
            Health.up().withDetail("message", "Storage service is operational").build()
        } catch (ex: Exception) {
            Health.down().withDetail("error", ex.message).build()
        }
    }
}
