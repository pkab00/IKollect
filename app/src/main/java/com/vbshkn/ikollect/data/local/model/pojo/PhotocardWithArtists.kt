package com.vbshkn.ikollect.data.local.model.pojo

import androidx.room.Embedded
import androidx.room.Junction
import androidx.room.Relation
import com.vbshkn.ikollect.data.local.model.entity.ArtistEntity
import com.vbshkn.ikollect.data.local.model.entity.PhotocardArtistCrossRef
import com.vbshkn.ikollect.data.local.model.entity.PhotocardEntity
import com.vbshkn.ikollect.data.local.model.entity.PhotocardTagCrossRef
import com.vbshkn.ikollect.data.local.model.entity.TagEntity

data class PhotocardWithArtists(
    @Embedded
    val photocard: PhotocardEntity,
    @Relation(
        entity = ArtistEntity::class,
        parentColumn = "ownerId",
        entityColumn = "artistId"
    )
    val owner: ArtistEntity,
    @Relation(
        entity = TagEntity::class,
        parentColumn = "photocardId",
        entityColumn = "tagId",
        associateBy = Junction(PhotocardTagCrossRef::class)
    )
    val tags: List<TagEntity>
)