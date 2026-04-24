package com.vbshkn.ikollect.domain.usecase

import com.vbshkn.ikollect.domain.AppError
import com.vbshkn.ikollect.domain.ValidationError
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class ValidateEmailUseCase @Inject constructor() {
    operator fun invoke(email: String): ValidationError.EmailError? {
        if (email.isBlank()) {
            return ValidationError.EmailError.EmptyEmail
        }
        if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            return ValidationError.EmailError.InvalidEmailFormat
        }
        return null
    }
}