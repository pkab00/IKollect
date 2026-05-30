package com.vbshkn.ikollect.domain.usecase.auth

import com.vbshkn.ikollect.data.local.database.AppDatabase
import com.vbshkn.ikollect.data.repository.AuthRepositoryImpl
import com.vbshkn.ikollect.di.ApplicationScope
import com.vbshkn.ikollect.domain.repository.AuthRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import javax.inject.Inject

class LogOutUseCase @Inject constructor(
    private val authRepository: AuthRepository,
    private val database: AppDatabase,
    @ApplicationScope private val scope: CoroutineScope
) {
    suspend operator fun invoke() {
        authRepository.signOut()
        scope.launch {
            database.clearAllTables()
        }
    }
}