package com.vbshkn.ikollect.domain.usecase.get

import com.vbshkn.ikollect.data.remote.NetworkResult
import com.vbshkn.ikollect.data.repository.AuthRepositoryImpl
import com.vbshkn.ikollect.domain.model.AppUser
import com.vbshkn.ikollect.domain.repository.AuthRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetUserProfileUseCase @Inject constructor(
    private val authRepository: AuthRepository
) {
    operator fun invoke(): Flow<NetworkResult<AppUser?>> {
        return authRepository.getUser()
    }
}