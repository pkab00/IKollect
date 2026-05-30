package com.vbshkn.ikollect.domain.usecase.update

import com.vbshkn.ikollect.data.repository.AuthRepositoryImpl
import com.vbshkn.ikollect.domain.repository.AuthRepository
import javax.inject.Inject

class UpdateUserNicknameUseCase @Inject constructor(
    private val authRepository: AuthRepository
) {
    suspend operator fun invoke(newNickname: String) {
        authRepository.changeNickname(newNickname)
    }
}