package com.vbshkn.ikollect.presentation.feature.albums.list

import androidx.compose.runtime.remember
import androidx.lifecycle.viewModelScope
import com.vbshkn.ikollect.R
import com.vbshkn.ikollect.data.AppError
import com.vbshkn.ikollect.data.remote.NetworkResult
import com.vbshkn.ikollect.domain.usecase.GetAllAlbumsUseCase
import com.vbshkn.ikollect.domain.usecase.ScanAlbumBarcodeUseCase
import com.vbshkn.ikollect.domain.base.BaseViewModel
import com.vbshkn.ikollect.util.UiText
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.onCompletion
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AlbumsViewModel @Inject constructor(
    private val getAllAlbumsUseCase: GetAllAlbumsUseCase,
    private val scanAlbumBarcodeUseCase: ScanAlbumBarcodeUseCase,
) : BaseViewModel<
        AlbumsUIState,
        AlbumsContract.Event,
        AlbumsContract.Effect
        >(initialState = AlbumsUIState()) {

    init {
        collectAlbums()
    }

    override fun onEvent(event: AlbumsContract.Event) {
        when (event) {
            is AlbumsContract.Event.OnAlbumClicked -> {
                sendEffect(AlbumsContract.Effect.NavigateToAlbum(event.id))
            }

            is AlbumsContract.Event.OnStartScanningClicked -> {
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
                        showDialog(AlbumsDialogState.ScanningResultDialog("$artists - $release"))
                    }
                    else {
                        val e = uiState.value.error ?: return@invokeOnCompletion
                        showDialog(AlbumsDialogState.ScanningErrorDialog(handleErrors(e)))
                    }
                }
            }

            AlbumsContract.Event.OnAlbumSavingConfirmed -> {
                val candidate = uiState.value.scannedCandidate
                if (candidate != null) {
                    dismissDialog()
                    sendEffect(AlbumsContract.Effect.NavigateToSaveFlow(candidate))
                }
            }

            AlbumsContract.Event.OnDismissDialogClicked -> {
                dismissDialog()
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
        updateState { it.copy(dialogState = AlbumsDialogState.None, scannedCandidate = null) }
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