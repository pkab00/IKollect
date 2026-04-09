package com.vbshkn.ikollect.data.local.model.pojo

import androidx.room.Embedded
import androidx.room.Junction
import androidx.room.Relation
import com.vbshkn.ikollect.data.local.model.entity.AlbumArtistCrossRef
import com.vbshkn.ikollect.data.local.model.entity.AlbumEntity
import com.vbshkn.ikollect.data.local.model.entity.ArtistEntity
import com.vbshkn.ikollect.data.local.model.entity.PhotocardEntity

data class AlbumFullDetail(
    @Embedded
    val album: AlbumWithArtists,
    @Relation(
        entity = PhotocardEntity::class,
        parentColumn = "albumId",
        entityColumn = "photocardId",
    )
    val photocards: List<PhotocardWithArtists>
)
