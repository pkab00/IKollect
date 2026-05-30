package com.vbshkn.ikollect.domain.model.candidate

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import kotlinx.serialization.Serializable

@Serializable
@Parcelize
data class ArtistCandidate(
    val artistId: Long,
    val name: String,
    val isGroup: Boolean,
    val isFavorite: Boolean,
    val profileImage: String?,
    val memberIds: List<Long>
) : Parcelable