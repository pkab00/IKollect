package com.vbshkn.ikollect.domain.usecase

import com.vbshkn.ikollect.domain.ValidationError
import javax.inject.Inject

class ValidateNicknameUseCase @Inject constructor() {
    operator fun invoke(nickname: String): ValidationError.NicknameError? {
        val nicknameRegex = Regex("^[a-zA-Z0\\-_]{3,25}$")
        if (nickname.isBlank()) {
            return ValidationError.NicknameError.EmptyNickname
        }
        if (!nickname.matches(nicknameRegex)) {
            return ValidationError.NicknameError.InvalidNicknameFormat
        }
        return null
    }
}