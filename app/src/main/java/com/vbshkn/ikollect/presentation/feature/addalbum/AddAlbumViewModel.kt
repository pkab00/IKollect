package com.vbshkn.ikollect.presentation.feature.addalbum

import android.content.Context
import android.net.Uri
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.toRoute
import com.vbshkn.ikollect.domain.model.AlbumCandidate
import com.vbshkn.ikollect.presentation.navigation.AlbumCandidateType
import com.vbshkn.ikollect.presentation.navigation.Route
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject
import kotlin.reflect.typeOf
import androidx.core.net.toUri
import java.io.File

@HiltViewModel
class AddAlbumViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle
) : ViewModel() {
    private val args = savedStateHandle.toRoute<Route.AddAlbumRoute>(
        typeMap = mapOf(typeOf<AlbumCandidate>() to AlbumCandidateType)
    )
    private val _uiState = MutableStateFlow(AddAlbumUIState(albumCandidate = args.candidate))
    val uiState = _uiState.asStateFlow()
    private val _effects = Channel<AddAlbumContract.Effect>(Channel.BUFFERED)
    val effects = _effects.receiveAsFlow()


    fun onEvent(event: AddAlbumContract.Event) {
        when (event) {
            is AddAlbumContract.Event.OnBackClicked -> {
                sendEffect(AddAlbumContract.Effect.NavigateBack)
            }
            is AddAlbumContract.Event.OnNextClicked -> {
                sendEffect(AddAlbumContract.Effect.NavigateNext)
            }
            is AddAlbumContract.Event.OnExitClicked -> {
                showDialog(AddAlbumDialogState.ConfirmExitDialog)
            }
            is AddAlbumContract.Event.OnExitConfirmed -> {
                sendEffect(AddAlbumContract.Effect.Exit)
            }
            is AddAlbumContract.Event.OnDismissDialog -> {
                dismissDialog()
            }
            is AddAlbumContract.Event.OnSelectPicture -> {
                sendEffect(AddAlbumContract.Effect.OpenGallery)
            }
            is AddAlbumContract.Event.OnTakePicture -> {
                sendEffect(AddAlbumContract.Effect.TryOpenCamera)
            }
            is AddAlbumContract.Event.OnShowCameraRationale -> {
                showDialog(AddAlbumDialogState.CameraRationaleDialog)
            }
            is AddAlbumContract.Event.OnPictureCaptured -> {
                if (event.uri != null) {
                    _uiState.update {
                        it.copy(
                            versionCandidate = it.versionCandidate?.copy(coverImage = event.uri),
                            isCoverCached = true
                        )
                    }
                }
            }
            is AddAlbumContract.Event.OnUpdateVersion -> {
                _uiState.update {
                    it.copy(versionCandidate = event.candidate)
                }
            }
            is AddAlbumContract.Event.OnExistingPhotoSelected -> {
                _uiState.value.versionCandidate?.let { candidate ->
                    _uiState.update {
                        it.copy(
                            versionCandidate = candidate.copy(coverImage = event.path),
                            isCoverCached = false
                        )
                    }
                }
            }
        }
    }

    fun canNavigateBack(currentRoute: Route.AddAlbumFlow): Boolean {
        return when (currentRoute) {
            Route.AddAlbumFlow.SeeInfo -> false
            else -> true
        }
    }

    fun canNavigateNext(currentRoute: Route.AddAlbumFlow): Boolean {
        return when (currentRoute) {
            Route.AddAlbumFlow.SeeInfo -> true
            Route.AddAlbumFlow.SelectVersion -> _uiState.value.versionCandidate != null
            Route.AddAlbumFlow.AddDetails -> _uiState.value.versionCandidate?.coverImage != null
        }
    }

    private fun showDialog(dialogState: AddAlbumDialogState) {
        _uiState.update {
            it.copy(dialogState = dialogState)
        }
    }

    private fun dismissDialog() {
        _uiState.update {
            it.copy(dialogState = AddAlbumDialogState.None)
        }
    }

    private fun sendEffect(effect: AddAlbumContract.Effect) {
        viewModelScope.launch {
            _effects.send(effect)
        }
    }

    private fun saveImagePermanently(
        context: Context,
        cacheUriString: String
    ): String {
        val cacheUri = cacheUriString.toUri()
        val cacheFile = File(cacheUri.path!!)

        val permanentName = "cover_${System.currentTimeMillis()}.jpg"
        val permanentFile = File(context.filesDir, permanentName)

        cacheFile.inputStream().use { input ->
            permanentFile.outputStream().use { output ->
                input.copyTo(output)
            }
        }
        cacheFile.delete()
        return Uri.fromFile(permanentFile).toString()
    }
}
