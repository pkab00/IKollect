package com.vbshkn.ikollect.presentation.feature.albums.list

import androidx.lifecycle.viewModelScope
import com.vbshkn.ikollect.presentation.feature.albums.list.AlbumsContract.Event
import com.vbshkn.ikollect.presentation.feature.albums.list.AlbumsContract.Effect
import com.vbshkn.ikollect.domain.usecase.get.GetAllAlbumsUseCase
import com.vbshkn.ikollect.domain.base.BaseViewModel
import com.vbshkn.ikollect.domain.usecase.RefreshDataUseCase
import com.vbshkn.ikollect.presentation.feature.albums.list.AlbumsContract.Effect.*
import com.vbshkn.ikollect.presentation.feature.albums.list.AlbumsDialogState.*
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AlbumsViewModel @Inject constructor(
    private val getAllAlbumsUseCase: GetAllAlbumsUseCase,
    private val refreshDataUseCase: RefreshDataUseCase
) : BaseViewModel<AlbumsUIState, Event, Effect>(initialState = AlbumsUIState()) {

    init {
        collectAlbums()
    }

    override fun onEvent(event: Event) {
        when (event) {
            is Event.OnAlbumClicked -> {
                sendEffect(NavigateToAlbum(event.id))
            }

            is Event.OnScanningClicked -> {
                sendEffect(TryOpenBarcodeScanner)
            }

            is Event.OnSearchClicked -> {
                sendEffect(NavigateToSearch)
            }

            is Event.OnDismissDialogClicked -> {
                dismissDialog()
            }

            is Event.OnPulledToSync -> viewModelScope.launch {
                updateState { it.copy(isSyncing = true) }
                val succeed = refreshDataUseCase()
                if (!succeed) sendEffect(ShowRefreshingErrorToast)
                updateState { it.copy(isSyncing = false) }
            }

            is  Event.OnShowCameraRationale -> {
                updateState { it.copy(dialogState = CameraRationaleDialog) }
            }
        }
    }

    private fun collectAlbums() = collectFlowIntoState(
        flow = getAllAlbumsUseCase(),
        onSuccess = { state, data -> state.copy(isLoading = false, albums = data) },
        onLoading = { state -> state.copy(isLoading = true) },
        onError = { state, e -> state.copy(isLoading = false, error = e) }
    )

    private fun dismissDialog() {
        updateState { it.copy(dialogState = None, scannedCandidate = null) }
    }
}