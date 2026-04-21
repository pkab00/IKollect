package com.vbshkn.ikollect.data.repository

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import javax.inject.Inject

private const val TAG = "AuthRepository"

class AuthRepository @Inject constructor(
    private val auth: FirebaseAuth
) {
    fun getUser(): FirebaseUser? {
        return auth.currentUser
    }

    fun createUser(
        email: String,
        password: String,
        onSuccess: (FirebaseUser) -> Unit,
        onFailure: (Exception) -> Unit
    ) {
        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                android.util.Log.d(TAG, "createUser: ${task.isSuccessful}")
                if (task.isSuccessful) {
                    val user = auth.currentUser
                    if (user != null) {
                        onSuccess(user)
                    } else {
                        onFailure(Exception("User is null after successful creation"))
                    }
                } else {
                    onFailure(task.exception ?: Exception("Unknown error during user creation"))
                }
            }
    }

    fun signIn(
        email: String,
        password: String,
        onSuccess: (FirebaseUser) -> Unit,
        onFailure: (Exception) -> Unit
    ) {
        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                android.util.Log.d(TAG, "signIn: ${task.isSuccessful}")
                if (task.isSuccessful) {
                    val user = auth.currentUser
                    if (user != null) {
                        onSuccess(user)
                    } else {
                        onFailure(Exception("User is null after successful sign-in"))
                    }
                } else {
                    onFailure(task.exception ?: Exception("Unknown error during sign-in"))
                }
            }
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
                        onFailure(task.exception ?: Exception("Unknown error during password update"))
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