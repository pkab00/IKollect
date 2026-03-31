package com.vbshkn.ikollect.presentation.navigation

import androidx.compose.runtime.Composable
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.vbshkn.ikollect.presentation.feature.camera.CameraScreen
import com.vbshkn.ikollect.presentation.feature.albums.list.AlbumsScreen
import com.vbshkn.ikollect.presentation.feature.photocards.list.PhotocardsScreen
import com.vbshkn.ikollect.presentation.feature.albums.list.AlbumsViewModel
import com.vbshkn.ikollect.presentation.feature.artists.profile.ArtistProfileScreen
import com.vbshkn.ikollect.presentation.feature.artists.profile.ArtistProfileViewModel
import com.vbshkn.ikollect.presentation.feature.camera.KomcaScannerScreen
import com.vbshkn.ikollect.presentation.feature.photocards.list.PhotocardsViewModel
import com.vbshkn.ikollect.presentation.navigation.graphs.albumWizardGraph
import com.vbshkn.ikollect.presentation.navigation.graphs.artistsGraph
import com.vbshkn.ikollect.presentation.navigation.graphs.photocardWizardGraph

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
                onGoToWizard = { navController.navigate(Route.AlbumWizardRoute(it)) }
            )
        }
        composable<Route.Photocards> {
            val viewModel = hiltViewModel<PhotocardsViewModel>()
            PhotocardsScreen(
                viewModel = viewModel,
                onGoToWizard = { navController.navigate(Route.PhotocardWizardRoute) }
            )
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
            ArtistProfileScreen(
                viewModel = viewModel,
                onAnotherArtistClick = { navController.navigate(Route.ArtistProfile(it)) },
                onBackClick = { navController.popBackStack() }
            )
        }

        artistsGraph(navController)
        albumWizardGraph(navController)
        photocardWizardGraph(navController)
    }
}