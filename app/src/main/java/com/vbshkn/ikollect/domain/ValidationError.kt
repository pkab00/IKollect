package com.vbshkn.ikollect.domain

sealed interface ValidationError {
    sealed interface NicknameError : ValidationError {
        object EmptyNickname : NicknameError
        object InvalidNicknameFormat : NicknameError
    }
    sealed interface EmailError : ValidationError {
        object EmptyEmail : EmailError
        object InvalidEmailFormat : EmailError
    }
    sealed interface PasswordError : ValidationError {
        object EmptyPassword : PasswordError
        object InvalidPasswordFormat : PasswordError
    }
}