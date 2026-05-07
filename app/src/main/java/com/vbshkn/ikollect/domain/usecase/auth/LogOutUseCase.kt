package com.vbshkn.ikollect.domain.usecase.auth

import com.vbshkn.ikollect.data.repository.AuthRepository
import javax.inject.Inject

class LogOutUseCase @Inject constructor(
    private val authRepository: AuthRepository
) {
    suspend operator fun invoke() {
        authRepository.signOut()
    }
}