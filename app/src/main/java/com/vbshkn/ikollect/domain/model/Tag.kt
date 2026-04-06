package com.vbshkn.ikollect.domain.model

import androidx.compose.ui.graphics.Color
import com.vbshkn.ikollect.util.UiText

data class Tag(
    val id: Long,
    val isSystem: Boolean,
    val name: UiText,
    val color: Color
)
