package com.vbshkn.ikollect.domain.usecase.validate

import com.vbshkn.ikollect.domain.error.ValidationError
import javax.inject.Inject

class ValidatePasswordUseCase @Inject constructor() {
    private val passwordRegex = Regex("^(?=.*\\d).{8,}$")

    operator fun invoke(password: String): ValidationError.PasswordError? {
        if (password.isBlank()) {
            return ValidationError.PasswordError.EmptyPassword
        }
        if (password.matches(passwordRegex).not()) {
            return ValidationError.PasswordError.InvalidPasswordFormat
        }
        return null
    }
}