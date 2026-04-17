package com.vbshkn.ikollect.presentation.navigation.graphs

import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import androidx.navigation.navigation
import androidx.navigation.toRoute
import com.vbshkn.ikollect.presentation.feature.albums.profile.AlbumProfileScreen
import com.vbshkn.ikollect.presentation.feature.albums.profile.AlbumProfileViewModel
import com.vbshkn.ikollect.presentation.feature.albums.profile.edit.EditAlbumProfileScreen
import com.vbshkn.ikollect.presentation.feature.albums.profile.edit.EditAlbumProfileViewModel
import com.vbshkn.ikollect.presentation.navigation.Route

fun NavGraphBuilder.albumProfileGraph(navController: NavHostController) {
    navigation<Route.AlbumProfile>(
        startDestination = Route.AlbumFlow.Profile(0L)
    ) {
        composable<Route.AlbumFlow.Profile> { backStackEntry ->
            val viewModel = hiltViewModel<AlbumProfileViewModel>()
            val albumId = backStackEntry.savedStateHandle.toRoute<Route.AlbumFlow.Profile>().id
            AlbumProfileScreen(
                viewModel = viewModel,
                onNavigateBack = { navController.popBackStack() },
                onNavigateToArtist = { navController.navigate(Route.ArtistProfile(it)) },
                onNavigateToPhotocard = { navController.navigate(Route.PhotocardProfile(it)) },
                onNavigateToEdit = { navController.navigate(Route.AlbumFlow.Edit(albumId)) }
            )
        }

        composable<Route.AlbumFlow.Edit> { backStackEntry ->
            val viewModel = hiltViewModel<EditAlbumProfileViewModel>()
            EditAlbumProfileScreen(
                viewModel = viewModel,
                savedStateHandle = backStackEntry.savedStateHandle,
                onNavigateBack = { navController.popBackStack() },
                onOpenScanner = { navController.navigate(Route.KomcaScanner) }
            )
        }
    }
}
