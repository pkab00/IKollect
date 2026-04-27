package com.vbshkn.ikollect.data.repository

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.vbshkn.ikollect.data.remote.NetworkResult
import com.vbshkn.ikollect.domain.model.AppUser
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

private const val TAG = "AuthRepository"

class AuthRepository @Inject constructor(
    private val auth: FirebaseAuth
) {
    fun getUser(): Flow<NetworkResult<AppUser?>> = callbackFlow {
        val authStateListener = FirebaseAuth.AuthStateListener { firebaseAuth ->
            trySend(NetworkResult.Loading)
            trySend(NetworkResult.Success(firebaseAuth.currentUser?.let {
                AppUser(
                    uid = it.uid,
                    email = it.email ?: "",
                    username = it.displayName,
                    profilePictureUrl = it.photoUrl?.toString()
                )
            }))
        }
        auth.addAuthStateListener(authStateListener)

        awaitClose {
            auth.removeAuthStateListener(authStateListener)
        }
    }

    suspend fun createUser(
        email: String,
        password: String
    ) {
        auth.createUserWithEmailAndPassword(email, password).await()
    }

    suspend fun signInWithEmail(
        email: String,
        password: String
    ) {
        auth.signInWithEmailAndPassword(email, password).await()
    }

    suspend fun signInWithGoogle(idToken: String) {
        val credential = GoogleAuthProvider.getCredential(idToken, null)
        auth.signInWithCredential(credential).await()
    }

    fun signOut() {
        auth.signOut()
    }

    fun isUserSignedIn(): Boolean {
        return auth.currentUser != null
    }

    fun sendPasswordResetEmail(
        email: String,
        onSuccess: () -> Unit,
        onFailure: (Exception) -> Unit
    ) {
        auth.sendPasswordResetEmail(email)
            .addOnCompleteListener { task ->
                android.util.Log.d(TAG, "sendPasswordResetEmail: ${task.isSuccessful}")
                if (task.isSuccessful) {
                    onSuccess()
                } else {
                    onFailure(task.exception ?: Exception("Unknown error during password reset"))
                }
            }
    }

    fun updatePassword(
        newPassword: String,
        onSuccess: () -> Unit,
        onFailure: (Exception) -> Unit
    ) {
        val user = auth.currentUser
        if (user != null) {
            user.updatePassword(newPassword)
                .addOnCompleteListener { task ->
                    android.util.Log.d(TAG, "updatePassword: ${task.isSuccessful}")
                    if (task.isSuccessful) {
                        onSuccess()
                    } else {
                        onFailure(
                            task.exception ?: Exception("Unknown error during password update")
                        )
                    }
                }
        } else {
            onFailure(Exception("No user is currently signed in"))
        }
    }

    fun deleteUser(
        onSuccess: () -> Unit,
        onFailure: (Exception) -> Unit
    ) {
        val user = auth.currentUser
        if (user != null) {
            user.delete()
                .addOnCompleteListener { task ->
                    android.util.Log.d(TAG, "deleteUser: ${task.isSuccessful}")
                    if (task.isSuccessful) {
                        onSuccess()
                    } else {
                        onFailure(task.exception ?: Exception("Unknown error during user deletion"))
                    }
                }
        } else {
            onFailure(Exception("No user is currently signed in"))
        }
    }
}