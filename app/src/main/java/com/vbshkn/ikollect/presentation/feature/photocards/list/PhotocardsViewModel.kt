package com.vbshkn.ikollect.presentation.feature.photocards.list

import com.vbshkn.ikollect.domain.usecase.GetAllPhotocardsUseCase
import com.vbshkn.ikollect.domain.base.BaseViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import jakarta.inject.Inject

@HiltViewModel
class PhotocardsViewModel @Inject constructor(
    private val getAllPhotocardsUseCase: GetAllPhotocardsUseCase
) : BaseViewModel<
        PhotocardsUIState,
        PhotocardsContract.Event,
        PhotocardsContract.Effect
        >(initialState = PhotocardsUIState()) {

    init { observePhotocards() }

    override fun onEvent(event: PhotocardsContract.Event) {
        when (event) {
            is PhotocardsContract.Event.OnWizardClicked -> {
                sendEffect(PhotocardsContract.Effect.GoToWizard)
            }
            is PhotocardsContract.Event.OnPhotocardClicked -> {
                sendEffect(PhotocardsContract.Effect.GoToPhotocard(event.id))
            }
            is PhotocardsContract.Event.OnPhotocardPreviewPressed -> updateState {
                it.copy(fullScreenPreview = event.imageUrl)
            }
            is PhotocardsContract.Event.OnPhotocardPreviewReleased -> updateState {
                it.copy(fullScreenPreview = null)
            }
        }
    }

    private fun observePhotocards() = collectFlowIntoState(
        flow = getAllPhotocardsUseCase(),
        onSuccess = { state, data -> state.copy(isLoading = false, photocards = data) },
        onLoading = { state -> state.copy(isLoading = true) },
        onError = { state, e -> state.copy(isLoading = false, error = e) }
    )
}