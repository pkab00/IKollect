package com.vbshkn.ikollect.data.local.model.pojo

import androidx.room.Embedded
import androidx.room.Junction
import androidx.room.Relation
import com.vbshkn.ikollect.data.local.model.entity.AlbumArtistCrossRef
import com.vbshkn.ikollect.data.local.model.entity.AlbumEntity
import com.vbshkn.ikollect.data.local.model.entity.ArtistEntity

data class AlbumWithArtists(
    @Embedded
    val album: AlbumEntity,
    @Relation(
        parentColumn = "albumId",
        entityColumn = "artistId",
        associateBy = Junction(AlbumArtistCrossRef::class)
    )
    val artists: List<ArtistEntity>
)