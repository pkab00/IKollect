package com.vbshkn.ikollect.presentation.navigation

import androidx.compose.runtime.Composable
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.vbshkn.ikollect.presentation.feature.camera.CameraScreen
import com.vbshkn.ikollect.presentation.feature.albums.AlbumsScreen
import com.vbshkn.ikollect.presentation.feature.photocards.PhotocardsScreen
import com.vbshkn.ikollect.presentation.feature.albums.AlbumsViewModel
import com.vbshkn.ikollect.presentation.feature.artists.profile.ArtistProfileScreen
import com.vbshkn.ikollect.presentation.feature.artists.profile.ArtistProfileViewModel
import com.vbshkn.ikollect.presentation.feature.camera.KomcaScannerScreen
import com.vbshkn.ikollect.presentation.navigation.graphs.addAlbumGraph
import com.vbshkn.ikollect.presentation.navigation.graphs.artistsGraph

@Composable
fun AppNavHost(navController: NavHostController) {
    NavHost(
        navController = navController,
        startDestination = NavBarDestinations.ALBUMS.route
    ) {
        composable<Route.Albums> {
            val viewModel = hiltViewModel<AlbumsViewModel>()
            AlbumsScreen(
                viewModel = viewModel,
                toAddAlbumRoute = { navController.navigate(Route.AlbumWizardRoute(it)) }
            )
        }
        composable<Route.Photocards> {
            PhotocardsScreen()
        }

        composable<Route.CameraScreen> {
            CameraScreen(
                onPhotoTaken = { image ->
                    navController.getBackStackEntry<Route.AlbumWizardRoute>()
                        .savedStateHandle["camera_result"] = image
                    navController.popBackStack()
                }
            )
        }
        composable<Route.KomcaScanner> {
            KomcaScannerScreen(
                onNumberRecognized = { number ->
                    navController.getBackStackEntry<Route.AlbumWizardRoute>()
                        .savedStateHandle["scanner_result"] = number
                    navController.popBackStack()
                }
            )
        }

        composable<Route.ArtistProfile> {
            val viewModel = hiltViewModel<ArtistProfileViewModel>()
            ArtistProfileScreen(viewModel)
        }

        artistsGraph(navController)
        addAlbumGraph(navController)
    }
}