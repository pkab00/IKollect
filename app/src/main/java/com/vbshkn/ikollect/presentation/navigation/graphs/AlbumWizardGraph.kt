package com.vbshkn.ikollect.presentation.navigation.graphs

import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import androidx.navigation.navigation
import com.vbshkn.ikollect.domain.model.AlbumCandidate
import com.vbshkn.ikollect.presentation.feature.albums.wizard.AlbumWizardContract
import com.vbshkn.ikollect.presentation.feature.albums.wizard.AlbumWizardViewModel
import com.vbshkn.ikollect.presentation.feature.albums.wizard.AlbumWizardScreen
import com.vbshkn.ikollect.presentation.navigation.AlbumCandidateType
import com.vbshkn.ikollect.presentation.navigation.Route
import com.vbshkn.ikollect.util.sharedHiltViewModel
import kotlin.reflect.typeOf

fun NavGraphBuilder.albumWizardGraph(navController: NavHostController) {
    navigation<Route.AlbumWizardRoute>(
        startDestination = Route.AlbumWizardFlow.SeeInfo,
        typeMap = mapOf(typeOf<AlbumCandidate>() to AlbumCandidateType)
    ) {
        val onExit: () -> Unit = { navController.popBackStack<Route.AlbumWizardRoute>(inclusive = true) }
        composable<Route.AlbumWizardFlow.SeeInfo> { backStackEntity ->
            val sharedViewModel =
                backStackEntity.sharedHiltViewModel<AlbumWizardViewModel, Route.AlbumWizardRoute>(navController)

            AlbumWizardScreen(
                viewModel = sharedViewModel,
                onExit = onExit,
                onBack = { navController.popBackStack() },
                onNext = { navController.navigate(Route.AlbumWizardFlow.SelectVersion) },
            )
        }
        composable<Route.AlbumWizardFlow.SelectVersion> { backStackEntity ->
            val sharedViewModel =
                backStackEntity.sharedHiltViewModel<AlbumWizardViewModel, Route.AlbumWizardRoute>(navController)

            AlbumWizardScreen(
                viewModel = sharedViewModel,
                onExit = onExit,
                onNext = { navController.navigate(Route.AlbumWizardFlow.AddDetails) },
                onBack = { navController.popBackStack() },
            )
        }
        composable<Route.AlbumWizardFlow.AddDetails> { backStackEntity ->
            val parentEntity = remember(backStackEntity) {
                navController.getBackStackEntry<Route.AlbumWizardRoute>()
            }
            val sharedViewModel = backStackEntity
                .sharedHiltViewModel<AlbumWizardViewModel, Route.AlbumWizardRoute>(navController)

            val cameraResult by parentEntity.savedStateHandle
                .getStateFlow<String?>("camera_result", null)
                .collectAsStateWithLifecycle()
            val scannerResult by parentEntity.savedStateHandle
                .getStateFlow<String?>("scanner_result", null)
                .collectAsStateWithLifecycle()

            LaunchedEffect(cameraResult, scannerResult) {
                if (cameraResult != null) {
                    sharedViewModel.onEvent(AlbumWizardContract.Event.OnPictureCaptured(cameraResult!!))
                    backStackEntity.savedStateHandle["camera_result"] = null
                }
                if (scannerResult != null) {
                    sharedViewModel.onEvent(AlbumWizardContract.Event.OnKomcaCodeChanged(scannerResult!!))
                    backStackEntity.savedStateHandle["scanner_result"] = null
                }
            }

            AlbumWizardScreen(
                viewModel = sharedViewModel,
                onExit = onExit,
                onBack = { navController.popBackStack() },
                onNext = { navController.navigate(Route.AlbumWizardFlow.WrapUp) },
                onCamera = { navController.navigate(Route.CameraScreen) },
                onScanner = { navController.navigate(Route.KomcaScanner) },
            )
        }
        composable<Route.AlbumWizardFlow.WrapUp> { backStackEntity ->
            val parentEntity = remember(backStackEntity) {
                navController.getBackStackEntry<Route.AlbumWizardRoute>()
            }
            val viewModel = hiltViewModel<AlbumWizardViewModel>(parentEntity)

            AlbumWizardScreen(
                viewModel = viewModel,
                onExit = onExit,
                onBack = { navController.popBackStack() },
                onNext = {}
            )
        }
    }
}