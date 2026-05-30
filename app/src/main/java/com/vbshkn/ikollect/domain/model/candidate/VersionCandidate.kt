package com.vbshkn.ikollect.domain.model.candidate

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import kotlinx.serialization.Serializable

@Serializable
@Parcelize
data class VersionCandidate(
    val name: String,
    val coverImage: String?
) : Parcelable