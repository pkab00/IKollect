package com.vbshkn.ikollect.data.local.model.pojo

import androidx.room.Embedded
import androidx.room.Junction
import androidx.room.Relation
import com.vbshkn.ikollect.data.local.model.entity.AlbumArtistCrossRef
import com.vbshkn.ikollect.data.local.model.entity.AlbumEntity
import com.vbshkn.ikollect.data.local.model.entity.ArtistEntity

data class ArtistWithAlbums(
    @Embedded
    val artist: ArtistEntity,
    @Relation(
        parentColumn = "artistId",
        entityColumn = "albumId",
        associateBy = Junction(AlbumArtistCrossRef::class)
    )
    val albums: List<AlbumEntity>
)