package com.vbshkn.ikollect.domain.usecase

import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseAuthInvalidUserException
import com.vbshkn.ikollect.data.repository.AuthRepository
import com.vbshkn.ikollect.domain.UserAuthError
import javax.inject.Inject

class LogInUseCase @Inject constructor(
    private val authRepository: AuthRepository
) {
    suspend operator fun invoke(email: String, password: String): UserAuthError.Login? {
        return try {
            authRepository.signIn(email, password)
            null
        } catch (e: Exception) {
            when (e) {
                is FirebaseAuthInvalidUserException, is FirebaseAuthInvalidCredentialsException -> UserAuthError.Login.InvalidUser
                else -> UserAuthError.Login.UnknownError(e.localizedMessage ?: "Unknown error")
            }
        }
    }
}