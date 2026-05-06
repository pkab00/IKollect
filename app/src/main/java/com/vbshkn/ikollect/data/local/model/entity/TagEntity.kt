package com.vbshkn.ikollect.data.local.model.entity

import androidx.compose.ui.graphics.Color
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlin.time.Instant

@Entity
data class TagEntity (
    @PrimaryKey(autoGenerate = true)
    val tagId: Long = 0,
    val isSystemTag: Boolean = false,
    val tagName: String,
    val tagColor: Color,
    val isSynchronized: Boolean = false,
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis(),
    val isDeleted: Boolean = false
)