package com.vbshkn.ikollect.domain.usecase

import com.vbshkn.ikollect.data.repository.AuthRepository
import javax.inject.Inject

class LogOutUseCase @Inject constructor(
    private val authRepository: AuthRepository
) {
    operator fun invoke() {
        authRepository.signOut()
    }
}