package com.vbshkn.ikollect.presentation.navigation

import androidx.compose.runtime.Composable
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.vbshkn.ikollect.domain.model.candidate.AlbumCandidate
import com.vbshkn.ikollect.presentation.feature.camera.AlbumCameraScreen
import com.vbshkn.ikollect.presentation.feature.albums.list.AlbumsScreen
import com.vbshkn.ikollect.presentation.feature.photocards.list.PhotocardsScreen
import com.vbshkn.ikollect.presentation.feature.albums.list.AlbumsViewModel
import com.vbshkn.ikollect.presentation.feature.albums.wizard.AlbumWizardScreen
import com.vbshkn.ikollect.presentation.feature.albums.wizard.AlbumWizardViewModel
import com.vbshkn.ikollect.presentation.feature.artists.profile.ArtistProfileScreen
import com.vbshkn.ikollect.presentation.feature.artists.profile.ArtistProfileViewModel
import com.vbshkn.ikollect.presentation.feature.camera.CameraResultContract
import com.vbshkn.ikollect.presentation.feature.camera.KomcaScannerScreen
import com.vbshkn.ikollect.presentation.feature.camera.PhotocardCameraScreen
import com.vbshkn.ikollect.presentation.feature.photocards.list.PhotocardsViewModel
import com.vbshkn.ikollect.presentation.feature.photocards.profile.PhotocardProfileScreen
import com.vbshkn.ikollect.presentation.feature.photocards.profile.PhotocardProfileViewModel
import com.vbshkn.ikollect.presentation.feature.photocards.wizard.PhotocardWizardViewModel
import com.vbshkn.ikollect.presentation.feature.photocards.wizard.PhotocardWizardScreen
import com.vbshkn.ikollect.presentation.navigation.graphs.albumProfileGraph
import com.vbshkn.ikollect.presentation.navigation.graphs.artistsGraph
import com.vbshkn.ikollect.presentation.navigation.graphs.photocardProfileGraph
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
                onGoToWizard = { navController.navigate(Route.AlbumWizard(it)) },
                onAlbumClick = { navController.navigate(Route.AlbumFlow.Profile(it)) }
            )
        }
        composable<Route.Photocards> {
            val viewModel = hiltViewModel<PhotocardsViewModel>()
            PhotocardsScreen(
                viewModel = viewModel,
                onNavigateToWizard = { navController.navigate(Route.PhotocardWizard) },
                onNavigateToPhotocard = { navController.navigate(Route.PhotocardFlow.Profile(it)) }
            )
        }

        composable<Route.AlbumCameraScreen> {
            AlbumCameraScreen { image ->
                navController.previousBackStackEntry
                    ?.savedStateHandle[CameraResultContract.CAMERA_RESULT] = image
                navController.popBackStack()
            }
        }
        composable<Route.PhotocardCameraScreen> {
            PhotocardCameraScreen { image ->
                navController.previousBackStackEntry
                    ?.savedStateHandle[CameraResultContract.CAMERA_RESULT] = image
                navController.popBackStack()
            }
        }
        composable<Route.KomcaScanner> {
            KomcaScannerScreen(
                onNumberRecognized = { number ->
                    navController.previousBackStackEntry
                        ?.savedStateHandle[CameraResultContract.SCANNER_RESULT] = number
                    navController.popBackStack()
                }
            )
        }

        composable<Route.ArtistProfile> {
            val viewModel = hiltViewModel<ArtistProfileViewModel>()
            ArtistProfileScreen(
                viewModel = viewModel,
                onNavigateBack = { navController.popBackStack() },
                onNavigateToArtist = { navController.navigate(Route.ArtistProfile(it)) },
                onNavigateToAlbum = { navController.navigate(Route.AlbumFlow.Profile(it)) },
                onNavigateToPhotocard = { navController.navigate(Route.PhotocardFlow.Profile(it)) }
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
        albumProfileGraph(navController)
        photocardProfileGraph(navController)
    }
}