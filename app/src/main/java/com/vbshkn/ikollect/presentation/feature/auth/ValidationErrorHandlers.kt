package com.vbshkn.ikollect.presentation.feature.auth

import androidx.compose.runtime.Composable
import com.vbshkn.ikollect.R
import com.vbshkn.ikollect.domain.error.ValidationError
import com.vbshkn.ikollect.util.UiText

@Composable
fun nicknameErrorHandler(error: ValidationError.NicknameError): UiText = when (error) {
    ValidationError.NicknameError.EmptyNickname -> UiText.StringResource(R.string.nickname_validation_error_empty)
    ValidationError.NicknameError.InvalidNicknameFormat -> UiText.StringResource(R.string.nickname_validation_error_format)
}

@Composable
fun emailErrorHandler(error: ValidationError.EmailError): UiText = when (error) {
    ValidationError.EmailError.EmptyEmail -> UiText.StringResource(R.string.email_validation_error_empty)
    ValidationError.EmailError.InvalidEmailFormat -> UiText.StringResource(R.string.email_validation_error_format)
}

@Composable
fun passwordErrorHandler(error: ValidationError.PasswordError) = when (error) {
    ValidationError.PasswordError.EmptyPassword -> UiText.StringResource(R.string.password_validation_error_empty)
    ValidationError.PasswordError.InvalidPasswordFormat -> UiText.StringResource(R.string.password_validation_error_format)
}