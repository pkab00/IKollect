package com.vbshkn.ikollect.presentation.navigation

import kotlinx.serialization.Serializable

@Serializable
sealed interface Route {
    @Serializable data object Albums : Route
    @Serializable data object Photocards : Route
    @Serializable data object Account : Route
    @Serializable data object AddAlbumRoute : Route
    @Serializable sealed class AddAlbumFlow : Route {
        @Serializable data object SeeInfo : AddAlbumFlow()
        @Serializable data object SelectVersion : AddAlbumFlow()
        @Serializable data object AddDetails : AddAlbumFlow()
    }

    companion object {
        fun shouldHideNavigationBar(currentDestination: Route?): Boolean {
            return when(currentDestination) {
                is AddAlbumRoute -> true
                else -> false
            }
        }
    }
}