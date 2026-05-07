package com.vbshkn.ikollect.domain.error

sealed interface UserAuthError {
    sealed interface Login : UserAuthError {
        object InvalidUser : Login
        data class UnknownError(val message: String) : Login
    }

    sealed interface Registration : UserAuthError {
        object EmailAlreadyInUse : Registration
        data class UnknownError(val message: String) : Registration
    }
}