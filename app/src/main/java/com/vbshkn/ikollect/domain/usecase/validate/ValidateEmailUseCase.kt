package com.vbshkn.ikollect.domain.usecase.validate

import com.vbshkn.ikollect.domain.error.ValidationError
import javax.inject.Inject

class ValidateEmailUseCase @Inject constructor() {
    operator fun invoke(email: String): ValidationError.EmailError? {
        val emailRegex = Regex(
            "[a-zA-Z0-9+._%\\-]{1,256}" +
                    "@" +
                    "[a-zA-Z0-9][a-zA-Z0-9\\-]{0,64}" +
                    "(" +
                    "\\." +
                    "[a-zA-Z0-9][a-zA-Z0-9\\-]{0,25}" +
                    ")+"
        )
        if (email.isBlank()) {
            return ValidationError.EmailError.EmptyEmail
        }
        if (!email.matches(emailRegex)) {
            return ValidationError.EmailError.InvalidEmailFormat
        }
        return null
    }
}