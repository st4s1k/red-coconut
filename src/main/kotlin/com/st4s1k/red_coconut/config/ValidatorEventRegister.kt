package com.st4s1k.red_coconut.config

import org.springframework.beans.factory.InitializingBean
import org.springframework.context.annotation.Configuration
import org.springframework.data.rest.core.event.ValidatingRepositoryEventListener
import org.springframework.validation.Validator

@Configuration
class ValidatorEventRegister(
    private val validators: Map<String, Validator?>?,
    private val validatingRepositoryEventListener: ValidatingRepositoryEventListener
) : InitializingBean {

    companion object {
        @JvmStatic
        val EVENTS = listOf(
            "beforeCreate",
            "beforeSave"
        )
    }

    @Throws(Exception::class)
    override fun afterPropertiesSet(): Unit = validators?.forEach { (key, value) ->
        EVENTS.firstOrNull(key::startsWith)?.let {
            validatingRepositoryEventListener.addValidator(it, value)
        }
    }!!
}
