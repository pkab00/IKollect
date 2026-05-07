package com.vbshkn.ikollect.domain.usecase.auth

import com.vbshkn.ikollect.data.repository.AuthRepository
import com.vbshkn.ikollect.domain.error.UserAuthError
import io.github.jan.supabase.auth.exception.AuthRestException
import javax.inject.Inject

class LogInUseCase @Inject constructor(
    private val authRepository: AuthRepository
) {
    suspend operator fun invoke(email: String, password: String): UserAuthError.Login? {
        return try {
            authRepository.signInWithEmail(email, password)
            null
        } catch (e: Exception) {
            when (e) {
                is AuthRestException -> UserAuthError.Login.InvalidUser
                else -> UserAuthError.Login.UnknownError(e.localizedMessage ?: "Unknown error")
            }
        }
    }
}