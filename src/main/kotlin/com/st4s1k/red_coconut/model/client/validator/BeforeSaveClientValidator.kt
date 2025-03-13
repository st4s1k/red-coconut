package com.st4s1k.red_coconut.model.client.validator

import com.st4s1k.red_coconut.model.client.Client
import com.st4s1k.red_coconut.repository.client.ClientRepository
import org.springframework.stereotype.Component
import org.springframework.validation.Errors
import org.springframework.validation.Validator

@Component("beforeSaveClientValidator")
class BeforeSaveClientValidator(private val repository: ClientRepository) : Validator {

    override fun supports(clazz: Class<*>): Boolean {
        return Client::class.java == clazz
    }

    override fun validate(target: Any, errors: Errors) {
        val client = target as Client

        if (client.username.isNullOrBlank()) {
            errors.rejectValue("username", "username.empty", "Username cannot be empty")
        } else if (repository.existsByUsername(client.username!!)) {
            errors.rejectValue("username", "username.exists", "Username already exists")
        }

        if (client.firstName.isNullOrBlank()) {
            errors.rejectValue("firstName", "firstName.empty", "First name cannot be empty")
        }

        if (client.lastName.isNullOrBlank()) {
            errors.rejectValue("lastName", "lastName.empty", "Last name cannot be empty")
        }
    }
}
