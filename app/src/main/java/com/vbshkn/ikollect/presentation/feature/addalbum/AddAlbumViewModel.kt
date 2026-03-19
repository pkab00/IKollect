package com.vbshkn.ikollect.presentation.feature.addalbum

import androidx.lifecycle.ViewModel
import com.vbshkn.ikollect.presentation.navigation.Route
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import javax.inject.Inject

@HiltViewModel
class AddAlbumViewModel @Inject constructor(

): ViewModel() {
    private val _uiState = MutableStateFlow(AddAlbumUIState())
    val uiState = _uiState.asStateFlow()

    fun canNavigateBack(currentRoute: Route.AddAlbumFlow): Boolean {
        return when(currentRoute) {
            Route.AddAlbumFlow.SeeInfo -> false
            else -> true
        }
    }

    fun canNavigateNext(currentRoute: Route.AddAlbumFlow): Boolean {
        return when(currentRoute) {
            Route.AddAlbumFlow.SeeInfo -> true
            Route.AddAlbumFlow.SelectVersion -> _uiState.value.versionCandidate != null
            Route.AddAlbumFlow.AddDetails -> true
        }
    }

    fun showDialog(dialogState: AddAlbumDialogState) {
        _uiState.update {
            it.copy(dialogState = dialogState)
        }
    }

    fun dismissDialog() {
        _uiState.update {
            it.copy(dialogState = AddAlbumDialogState.None)
        }
    }
}