package com.vbshkn.ikollect.domain.usecase

import com.google.firebase.auth.FirebaseAuthUserCollisionException
import com.google.firebase.auth.FirebaseAuthWeakPasswordException
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseAuthException
import com.vbshkn.ikollect.data.repository.AuthRepository
import com.vbshkn.ikollect.domain.UserAuthError
import javax.inject.Inject

class RegisterUserUseCase @Inject constructor(
    private val authRepository: AuthRepository
) {
    suspend operator fun invoke(email: String, password: String): UserAuthError? {
        return try {
            authRepository.createUser(email, password)
            null
        } catch (e: Exception) {
            when (e) {
                is FirebaseAuthUserCollisionException -> UserAuthError.Registration.EmailAlreadyInUse
                else -> UserAuthError.Registration.UnknownError(e.localizedMessage ?: "Unknown error")
            }
        }
    }
}