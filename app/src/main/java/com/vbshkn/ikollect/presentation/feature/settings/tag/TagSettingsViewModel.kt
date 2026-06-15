package com.vbshkn.ikollect.presentation.feature.settings.tag

import androidx.lifecycle.viewModelScope
import com.vbshkn.ikollect.presentation.feature.settings.tag.TagSettingsContract.Event
import com.vbshkn.ikollect.presentation.feature.settings.tag.TagSettingsContract.Effect
import com.vbshkn.ikollect.domain.base.BaseViewModel
import com.vbshkn.ikollect.domain.usecase.delete.DeleteTagUseCase
import com.vbshkn.ikollect.domain.usecase.get.GetAllTagsUseCase
import com.vbshkn.ikollect.domain.usecase.save.SaveTagUseCase
import com.vbshkn.ikollect.domain.usecase.update.UpdateTagUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class TagSettingsViewModel @Inject constructor(
    private val getAllTagsUseCase: GetAllTagsUseCase,
    private val saveTagUseCase: SaveTagUseCase,
    private val updateTagUseCase: UpdateTagUseCase,
    private val deleteTagUseCase: DeleteTagUseCase
) : BaseViewModel<TagSettingsUiState, Event, Effect>(initialState = TagSettingsUiState()) {
    init {
        observeTags()
    }

    override fun onEvent(event: Event) {
        when (event) {
            is Event.OnCustomTagSelected -> {
                updateState { it.copy(dialogState = TagSettingsDialogState.EditTagDialog(event.tag)) }
            }
            is Event.OnDismissDialogClicked -> {
                updateState { it.copy(dialogState = TagSettingsDialogState.None) }
            }
            is Event.OnNavigateBackClicked -> {
                sendEffect(Effect.NavigateBack)
            }
            is Event.OnNewTagClicked -> {
                updateState { it.copy(dialogState = TagSettingsDialogState.CreateTagDialog) }
            }
            is Event.OnEditTagConfirmed -> viewModelScope.launch {
                updateTagUseCase(event.tag)
                updateState { it.copy(dialogState = TagSettingsDialogState.None) }
            }
            is Event.OnSaveNewTagConfirmed -> viewModelScope.launch {
                saveTagUseCase(event.tag)
                updateState { it.copy(dialogState = TagSettingsDialogState.None) }
            }
            is Event.OnDeleteTagConfirmed -> viewModelScope.launch {
                deleteTagUseCase(event.tag)
                updateState { it.copy(dialogState = TagSettingsDialogState.None) }
            }
        }
    }

    private fun observeTags() = collectFlowIntoState(
        flow = getAllTagsUseCase(),
        onSuccess = { state, data ->
            val (systemTags, customTags) = data.partition { it.isSystem }
            state.copy(systemTags = systemTags, customTags = customTags)
        },
        onLoading = { state -> state },
        onError = { state, error -> state }
    )
}