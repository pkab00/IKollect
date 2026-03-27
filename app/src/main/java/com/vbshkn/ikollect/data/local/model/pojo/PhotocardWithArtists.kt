package com.vbshkn.ikollect.data.local.model.pojo

import androidx.room.Embedded
import androidx.room.Junction
import androidx.room.Relation
import com.vbshkn.ikollect.data.local.model.entity.ArtistEntity
import com.vbshkn.ikollect.data.local.model.entity.PhotocardArtistCrossRef
import com.vbshkn.ikollect.data.local.model.entity.PhotocardEntity

data class PhotocardWithArtists(
    @Embedded
    val photocard: PhotocardEntity,
    @Relation(
        parentColumn = "photocardId",
        entityColumn = "artistId",
        associateBy = Junction(PhotocardArtistCrossRef::class)
    )
    val artists: List<ArtistEntity>
)