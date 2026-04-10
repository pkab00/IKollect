package com.vbshkn.ikollect.data.local.model.pojo

import androidx.room.Embedded
import androidx.room.Junction
import androidx.room.Relation
import com.vbshkn.ikollect.data.local.model.entity.AlbumEntity
import com.vbshkn.ikollect.data.local.model.entity.ArtistEntity
import com.vbshkn.ikollect.data.local.model.entity.PhotocardArtistCrossRef

data class PhotocardFullDetail(
    @Embedded
    val photocard: PhotocardMinimalDetail,
    @Relation(
        entity = AlbumEntity::class,
        parentColumn = "albumId",
        entityColumn = "albumId"
    )
    val album: AlbumWithArtists?,
    @Relation(
        entity = ArtistEntity::class,
        parentColumn = "photocardId",
        entityColumn = "artistId",
        associateBy = Junction(PhotocardArtistCrossRef::class)
    )
    val artists: List<ArtistEntity>
)
