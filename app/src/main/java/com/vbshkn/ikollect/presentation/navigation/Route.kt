package com.vbshkn.ikollect.presentation.navigation

import com.vbshkn.ikollect.domain.model.AlbumCandidate
import kotlinx.serialization.Serializable

@Serializable
sealed interface Route {
    @Serializable data object Albums : Route
    @Serializable data object Photocards : Route
    @Serializable data object CameraScreen : Route
    @Serializable data object KomcaScanner : Route

    @Serializable data class ArtistProfile(val id: Long) : Route

    @Serializable data object ArtistsRoute : Route
    @Serializable sealed class ArtistsFlow : Route {
        @Serializable data object Main : ArtistsFlow()
        @Serializable data object AllGroups : ArtistsFlow()
        @Serializable data object AllSoloists : ArtistsFlow()
    }

    @Serializable data class AlbumWizardRoute(val candidate: AlbumCandidate) : Route
    @Serializable
    sealed class AlbumWizardFlow : Route {
        @Serializable data object SeeInfo : AlbumWizardFlow()
        @Serializable data object SelectVersion : AlbumWizardFlow()
        @Serializable data object DetailsWizard : AlbumWizardFlow()
        @Serializable data object WrapUp : AlbumWizardFlow()
    }
}