package com.vbshkn.ikollect.presentation.feature.albums.list

import androidx.lifecycle.viewModelScope
import com.vbshkn.ikollect.presentation.feature.albums.list.AlbumsContract.Event
import com.vbshkn.ikollect.presentation.feature.albums.list.AlbumsContract.Effect
import com.vbshkn.ikollect.R
import com.vbshkn.ikollect.domain.error.AppError
import com.vbshkn.ikollect.domain.usecase.get.GetAllAlbumsUseCase
import com.vbshkn.ikollect.domain.usecase.ScanAlbumBarcodeUseCase
import com.vbshkn.ikollect.domain.base.BaseViewModel
import com.vbshkn.ikollect.domain.usecase.RefreshDataUseCase
import com.vbshkn.ikollect.presentation.feature.albums.list.AlbumsContract.Effect.*
import com.vbshkn.ikollect.presentation.feature.albums.list.AlbumsDialogState.*
import com.vbshkn.ikollect.util.UiText
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.onCompletion
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AlbumsViewModel @Inject constructor(
    private val getAllAlbumsUseCase: GetAllAlbumsUseCase,
    private val scanAlbumBarcodeUseCase: ScanAlbumBarcodeUseCase,
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

            is Event.OnStartScanningClicked -> {
                collectFlowIntoState(
                    flow = scanAlbumBarcodeUseCase()
                        .onCompletion { updateState { it.copy(isLoading = false) } },
                    onSuccess = { state, data -> state.copy(scannedCandidate = data) },
                    onLoading = { state -> state.copy(isLoading = true) },
                    onError = { state, e -> state.copy(isLoading = false, error = e) }
                ).invokeOnCompletion {
                    if (uiState.value.scannedCandidate != null) {
                        val artists = uiState.value.scannedCandidate?.artistCandidates?.joinToString { it.name }
                        val release = uiState.value.scannedCandidate?.name
                        showDialog(ScanningResultDialog("$artists - $release"))
                    }
                    else {
                        val e = uiState.value.error ?: return@invokeOnCompletion
                        showDialog(ScanningErrorDialog(handleErrors(e)))
                    }
                }
            }

            is Event.OnSearchClicked -> {
                sendEffect(NavigateToSearch)
            }

            is Event.OnAlbumSavingConfirmed -> {
                val candidate = uiState.value.scannedCandidate
                if (candidate != null) {
                    dismissDialog()
                    sendEffect(NavigateToSaveFlow(candidate))
                }
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
        }
    }

    private fun collectAlbums() = collectFlowIntoState(
        flow = getAllAlbumsUseCase(),
        onSuccess = { state, data -> state.copy(isLoading = false, albums = data) },
        onLoading = { state -> state.copy(isLoading = true) },
        onError = { state, e -> state.copy(isLoading = false, error = e) }
    )

    private fun showDialog(dialogState: AlbumsDialogState) {
        updateState { it.copy(dialogState = dialogState) }
    }

    private fun dismissDialog() {
        updateState { it.copy(dialogState = None, scannedCandidate = null) }
    }

    private fun handleErrors(error: AppError): UiText {
        return when (error) {
            AppError.ConnectionFailed -> {
                UiText.StringResource(R.string.error_body_connection_failed)
            }

            AppError.InvalidAlbumStyle -> {
                UiText.StringResource(R.string.error_body_invalid_album_style)
            }

            AppError.ReleaseNotFound -> {
                UiText.StringResource(R.string.error_body_release_not_found)
            }

            AppError.ScanningFailed -> {
                UiText.StringResource(R.string.error_body_release_not_found)
            }

            else -> {
                UiText.StringResource(R.string.error_body_unknown)
            }
        }
    }
}