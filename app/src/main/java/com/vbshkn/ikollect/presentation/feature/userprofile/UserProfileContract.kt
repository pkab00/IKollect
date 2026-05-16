package com.vbshkn.ikollect.presentation.feature.userprofile

sealed interface UserProfileContract {
    sealed interface Effect {
        object GoToAuthScreen : Effect
        data object GoToSettings : Effect
        data class GoToAlbum(val id: Long) : Effect
        data class GoToPhotocard(val id: Long) : Effect
        data class GoToArtist(val id: Long) : Effect
    }
    sealed interface Event {
        object OnLogInClick : Event
        object OnSettingsClick : Event
        data class OnAlbumClick(val id: Long) : Event
        data class OnPhotocardClick(val id: Long) : Event
        data class OnArtistClick(val id: Long) : Event
    }
}