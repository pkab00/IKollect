package com.vbshkn.ikollect.domain.repository

import com.vbshkn.ikollect.data.remote.NetworkResult
import com.vbshkn.ikollect.domain.model.AppUser
import kotlinx.coroutines.flow.Flow

interface AuthRepository {
    fun getUser(): Flow<NetworkResult<AppUser?>>

    suspend fun createUser(email: String, password: String, nickname: String)

    suspend fun signInWithEmail(email: String, password: String)

    suspend fun signInWithGoogle(idToken: String)

    suspend fun signOut()

    suspend fun changeNickname(newNickname: String)
    fun isUserSignedIn(): Boolean

    suspend fun sendPasswordResetEmail(email: String)

    suspend fun updatePassword(newPassword: String)

    suspend fun deleteUser()

    suspend fun awaitAndGetUid(): String?
}