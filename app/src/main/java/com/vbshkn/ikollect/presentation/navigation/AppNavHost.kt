package com.vbshkn.ikollect.presentation.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.res.stringResource
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navigation
import com.vbshkn.ikollect.R
import com.vbshkn.ikollect.domain.model.AlbumCandidate
import com.vbshkn.ikollect.presentation.feature.camera.CameraScreen
import com.vbshkn.ikollect.presentation.feature.account.AccountScreen
import com.vbshkn.ikollect.presentation.feature.addalbum.AddAlbumContract
import com.vbshkn.ikollect.presentation.feature.addalbum.AddAlbumViewModel
import com.vbshkn.ikollect.presentation.feature.addalbum.screen.AddDetailsScreen
import com.vbshkn.ikollect.presentation.feature.addalbum.screen.SeeInfoScreen
import com.vbshkn.ikollect.presentation.feature.addalbum.screen.SelectVersionScreen
import com.vbshkn.ikollect.presentation.feature.addalbum.screen.WizardWrapper
import com.vbshkn.ikollect.presentation.feature.addalbum.screen.WrapUpScreen
import com.vbshkn.ikollect.presentation.feature.albums.AlbumsScreen
import com.vbshkn.ikollect.presentation.feature.photocards.PhotocardsScreen
import com.vbshkn.ikollect.presentation.feature.albums.AlbumsViewModel
import com.vbshkn.ikollect.presentation.feature.camera.KomcaScannerScreen
import kotlin.reflect.typeOf

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
                toAddAlbumRoute = { navController.navigate(Route.AddAlbumRoute(it)) }
            )
        }
        composable<Route.Photocards> {
            PhotocardsScreen()
        }
        composable<Route.Account> {
            AccountScreen()
        }

        navigation<Route.AddAlbumRoute>(
            startDestination = Route.AddAlbumFlow.SeeInfo,
            typeMap = mapOf(typeOf<AlbumCandidate>() to AlbumCandidateType)
        ) {
            val onExit: () -> Unit = { navController.popBackStack<Route.AddAlbumRoute>(inclusive = true) }
            composable<Route.AddAlbumFlow.SeeInfo> { backStackEntity ->
                val parentEntity = remember(backStackEntity) {
                    navController.getBackStackEntry<Route.AddAlbumRoute>()
                }
                val viewModel = hiltViewModel<AddAlbumViewModel>(parentEntity)

                WizardWrapper(
                    title = stringResource(R.string.wizard_title_info),
                    onBack = { navController.popBackStack() },
                    onNext = { navController.navigate(Route.AddAlbumFlow.SelectVersion) },
                    currentRoute = Route.AddAlbumFlow.SeeInfo,
                    viewModel = viewModel,
                    onExit = onExit,
                ) { paddingValues ->
                    SeeInfoScreen(viewModel, paddingValues)
                }
            }
            composable<Route.AddAlbumFlow.SelectVersion> { backStackEntity ->
                val parentEntity = remember(backStackEntity) {
                    navController.getBackStackEntry<Route.AddAlbumRoute>()
                }
                val viewModel = hiltViewModel<AddAlbumViewModel>(parentEntity)

                WizardWrapper(
                    title = stringResource(R.string.wizard_title_version),
                    onBack = { navController.popBackStack() },
                    onNext = { navController.navigate(Route.AddAlbumFlow.AddDetails) },
                    currentRoute = Route.AddAlbumFlow.SelectVersion,
                    viewModel = viewModel,
                    onExit = onExit
                ) { paddingValues ->
                    SelectVersionScreen(viewModel, paddingValues)
                }
            }
            composable<Route.AddAlbumFlow.AddDetails> { backStackEntity ->
                val parentEntity = remember(backStackEntity) {
                    navController.getBackStackEntry<Route.AddAlbumRoute>()
                }
                val viewModel = hiltViewModel<AddAlbumViewModel>(parentEntity)

                val cameraResult by parentEntity.savedStateHandle
                    .getStateFlow<String?>("camera_result", null)
                    .collectAsStateWithLifecycle()
                val scannerResult by parentEntity.savedStateHandle
                    .getStateFlow<String?>("scanner_result", null)
                    .collectAsStateWithLifecycle()

                LaunchedEffect(cameraResult, scannerResult) {
                    if (cameraResult != null) {
                        viewModel.onEvent(AddAlbumContract.Event.OnPictureCaptured(cameraResult!!))
                        backStackEntity.savedStateHandle["camera_result"] = null
                    }
                    if (scannerResult != null) {
                        viewModel.onEvent(AddAlbumContract.Event.OnKomcaCodeChanged(scannerResult!!))
                        backStackEntity.savedStateHandle["scanner_result"] = null
                    }
                }

                WizardWrapper(
                    title = stringResource(R.string.wizard_title_details),
                    onBack = { navController.popBackStack() },
                    onNext = { navController.navigate(Route.AddAlbumFlow.WrapUp) },
                    onExit = onExit,
                    onCamera = { navController.navigate(Route.CameraScreen) },
                    onScanner = { navController.navigate(Route.KomcaScanner) },
                    currentRoute = Route.AddAlbumFlow.AddDetails,
                    viewModel = viewModel,
                ) { paddingValues ->
                    AddDetailsScreen(viewModel, paddingValues)
                }
            }
            composable<Route.AddAlbumFlow.WrapUp> { backStackEntity ->
                val parentEntity = remember(backStackEntity) {
                    navController.getBackStackEntry<Route.AddAlbumRoute>()
                }
                val viewModel = hiltViewModel<AddAlbumViewModel>(parentEntity)

                WizardWrapper(
                    title = stringResource(R.string.wizard_title_wrapup),
                    viewModel = viewModel,
                    onBack = { navController.popBackStack() },
                    onNext = {
                        viewModel.onEvent(AddAlbumContract.Event.OnWrapUp)
                        onExit()
                    },
                    onExit = onExit,
                    isLastScreen = true,
                    currentRoute = Route.AddAlbumFlow.WrapUp
                ) { paddingValues ->
                    WrapUpScreen(viewModel, paddingValues)
                }
            }
        }

        composable<Route.CameraScreen> {
            CameraScreen(
                onPhotoTaken = { image ->
                    navController.getBackStackEntry<Route.AddAlbumRoute>()
                        .savedStateHandle["camera_result"] = image
                    navController.popBackStack()
                }
            )
        }
        composable<Route.KomcaScanner> {
            KomcaScannerScreen(
                onNumberRecognized = { number ->
                    navController.getBackStackEntry<Route.AddAlbumRoute>()
                        .savedStateHandle["scanner_result"] = number
                    navController.popBackStack()
                }
            )
        }
    }
}