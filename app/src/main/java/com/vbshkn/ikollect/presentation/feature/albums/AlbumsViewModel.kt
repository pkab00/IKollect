package com.vbshkn.ikollect.presentation.feature.albums

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.vbshkn.ikollect.R
import com.vbshkn.ikollect.data.AppError
import com.vbshkn.ikollect.data.remote.NetworkResult
import com.vbshkn.ikollect.domain.usecase.LoadAllAlbumsUseCase
import com.vbshkn.ikollect.domain.usecase.ScanAlbumBarcodeUseCase
import com.vbshkn.ikollect.presentation.feature.albums.AlbumsContract.Effect.*
import com.vbshkn.ikollect.presentation.feature.albums.AlbumsDialogState.*
import com.vbshkn.ikollect.util.UiText
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.onCompletion
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AlbumsViewModel @Inject constructor(
    private val loadAllAlbumsUseCase: LoadAllAlbumsUseCase,
    private val scanAlbumBarcodeUseCase: ScanAlbumBarcodeUseCase,
): ViewModel() {
    private val _uiState = MutableStateFlow(AlbumsUIState())
    val uiState = _uiState.asStateFlow()
    private val _effects = Channel<AlbumsContract.Effect>(Channel.BUFFERED)
    val effects = _effects.receiveAsFlow()

    init {
        viewModelScope.launch {
            collectAlbums()
        }
    }

    fun onEvent(event: AlbumsContract.Event) {
        when(event) {
            is AlbumsContract.Event.OnAlbumClicked -> {
                sendEffect(NavigateToAlbum(event.id))
            }
            is AlbumsContract.Event.OnStartScanningClicked -> viewModelScope.launch {
                scanAlbumBarcodeUseCase()
                    .onCompletion {
                        _uiState.update { it.copy(isLoading = false) }
                    }
                    .collect { result ->
                    when(result) {
                        is NetworkResult.Loading -> {
                            _uiState.update { it.copy(isLoading = true) }
                        }
                        is NetworkResult.Success -> {
                            _uiState.update {
                                it.copy(scannedCandidate = result.data)
                            }
                            showDialog(
                                ScanningResultDialog(
                                    "${result.data.artists.joinToString { it.name }} - ${result.data.name}")
                            )
                        }
                        is NetworkResult.Error -> {
                            showDialog(
                                ScanningErrorDialog(
                                    handleErrors(result.error)
                                )
                            )
                        }
                    }
                }
            }
            AlbumsContract.Event.OnAlbumSavingConfirmed -> {
                val candidate = uiState.value.scannedCandidate
                if (candidate != null) {
                    dismissDialog()
                    sendEffect(NavigateToSaveFlow(candidate))
                }
            }
            AlbumsContract.Event.OnDismissDialogClicked -> {
                dismissDialog()
            }
        }
    }

    private suspend fun collectAlbums() {
        loadAllAlbumsUseCase().collect { result ->
            when(result) {
                is NetworkResult.Loading -> _uiState.update {
                        it.copy(isLoading = true)
                    }
                is NetworkResult.Error -> _uiState.update {
                    it.copy(
                        isLoading = false,
                        error = result.error
                    )
                }
                is NetworkResult.Success -> _uiState.update {
                    it.copy(
                        isLoading = false,
                        albums = result.data
                    )
                }
            }
        }
    }

    private fun sendEffect(effect: AlbumsContract.Effect) {
        viewModelScope.launch {
            _effects.send(effect)
        }
    }

    private fun showDialog(dialogState: AlbumsDialogState) {
        _uiState.update { it.copy(dialogState = dialogState) }
    }

    private fun dismissDialog() {
        _uiState.update {
            it.copy(
                dialogState = None,
                scannedCandidate = null
            )
        }
    }

    private fun handleErrors(error: AppError): UiText {
        return when(error) {
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