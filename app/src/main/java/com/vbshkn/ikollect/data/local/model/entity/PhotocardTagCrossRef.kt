package com.vbshkn.ikollect.data.local.model.entity

import androidx.room.Entity

@Entity(primaryKeys = ["photocardId", "tagId"])
data class PhotocardTagCrossRef(
    val photocardId: Long,
    val tagId: Long
)
