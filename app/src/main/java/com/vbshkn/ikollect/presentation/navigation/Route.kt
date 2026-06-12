package com.vbshkn.ikollect.presentation.navigation

import com.vbshkn.ikollect.domain.model.candidate.AlbumCandidate
import kotlinx.serialization.Serializable

@Serializable
sealed interface Route {
    @Serializable data object Albums : Route
    @Serializable sealed class AlbumsFlow : Route {
        @Serializable data object Main : AlbumsFlow()
        @Serializable data object Search : AlbumsFlow()
    }
    @Serializable data object Photocards : Route
    @Serializable sealed class PhotocardsFlow : Route {
        @Serializable data object Main : PhotocardsFlow()
        @Serializable data object Search : PhotocardsFlow()
    }
    @Serializable data object Artists : Route
    @Serializable sealed class ArtistsFlow : Route {
        @Serializable data object Main : ArtistsFlow()
        @Serializable data object Search : ArtistsFlow()
    }
    @Serializable data object UserProfile : Route
    @Serializable data object AlbumCameraScreen : Route

    @Serializable data object KomcaScanner : Route
    @Serializable data object BarcodeScanner : Route
    @Serializable data object PhotocardCameraScreen : Route

    @Serializable data object AlbumProfile : Route
    @Serializable sealed class AlbumFlow : Route {
        @Serializable data class Profile(val id: Long) : AlbumFlow()
        @Serializable data class Edit(val id: Long) : AlbumFlow()
    }

    @Serializable data object PhotocardProfile : Route
    @Serializable sealed class PhotocardFlow : Route {
        @Serializable data class Profile(val id: Long) : PhotocardFlow()
        @Serializable data class Edit(val id: Long) : PhotocardFlow()
    }
    @Serializable data class ArtistProfile(val id: Long) : Route
    @Serializable data object Auth : Route
    @Serializable sealed class AuthFlow : Route {
        @Serializable data object Login : AuthFlow()
        @Serializable data object Register : AuthFlow()
    }

    @Serializable data object Settings : Route
    @Serializable sealed class SettingsFlow : Route {
        @Serializable data object Main : SettingsFlow()
        @Serializable data object Theme : SettingsFlow()
        @Serializable data object Language : SettingsFlow()
        @Serializable data object Tabs : SettingsFlow()
        @Serializable data object Tags : SettingsFlow()
    }

    @Serializable data class AlbumWizard(val candidate: AlbumCandidate) : Route
    @Serializable data object PhotocardWizard : Route
}