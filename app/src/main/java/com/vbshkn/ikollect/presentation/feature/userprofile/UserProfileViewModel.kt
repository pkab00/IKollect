package com.vbshkn.ikollect.presentation.feature.userprofile

import com.vbshkn.ikollect.presentation.feature.userprofile.UserProfileContract.Effect
import com.vbshkn.ikollect.presentation.feature.userprofile.UserProfileContract.Event
import androidx.lifecycle.viewModelScope
import com.vbshkn.ikollect.domain.base.BaseViewModel
import com.vbshkn.ikollect.domain.usecase.get.GetUserProfileUseCase
import com.vbshkn.ikollect.domain.usecase.auth.LogOutUseCase
import com.vbshkn.ikollect.domain.usecase.favorite.GetFavoriteAlbumsUseCase
import com.vbshkn.ikollect.domain.usecase.favorite.GetFavoriteArtistsUseCase
import com.vbshkn.ikollect.domain.usecase.favorite.GetFavoritePhotocardsUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class UserProfileViewModel @Inject constructor(
    private val getUserProfileUseCase: GetUserProfileUseCase,
    private val getFavoriteAlbumsUseCase: GetFavoriteAlbumsUseCase,
    private val getFavoritePhotocardsUseCase: GetFavoritePhotocardsUseCase,
    private val getFavoriteArtistsUseCase: GetFavoriteArtistsUseCase
) : BaseViewModel<UserProfileUIState, Event, Effect>(initialState = UserProfileUIState()) {

    init {
        loadUserProfile()
        collectFavoriteItems()
    }

    private fun loadUserProfile() = collectFlowIntoState(
        flow = getUserProfileUseCase(),
        onLoading = { state -> state.copy(isLoading = true) },
        onSuccess = { state, profile ->
            state.copy(isLoading = false, user = profile)
        },
        onError = { state, error -> state.copy(isLoading = false, error = error) }
    )

    override fun onEvent(event: Event) {
        when (event) {
            is Event.OnLogInClick -> {
                sendEffect(Effect.GoToAuthScreen)
            }
            is Event.OnSettingsClick -> {
                sendEffect(Effect.GoToSettings)
            }

            is Event.OnAlbumClick -> {
                sendEffect(Effect.GoToAlbum(event.id))
            }
            is Event.OnArtistClick -> {
                sendEffect(Effect.GoToArtist(event.id))
            }
            is Event.OnPhotocardClick -> {
                sendEffect(Effect.GoToPhotocard(event.id))
            }
        }
    }

    private fun collectFavoriteItems() {
        collectFlowIntoState(
            flow = getFavoriteAlbumsUseCase(),
            onLoading = { state -> state.copy(isLoadingItems = true) },
            onSuccess = { state, list ->
                state.copy(
                    isLoadingItems = false,
                    favoriteAlbums = list
                )
            },
            onError = {state, error -> state.copy(isLoadingItems = false) }
        )
        collectFlowIntoState(
            flow = getFavoritePhotocardsUseCase(),
            onLoading = { state -> state.copy(isLoadingItems = true) },
            onSuccess = { state, list ->
                state.copy(
                    isLoadingItems = false,
                    favoritePhotocards = list
                )
            },
            onError = {state, error -> state.copy(isLoadingItems = false) }
        )
        collectFlowIntoState(
            flow = getFavoriteArtistsUseCase(),
            onLoading = { state -> state.copy(isLoadingItems = true) },
            onSuccess = { state, list ->
                state.copy(
                    isLoadingItems = false,
                    favoriteArtists = list
                )
            },
            onError = {state, error -> state.copy(isLoadingItems = false) }
        )
    }
}