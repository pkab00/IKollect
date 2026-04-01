package com.vbshkn.ikollect.presentation.navigation

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.vbshkn.ikollect.domain.model.AlbumCandidate
import com.vbshkn.ikollect.presentation.feature.camera.CameraScreen
import com.vbshkn.ikollect.presentation.feature.albums.list.AlbumsScreen
import com.vbshkn.ikollect.presentation.feature.photocards.list.PhotocardsScreen
import com.vbshkn.ikollect.presentation.feature.albums.list.AlbumsViewModel
import com.vbshkn.ikollect.presentation.feature.albums.wizard.AlbumWizardContract
import com.vbshkn.ikollect.presentation.feature.albums.wizard.AlbumWizardScreen
import com.vbshkn.ikollect.presentation.feature.albums.wizard.AlbumWizardViewModel
import com.vbshkn.ikollect.presentation.feature.artists.profile.ArtistProfileScreen
import com.vbshkn.ikollect.presentation.feature.artists.profile.ArtistProfileViewModel
import com.vbshkn.ikollect.presentation.feature.camera.KomcaScannerScreen
import com.vbshkn.ikollect.presentation.feature.photocards.list.PhotocardsViewModel
import com.vbshkn.ikollect.presentation.feature.photocards.wizard.PhotocardWizardViewModel
import com.vbshkn.ikollect.presentation.feature.photocards.wizard.screen.PhotocardWizardScreen
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

        composable<Route.CameraScreen> {
            CameraScreen(
                onPhotoTaken = { image ->
                    navController.getBackStackEntry<Route.AlbumWizard>()
                        .savedStateHandle["camera_result"] = image
                    navController.popBackStack()
                }
            )
        }
        composable<Route.KomcaScanner> {
            KomcaScannerScreen(
                onNumberRecognized = { number ->
                    navController.getBackStackEntry<Route.AlbumWizard>()
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

        composable<Route.AlbumWizard> (
            typeMap = mapOf(typeOf<AlbumCandidate>() to AlbumCandidateType)
        ) { backStackEntity ->
            val viewModel = hiltViewModel<AlbumWizardViewModel>()

            val cameraResult by backStackEntity.savedStateHandle
                .getStateFlow<String?>("camera_result", null)
                .collectAsStateWithLifecycle()
            val scannerResult by backStackEntity.savedStateHandle
                .getStateFlow<String?>("scanner_result", null)
                .collectAsStateWithLifecycle()

            LaunchedEffect(cameraResult, scannerResult) {
                if (cameraResult != null) {
                    viewModel.onEvent(AlbumWizardContract.Event.OnPictureCaptured(cameraResult!!))
                    backStackEntity.savedStateHandle["camera_result"] = null
                }
                if (scannerResult != null) {
                    viewModel.onEvent(AlbumWizardContract.Event.OnKomcaCodeChanged(scannerResult!!))
                    backStackEntity.savedStateHandle["scanner_result"] = null
                }
            }

            AlbumWizardScreen(
                viewModel = viewModel,
                onExit = { navController.popBackStack() },
                onCamera = { navController.navigate(Route.CameraScreen) },
                onScanner = { navController.navigate(Route.KomcaScanner) }
            )
        }

        composable<Route.PhotocardWizard> { backStackEntry ->
            val viewModel = hiltViewModel<PhotocardWizardViewModel>()
            PhotocardWizardScreen(viewModel) { paddingValues ->
                Box(Modifier.fillMaxSize().padding(paddingValues))
            }
        }

        artistsGraph(navController)
    }
}