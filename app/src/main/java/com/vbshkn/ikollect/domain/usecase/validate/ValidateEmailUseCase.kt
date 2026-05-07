package com.vbshkn.ikollect.domain.usecase.validate

import android.util.Patterns
import com.vbshkn.ikollect.domain.error.ValidationError
import javax.inject.Inject

class ValidateEmailUseCase @Inject constructor() {
    operator fun invoke(email: String): ValidationError.EmailError? {
        if (email.isBlank()) {
            return ValidationError.EmailError.EmptyEmail
        }
        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            return ValidationError.EmailError.InvalidEmailFormat
        }
        return null
    }
}