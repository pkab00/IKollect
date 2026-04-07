package com.vbshkn.ikollect.domain.model.profile

import com.vbshkn.ikollect.domain.model.list.PhotocardListItem
import com.vbshkn.ikollect.domain.model.details.AlbumDetails
import com.vbshkn.ikollect.domain.model.list.ArtistListItem

sealed class ArtistProfileData {
    abstract val artist: ArtistListItem
    abstract val albums: List<AlbumDetails>
    abstract val photocards: List<PhotocardListItem>

    data class GroupProfile(
        override val artist: ArtistListItem,
        override val albums: List<AlbumDetails>,
        override val photocards: List<PhotocardListItem>,
        val members: List<ArtistListItem>
    ) : ArtistProfileData()

    data class SoloistProfile(
        override val artist: ArtistListItem,
        override val albums: List<AlbumDetails>,
        override val photocards: List<PhotocardListItem>,
        val groups: List<ArtistListItem>
    ) : ArtistProfileData()
}