package com.vbshkn.ikollect.data.local.pojo

import androidx.room.Embedded
import androidx.room.Junction
import androidx.room.Relation
import com.vbshkn.ikollect.data.local.entity.AlbumArtistCrossRef
import com.vbshkn.ikollect.data.local.entity.AlbumEntity
import com.vbshkn.ikollect.data.local.entity.ArtistEntity

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