package com.vbshkn.ikollect.domain.model.profile

import com.vbshkn.ikollect.domain.model.details.AlbumDetails
import com.vbshkn.ikollect.domain.model.details.PhotocardDetails
import com.vbshkn.ikollect.domain.model.list.ArtistListItem

data class PhotocardProfileData (
    val photocard: PhotocardDetails,
    val album: AlbumDetails?,
    val depictedArtists: List<ArtistListItem>
)