package com.vbshkn.ikollect.data.repository

import com.vbshkn.ikollect.data.remote.NetworkResult
import com.vbshkn.ikollect.domain.model.AppUser
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.auth.auth
import io.github.jan.supabase.auth.providers.Google
import io.github.jan.supabase.auth.providers.builtin.Email
import io.github.jan.supabase.auth.providers.builtin.IDToken
import io.github.jan.supabase.auth.status.SessionStatus
import io.github.jan.supabase.postgrest.from
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flow
import kotlinx.serialization.Serializable
import javax.inject.Inject

private const val TAG = "AuthRepository"
private const val PROFILES_TABLE = "profiles"

class AuthRepository @Inject constructor(
    private val supabase: SupabaseClient
) {
    private val auth = supabase.auth
    private val postgrest = supabase.from(PROFILES_TABLE)

    @OptIn(ExperimentalCoroutinesApi::class)
    fun getUser(): Flow<NetworkResult<AppUser?>> = auth.sessionStatus
        .flatMapLatest { status ->
            flow {
                emit(NetworkResult.Loading)
                when (status) {
                    is SessionStatus.Authenticated -> {
                        val user = status.session.user
                        if (user != null) {
                            try {
                                val profile = postgrest
                                    .select { filter { eq("id", user.id) } }
                                    .decodeSingleOrNull<ProfileRemote>()

                                emit(NetworkResult.Success(AppUser(
                                    uid = user.id,
                                    email = user.email ?: "",
                                    username = profile?.nickname,
                                    profilePictureUrl = null
                                )))
                            } catch (e: Exception) {
                                emit(NetworkResult.Success(AppUser(
                                    uid = user.id,
                                    email = user.email ?: "",
                                    username = null,
                                    profilePictureUrl = null
                                )))
                            }
                        } else {
                            emit(NetworkResult.Success(null))
                        }
                    }
                    else -> emit(NetworkResult.Success(null))
                }
            }
        }

    suspend fun createUser(email: String, password: String, nickname: String) {
        auth.signUpWith(Email) {
            this.email = email
            this.password = password
        }

        val userId = auth.currentUserOrNull()?.id
        if (userId != null) {
            postgrest.insert(ProfileRemote(id = userId, nickname = nickname))
        }
    }

    suspend fun signInWithEmail(email: String, password: String) {
        auth.signInWith(Email) {
            this.email = email
            this.password = password
        }
    }

    suspend fun signInWithGoogle(idToken: String) {
        auth.signInWith(IDToken) {
            this.idToken = idToken
            provider = Google
        }
    }

    suspend fun signOut() {
        auth.signOut()
    }

    fun isUserSignedIn(): Boolean {
        return auth.currentSessionOrNull() != null
    }

    suspend fun sendPasswordResetEmail(email: String) {
        auth.resetPasswordForEmail(email)
    }

    suspend fun updatePassword(newPassword: String) {
        auth.updateUser {
            password = newPassword
        }
    }

    suspend fun deleteUser() {
        val userId = auth.currentUserOrNull()?.id
        if (userId != null) {
            postgrest.delete { filter { eq("id", userId) } }
            // Для полного удаления аккаунта (Auth) потребуется админский доступ или Edge Function
        }
    }

    suspend fun awaitAndGetUid(): String? {
        auth.awaitInitialization()
        return auth.currentSessionOrNull()?.user?.id
    }
}

@Serializable
data class ProfileRemote(
    val id: String,
    val nickname: String? = null
)