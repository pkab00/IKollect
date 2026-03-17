package com.vbshkn.ikollect.domain.usecase

import com.vbshkn.ikollect.data.remote.NetworkResult
import com.vbshkn.ikollect.data.repository.AlbumRepository
import com.vbshkn.ikollect.data.service.BarcodeScannerService
import com.vbshkn.ikollect.domain.model.AlbumCandidate
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

class ScanAlbumBarcodeUseCase @Inject constructor(
    private val barcodeScannerService: BarcodeScannerService,
    private val albumRepository: AlbumRepository
) {
    operator fun invoke(): Flow<NetworkResult<AlbumCandidate>> = callbackFlow {
        trySend(NetworkResult.Loading)
        barcodeScannerService.startScanning(
            onSuccess = { code ->
                launch {
                    albumRepository.getAlbumCandidate(code).collect { result ->
                        // тут в будущем будет проверка на то, является ли альбом к-поп или нет
                        trySend(result)
                        if (result !is NetworkResult.Loading) close()
                    }
                }
            },
            onFailure = { e ->
                trySend(NetworkResult.Error(message = "SCANNING ERROR: ${e.localizedMessage}"))
                close()
            }
        )
        awaitClose()
    }
}