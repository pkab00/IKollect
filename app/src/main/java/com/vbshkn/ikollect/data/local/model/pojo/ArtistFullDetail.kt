package com.vbshkn.ikollect.data.local.model.pojo

import androidx.room.Embedded
import androidx.room.Junction
import androidx.room.Relation
import com.vbshkn.ikollect.data.local.model.entity.AlbumArtistCrossRef
import com.vbshkn.ikollect.data.local.model.entity.AlbumEntity
import com.vbshkn.ikollect.data.local.model.entity.ArtistArtistCrossRef
import com.vbshkn.ikollect.data.local.model.entity.ArtistEntity
import com.vbshkn.ikollect.data.local.model.entity.PhotocardArtistCrossRef
import com.vbshkn.ikollect.data.local.model.entity.PhotocardEntity

data class ArtistFullDetail(
    @Embedded
    val artist: ArtistEntity,
    @Relation(
        entity = AlbumEntity::class,
        parentColumn = "artistId",
        entityColumn = "albumId",
        associateBy = Junction(AlbumArtistCrossRef::class)
    )
    val albums: List<AlbumWithArtists>,
    @Relation(
        entity = PhotocardEntity::class,
        parentColumn = "artistId",
        entityColumn = "photocardId",
        associateBy = Junction(PhotocardArtistCrossRef::class)
    )
    val photocards: List<PhotocardWithArtists>,
    @Relation(
        entity = ArtistEntity::class,
        parentColumn = "artistId",
        entityColumn = "artistId",
        associateBy = Junction(
            value = ArtistArtistCrossRef::class,
            parentColumn = "groupId",
            entityColumn = "memberId"
        )
    )
    val members: List<ArtistEntity>,
    @Relation(
        entity = ArtistEntity::class,
        parentColumn = "artistId",
        entityColumn = "artistId",
        associateBy = Junction(
            value = ArtistArtistCrossRef::class,
            parentColumn = "memberId",
            entityColumn = "groupId"
        )
    )
    val groups: List<ArtistEntity>
)
