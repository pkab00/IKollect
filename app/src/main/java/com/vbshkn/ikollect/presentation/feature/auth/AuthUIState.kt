package com.vbshkn.ikollect.presentation.feature.auth

import com.vbshkn.ikollect.domain.AppError
import com.vbshkn.ikollect.domain.ValidationError
import com.vbshkn.ikollect.domain.UserAuthError

data class AuthUIState(
    val email: String = "",
    val password: String = "",
    val nickname: String = "",
    val isLoading: Boolean = false,
    val nicknameValidationError: ValidationError.NicknameError? = null,
    val emailValidationError: ValidationError.EmailError? = null,
    val passwordValidationError: ValidationError.PasswordError? = null
)
