package com.vbshkn.ikollect.domain.usecase

import com.vbshkn.ikollect.data.remote.NetworkResult
import com.vbshkn.ikollect.data.repository.AuthRepository
import com.vbshkn.ikollect.domain.model.AppUser
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetUserProfileUseCase @Inject constructor(
    private val authRepository: AuthRepository
) {
    operator fun invoke(): Flow<NetworkResult<AppUser?>> {
        return authRepository.getUser()
    }
}