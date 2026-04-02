package com.vbshkn.ikollect.presentation.navigation

import androidx.compose.runtime.Composable
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.vbshkn.ikollect.domain.model.AlbumCandidate
import com.vbshkn.ikollect.presentation.feature.camera.AlbumCameraScreen
import com.vbshkn.ikollect.presentation.feature.albums.list.AlbumsScreen
import com.vbshkn.ikollect.presentation.feature.photocards.list.PhotocardsScreen
import com.vbshkn.ikollect.presentation.feature.albums.list.AlbumsViewModel
import com.vbshkn.ikollect.presentation.feature.albums.wizard.AlbumWizardScreen
import com.vbshkn.ikollect.presentation.feature.albums.wizard.AlbumWizardViewModel
import com.vbshkn.ikollect.presentation.feature.artists.profile.ArtistProfileScreen
import com.vbshkn.ikollect.presentation.feature.artists.profile.ArtistProfileViewModel
import com.vbshkn.ikollect.presentation.feature.camera.KomcaScannerScreen
import com.vbshkn.ikollect.presentation.feature.camera.PhotocardCameraScreen
import com.vbshkn.ikollect.presentation.feature.photocards.list.PhotocardsViewModel
import com.vbshkn.ikollect.presentation.feature.photocards.wizard.PhotocardWizardViewModel
import com.vbshkn.ikollect.presentation.feature.photocards.wizard.PhotocardWizardScreen
import com.vbshkn.ikollect.presentation.navigation.graphs.artistsGraph
import kotlin.reflect.typeOf

@Composable
fun AppNavHost(navController: NavHostController) {
    NavHost(
        navController = navController,
        startDestination = Route.Albums
    ) {
        composable<Route.Albums> {
            val viewModel = hiltViewModel<AlbumsViewModel>()
            AlbumsScreen(
                viewModel = viewModel,
                onGoToWizard = { navController.navigate(Route.AlbumWizard(it)) }
            )
        }
        composable<Route.Photocards> {
            val viewModel = hiltViewModel<PhotocardsViewModel>()
            PhotocardsScreen(
                viewModel = viewModel,
                onGoToWizard = { navController.navigate(Route.PhotocardWizard) }
            )
        }

        composable<Route.AlbumCameraScreen> {
            AlbumCameraScreen { image ->
                navController.previousBackStackEntry
                    ?.savedStateHandle["camera_result"] = image
                navController.popBackStack()
            }
        }
        composable<Route.PhotocardCameraScreen> {
            PhotocardCameraScreen { image ->
                navController.previousBackStackEntry
                    ?.savedStateHandle["camera_result"] = image
                navController.popBackStack()
            }
        }
        composable<Route.KomcaScanner> {
            KomcaScannerScreen(
                onNumberRecognized = { number ->
                    navController.previousBackStackEntry
                        ?.savedStateHandle["scanner_result"] = number
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

        composable<Route.AlbumWizard> (
            typeMap = mapOf(typeOf<AlbumCandidate>() to AlbumCandidateType)
        ) { backStackEntry ->
            val viewModel = hiltViewModel<AlbumWizardViewModel>()
            val savedStateHandle = backStackEntry.savedStateHandle

            AlbumWizardScreen(
                viewModel = viewModel,
                savedStateHandle = savedStateHandle,
                onExit = { navController.popBackStack() },
                onCamera = { navController.navigate(Route.AlbumCameraScreen) },
                onScanner = { navController.navigate(Route.KomcaScanner) }
            )
        }

        composable<Route.PhotocardWizard> { backStackEntry ->
            val viewModel = hiltViewModel<PhotocardWizardViewModel>()
            val savedStateHandle = backStackEntry.savedStateHandle

            PhotocardWizardScreen(
                viewModel = viewModel,
                savedStateHandle = savedStateHandle,
                onExit = { navController.popBackStack() },
                onCamera = { navController.navigate(Route.PhotocardCameraScreen) }
            )
        }

        artistsGraph(navController)
    }
}