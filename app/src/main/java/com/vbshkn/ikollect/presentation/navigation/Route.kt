package com.vbshkn.ikollect.presentation.navigation

import com.vbshkn.ikollect.domain.model.AlbumCandidate
import kotlinx.serialization.Serializable

@Serializable
sealed interface Route {
    @Serializable data object Albums : Route
    @Serializable data object Photocards : Route
    @Serializable data object CameraScreen : Route
    @Serializable data object KomcaScanner : Route

    @Serializable data object ArtistsRoute : Route
    @Serializable sealed class ArtistsFlow : Route {
        @Serializable data object Main : ArtistsFlow()
        @Serializable data object AllGroups : ArtistsFlow()
        @Serializable data object AllSoloists : ArtistsFlow()
    }

    @Serializable data class AddAlbumRoute(val candidate: AlbumCandidate) : Route
    @Serializable
    sealed class AddAlbumFlow : Route {
        @Serializable data object SeeInfo : AddAlbumFlow()
        @Serializable data object SelectVersion : AddAlbumFlow()
        @Serializable data object AddDetails : AddAlbumFlow()
        @Serializable data object WrapUp : AddAlbumFlow()
    }
}