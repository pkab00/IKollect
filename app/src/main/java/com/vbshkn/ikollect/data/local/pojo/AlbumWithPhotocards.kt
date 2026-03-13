package com.vbshkn.ikollect.data.local.pojo

import androidx.room.Embedded
import androidx.room.Relation
import com.vbshkn.ikollect.data.local.entity.AlbumEntity
import com.vbshkn.ikollect.data.local.entity.PhotocardEntity

data class AlbumWithPhotocards(
    @Embedded
    val album: AlbumEntity,
    @Relation(
        parentColumn = "albumId",
        entityColumn = "photocardId"
    )
    val photocards: List<PhotocardEntity>
)
