package com.vbshkn.ikollect.presentation.feature.userprofile

import androidx.lifecycle.viewModelScope
import com.vbshkn.ikollect.domain.base.BaseViewModel
import com.vbshkn.ikollect.domain.usecase.get.GetUserProfileUseCase
import com.vbshkn.ikollect.domain.usecase.auth.LogOutUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class UserProfileViewModel @Inject constructor(
    private val getUserProfileUseCase: GetUserProfileUseCase,
    private val logOutUseCase: LogOutUseCase
) : BaseViewModel<UserProfileUIState, UserProfileContract.Event, UserProfileContract.Effect>(initialState = UserProfileUIState()) {

    init {
        loadUserProfile()
    }

    private fun loadUserProfile() = collectFlowIntoState(
        flow = getUserProfileUseCase(),
        onLoading = { state -> state.copy(isLoading = true) },
        onSuccess = { state, profile ->
            state.copy(isLoading = false, user = profile)
        },
        onError = { state, error -> state.copy(isLoading = false, error = error) }
    )

    override fun onEvent(event: UserProfileContract.Event) {
        when (event) {
            is UserProfileContract.Event.OnLogInClick -> {
                sendEffect(UserProfileContract.Effect.GoToAuthScreen)
            }
            is UserProfileContract.Event.OnLogOutClick -> viewModelScope.launch {
                logOutUseCase()
            }
        }
    }
}