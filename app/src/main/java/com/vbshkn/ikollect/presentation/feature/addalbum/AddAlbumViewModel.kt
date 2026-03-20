package com.vbshkn.ikollect.presentation.feature.addalbum

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.toRoute
import com.vbshkn.ikollect.domain.model.AlbumCandidate
import com.vbshkn.ikollect.domain.model.VersionCandidate
import com.vbshkn.ikollect.presentation.navigation.AlbumCandidateType
import com.vbshkn.ikollect.presentation.navigation.Route
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject
import kotlin.reflect.typeOf

@HiltViewModel
class AddAlbumViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle
): ViewModel() {
    private val args = savedStateHandle.toRoute<Route.AddAlbumRoute>(
        typeMap = mapOf(typeOf<AlbumCandidate>() to AlbumCandidateType)
    )
    private val _uiState = MutableStateFlow(AddAlbumUIState(albumCandidate = args.candidate))
    val uiState = _uiState.asStateFlow()

    fun updateVersionCandidate(candidate: VersionCandidate) {
        _uiState.update {
            it.copy(versionCandidate = candidate)
        }
    }

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