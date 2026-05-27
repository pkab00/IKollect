package com.vbshkn.ikollect.presentation.feature.photocards.list

import androidx.lifecycle.viewModelScope
import com.vbshkn.ikollect.presentation.feature.photocards.list.PhotocardsContract.Event
import com.vbshkn.ikollect.presentation.feature.photocards.list.PhotocardsContract.Effect
import com.vbshkn.ikollect.domain.usecase.get.GetAllPhotocardsUseCase
import com.vbshkn.ikollect.domain.base.BaseViewModel
import com.vbshkn.ikollect.domain.usecase.RefreshDataUseCase
import com.vbshkn.ikollect.presentation.feature.photocards.list.PhotocardsContract.Effect.*
import dagger.hilt.android.lifecycle.HiltViewModel
import jakarta.inject.Inject
import kotlinx.coroutines.launch

@HiltViewModel
class PhotocardsViewModel @Inject constructor(
    private val getAllPhotocardsUseCase: GetAllPhotocardsUseCase,
    private val refreshDataUseCase: RefreshDataUseCase
) : BaseViewModel<PhotocardsUIState, Event, Effect>(initialState = PhotocardsUIState()) {

    init { observePhotocards() }

    override fun onEvent(event: Event) {
        when (event) {
            is Event.OnWizardClicked -> {
                sendEffect(GoToWizard)
            }
            is Event.OnPhotocardClicked -> {
                sendEffect(GoToPhotocard(event.id))
            }
            is Event.OnPhotocardPreviewPressed -> updateState {
                it.copy(fullScreenPreview = event.imageUrl)
            }
            is Event.OnPhotocardPreviewReleased -> updateState {
                it.copy(fullScreenPreview = null)
            }
            is Event.OnPulledToRefresh -> viewModelScope.launch {
                updateState { it.copy(isSyncing = true) }
                val succeed = refreshDataUseCase()
                if (!succeed) sendEffect(ShowRefreshingErrorToast)
                updateState { it.copy(isSyncing = false) }

            }
            is Event.OnSearchClicked -> {
                sendEffect(GoToSearch)
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