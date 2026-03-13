package com.vbshkn.ikollect.data.local.pojo

import androidx.room.Embedded
import androidx.room.Junction
import androidx.room.Relation
import com.vbshkn.ikollect.data.local.entity.AlbumArtistCrossRef
import com.vbshkn.ikollect.data.local.entity.AlbumEntity
import com.vbshkn.ikollect.data.local.entity.ArtistEntity
import com.vbshkn.ikollect.data.local.entity.PhotocardEntity

data class AlbumFullDetail(
    @Embedded
    val album: AlbumEntity,
    @Relation(
        parentColumn = "albumId",
        entityColumn = "artistId",
        associateBy = Junction(AlbumArtistCrossRef::class)
    )
    val artists: List<ArtistEntity>,
    @Relation(
        parentColumn = "albumId",
        entityColumn = "photocardId"
    )
    val photocards: List<PhotocardEntity>
)
