package com.vbshkn.ikollect.domain.model

sealed class ArtistProfileData {
    abstract val artist: Artist
    abstract val albums: List<Album>
    abstract val photocards: List<Photocard>

    data class GroupProfile(
        override val artist: Artist,
        override val albums: List<Album>,
        override val photocards: List<Photocard>,
        val members: List<Artist>
    ) : ArtistProfileData()

    data class SoloistProfile(
        override val artist: Artist,
        override val albums: List<Album>,
        override val photocards: List<Photocard>,
        val groups: List<Artist>
    ) : ArtistProfileData()
}
