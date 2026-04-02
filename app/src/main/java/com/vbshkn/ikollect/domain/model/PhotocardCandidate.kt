package com.vbshkn.ikollect.domain.model

data class PhotocardCandidate(
    val imageUrl: String? = null,
    val owner: Artist? = null,
    val album: Album? = null,
    val depictedArtists: List<Artist> = emptyList(),
    val displayName: String = "",
    val isPob: Boolean = false,
    val userNote: String = ""
)
