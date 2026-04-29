package com.vbshkn.ikollect.domain.usecase

import com.vbshkn.ikollect.data.repository.AuthRepository
import com.vbshkn.ikollect.domain.UserAuthError
import io.github.jan.supabase.auth.exception.AuthRestException
import javax.inject.Inject

class RegisterUserUseCase @Inject constructor(
    private val authRepository: AuthRepository
) {
    suspend operator fun invoke(email: String, password: String, nickname: String): UserAuthError? {
        return try {
            authRepository.createUser(email, password, nickname)
            null
        } catch (e: Exception) {
            when (e) {
                is AuthRestException -> {
                    when (e.error) {
                        "user_already_exists" -> UserAuthError.Registration.EmailAlreadyInUse
                        "weak_password" -> UserAuthError.Registration.UnknownError("Weak password.")
                        "over_email_send_rate_limit" -> UserAuthError.Registration.UnknownError("Too many attempts. Try again later.")
                        else -> UserAuthError.Registration.UnknownError(e.description ?: "Registration error.")
                    }
                }
                is java.net.ConnectException -> UserAuthError.Registration.UnknownError("Server connection error.")
                else -> UserAuthError.Registration.UnknownError(e.localizedMessage ?: "Unknown error.")
            }
        }
    }
}