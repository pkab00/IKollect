package com.vbshkn.ikollect.presentation.feature.camera.barcode_scanner

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.vbshkn.ikollect.R
import com.vbshkn.ikollect.data.remote.NetworkResult
import com.vbshkn.ikollect.data.repository.AlbumRepository
import com.vbshkn.ikollect.domain.error.AppError
import com.vbshkn.ikollect.domain.model.candidate.AlbumCandidate
import com.vbshkn.ikollect.util.UiText
import dagger.hilt.android.lifecycle.HiltViewModel
import jakarta.inject.Inject
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

@HiltViewModel
class BarcodeScannerViewModel @Inject constructor(
    private val albumRepository: AlbumRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(BarcodeScannerUiState())
    val uiState = _uiState.asStateFlow()

    init {
        observeAlbumCandidate()
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    private fun observeAlbumCandidate() = viewModelScope.launch {
        _uiState
            .map { it.barcode }
            .filterNotNull()
            .flatMapLatest { code ->
                albumRepository.getAlbumCandidate(code)
            }
            .collect { result ->
                when (result) {
                    is NetworkResult.Error -> {
                        _uiState.update { it.copy(
                            isLoading = false,
                            dialogState = BarcodeScannerDialogState.ErrorDialog(handleErrors(result.error))
                        ) }
                    }
                    is NetworkResult.Loading -> {
                        _uiState.update { it.copy(isLoading = true) }
                    }
                    is NetworkResult.Success -> {
                        _uiState.update { it.copy(
                            isLoading = false,
                            albumCandidate = result.data,
                            dialogState = BarcodeScannerDialogState.SuccessDialog
                        ) }
                    }
                }
            }
    }

    fun onBarcodeRecognized(barcode: String) {
        _uiState.update { it.copy(barcode = barcode) }
    }

    fun updateDialogState(dialogState: BarcodeScannerDialogState) {
        _uiState.update { it.copy(dialogState = dialogState) }
    }

    fun reset() {
        _uiState.update { it.copy(barcode = null, albumCandidate = null) }
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