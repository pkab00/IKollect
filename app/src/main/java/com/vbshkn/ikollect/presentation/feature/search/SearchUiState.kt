package com.vbshkn.ikollect.presentation.feature.search

import com.vbshkn.ikollect.domain.error.AppError
import com.vbshkn.ikollect.domain.model.Searchable

data class  SearchUiState <T : Searchable> (
    val query: String = "",
    val results: List<T> = emptyList(),
    val isLoading: Boolean = false,
    val error: AppError? = null
)
