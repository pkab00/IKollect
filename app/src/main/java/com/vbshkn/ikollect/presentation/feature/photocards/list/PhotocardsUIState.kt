package com.vbshkn.ikollect.presentation.feature.photocards.list

import com.vbshkn.ikollect.data.AppError
import com.vbshkn.ikollect.domain.model.Photocard

data class PhotocardsUIState(
    val isLoading: Boolean = false,
    val error: AppError? = null,
    val photocards: List<Photocard> = emptyList()
)
