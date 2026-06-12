package com.vbshkn.ikollect.domain.model

import androidx.compose.ui.graphics.Color
import com.vbshkn.ikollect.util.UiText

data class TagItem(
    val id: Long = 0,
    val isSystem: Boolean,
    val name: UiText,
    val color: Color
)
