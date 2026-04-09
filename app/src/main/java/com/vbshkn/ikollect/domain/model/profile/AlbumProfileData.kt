package com.vbshkn.ikollect.domain.model.profile

import com.vbshkn.ikollect.domain.model.details.AlbumDetails
import com.vbshkn.ikollect.domain.model.list.PhotocardListItem

data class AlbumProfileData(
    val album: AlbumDetails,
    val photocards: List<PhotocardListItem>
)
