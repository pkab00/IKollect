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
import com.vbshkn.ikollect.presentation.feature.photocards.wizard.PhotocardWizardViewModel
import com.vbshkn.ikollect.presentation.feature.photocards.wizard.PhotocardWizardScreen
import com.vbshkn.ikollect.presentation.feature.userprofile.UserProfileScreen
import com.vbshkn.ikollect.presentation.feature.userprofile.UserProfileViewModel
import com.vbshkn.ikollect.presentation.navigation.graphs.albumProfileGraph
import com.vbshkn.ikollect.presentation.navigation.graphs.albumsGraph
import com.vbshkn.ikollect.presentation.navigation.graphs.artistsGraph
import com.vbshkn.ikollect.presentation.navigation.graphs.authGraph
import com.vbshkn.ikollect.presentation.navigation.graphs.photocardProfileGraph
import com.vbshkn.ikollect.presentation.navigation.graphs.photocardsGraph
import com.vbshkn.ikollect.presentation.navigation.graphs.settingsGraph
import kotlin.reflect.typeOf

@Composable
fun AppNavHost(
    navController: NavHostController,
    startRoute: Route
) {
    NavHost(
        navController = navController,
        startDestination = startRoute
    ) {
        composable<Route.UserProfile> {
            val viewModel = hiltViewModel<UserProfileViewModel>()
            UserProfileScreen(
                viewModel = viewModel,
                onNavigateToAuth = { navController.navigate(Route.AuthFlow.Login) },
                onNavigateToSettings = { navController.navigate(Route.Settings) },
                onNavigateToAlbum = { navController.navigate(Route.AlbumFlow.Profile(it)) },
                onNavigateToPhotocard = { navController.navigate(Route.PhotocardFlow.Profile(it)) },
                onNavigateToArtist = { navController.navigate(Route.ArtistProfile(it)) }
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

        with(navController) {
            albumsGraph(this)
            photocardsGraph(this)
            artistsGraph(this)
            albumProfileGraph(this)
            photocardProfileGraph(this)
            authGraph(this)
            settingsGraph(this)
        }
    }
}