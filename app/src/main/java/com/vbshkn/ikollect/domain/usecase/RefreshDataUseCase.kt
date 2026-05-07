package com.vbshkn.ikollect.domain.usecase

import com.vbshkn.ikollect.data.background.HandshakeResult
import com.vbshkn.ikollect.data.background.SyncManager
import com.vbshkn.ikollect.data.repository.AuthRepository
import javax.inject.Inject

private typealias Succeed = Boolean

class RefreshDataUseCase @Inject constructor(
    private val syncManager: SyncManager,
    private val authRepository: AuthRepository
) {
    suspend operator fun invoke(): Succeed {
        val userId = authRepository.awaitAndGetUid()
        if (userId != null) {
            val handshakeResult = syncManager.performHandshake(userId)
            return handshakeResult is HandshakeResult.FullSuccess
        }
        return false
    }
}