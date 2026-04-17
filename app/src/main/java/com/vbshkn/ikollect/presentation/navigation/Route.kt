package com.vbshkn.ikollect.presentation.navigation

import com.vbshkn.ikollect.domain.model.candidate.AlbumCandidate
import kotlinx.serialization.Serializable

@Serializable
sealed interface Route {
    @Serializable data object Albums : Route
    @Serializable data object Photocards : Route
    @Serializable data object Artists : Route
    @Serializable data object AlbumCameraScreen : Route

    @Serializable data object KomcaScanner : Route
    @Serializable data object PhotocardCameraScreen : Route

    @Serializable data object AlbumProfile : Route
    @Serializable sealed class AlbumFlow : Route {
        @Serializable data class Profile(val id: Long) : AlbumFlow()
        @Serializable data class Edit(val id: Long) : AlbumFlow()
    }

    @Serializable data class PhotocardProfile(val id: Long) : Route
    @Serializable data class ArtistProfile(val id: Long) : Route
    @Serializable sealed class ArtistsFlow : Route {
        @Serializable data object Main : ArtistsFlow()
        @Serializable data object AllGroups : ArtistsFlow()
        @Serializable data object AllSoloists : ArtistsFlow()
    }
    @Serializable data class AlbumWizard(val candidate: AlbumCandidate) : Route
    @Serializable data object PhotocardWizard : Route
}