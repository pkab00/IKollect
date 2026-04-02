package com.vbshkn.ikollect.presentation.navigation

import com.vbshkn.ikollect.domain.model.AlbumCandidate
import kotlinx.serialization.Serializable

@Serializable
sealed interface Route {
    @Serializable data object Albums : Route
    @Serializable data object Photocards : Route

    @Serializable data object AlbumCameraScreen : Route
    @Serializable data object KomcaScanner : Route
    @Serializable data object PhotocardCameraScreen : Route

    @Serializable data class ArtistProfile(val id: Long) : Route

    @Serializable data object ArtistsRoute : Route
    @Serializable sealed class ArtistsFlow : Route {
        @Serializable data object Main : ArtistsFlow()
        @Serializable data object AllGroups : ArtistsFlow()
        @Serializable data object AllSoloists : ArtistsFlow()
    }
    @Serializable data class AlbumWizard(val candidate: AlbumCandidate) : Route
    @Serializable data object PhotocardWizard : Route
}