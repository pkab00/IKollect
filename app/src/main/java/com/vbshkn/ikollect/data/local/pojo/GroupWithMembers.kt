package com.vbshkn.ikollect.data.local.pojo

import androidx.room.Embedded
import androidx.room.Junction
import androidx.room.Relation
import com.vbshkn.ikollect.data.local.entity.ArtistArtistCrossRef
import com.vbshkn.ikollect.data.local.entity.ArtistEntity

data class GroupWithMembers(
    @Embedded val group: ArtistEntity,
    @Relation(
        parentColumn = "artistId",
        entityColumn = "artistId",
        associateBy = Junction(
            value = ArtistArtistCrossRef::class,
            parentColumn = "groupId",
            entityColumn = "memberId"
        )
    )
    val members: List<ArtistEntity>
)
