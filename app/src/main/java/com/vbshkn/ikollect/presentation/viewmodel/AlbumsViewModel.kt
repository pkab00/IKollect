package com.vbshkn.ikollect.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.vbshkn.ikollect.data.remote.NetworkResult
import com.vbshkn.ikollect.domain.usecase.ScanAlbumBarcodeUseCase
import com.vbshkn.ikollect.presentation.contract.AlbumsScreenContract
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AlbumsViewModel @Inject constructor(
    private val scanAlbumBarcodeUseCase: ScanAlbumBarcodeUseCase
): ViewModel() {
    private val _effects = Channel<AlbumsScreenContract.Effect>(Channel.BUFFERED)
    val effects = _effects.receiveAsFlow()

    fun onEvent(event: AlbumsScreenContract.Event) {
        when(event) {
            is AlbumsScreenContract.Event.OnAlbumClicked -> {
                sendEffect(AlbumsScreenContract.Effect.NavigateToAlbum(event.id))
            }
            is AlbumsScreenContract.Event.OnStartScanningClicked -> viewModelScope.launch {
                scanAlbumBarcodeUseCase().collect { result ->
                    when(result) {
                        is NetworkResult.Loading -> {
                            android.util.Log.d("BARCODE", "Processing...")
                        }
                        is NetworkResult.Success -> {
                            android.util.Log.d("BARCODE", "Scanned album: ${result.data}")
                        }
                        is NetworkResult.Error -> {
                            android.util.Log.d("BARCODE", "Scanning error: ${result.message}")
                        }
                    }
                }
            }
        }
    }

    private fun sendEffect(effect: AlbumsScreenContract.Effect) {
        viewModelScope.launch {
            _effects.send(effect)
        }
    }
}