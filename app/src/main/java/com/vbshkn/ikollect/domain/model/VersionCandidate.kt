package com.vbshkn.ikollect.domain.model

import kotlinx.serialization.Serializable

@Serializable
data class VersionCandidate(
    val name: String,
    val coverImage: String?
)
