package com.vbshkn.ikollect.data.local.model.entity

import androidx.compose.ui.graphics.Color
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class TagEntity (
    @PrimaryKey(autoGenerate = true)
    val tagId: Long = 0,
    val isSystemTag: Boolean = false,
    val tagName: String,
    val tagColor: Color
)