package com.vbshkn.ikollect.domain.model

data class AppUser(
    val uid: String,
    val email: String,
    val username: String? = null,
    val profilePictureUrl: String? = null
)
