package com.vbshkn.ikollect.presentation.navigation.graphs

import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.res.stringResource
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import androidx.navigation.navigation
import com.vbshkn.ikollect.R
import com.vbshkn.ikollect.domain.model.AlbumCandidate
import com.vbshkn.ikollect.presentation.feature.albumwizard.AlbumWizardContract
import com.vbshkn.ikollect.presentation.feature.albumwizard.AlbumWizardViewModel
import com.vbshkn.ikollect.presentation.feature.albumwizard.screen.AddDetailsScreen
import com.vbshkn.ikollect.presentation.feature.albumwizard.screen.SeeInfoScreen
import com.vbshkn.ikollect.presentation.feature.albumwizard.screen.SelectVersionScreen
import com.vbshkn.ikollect.presentation.feature.albumwizard.screen.WizardWrapper
import com.vbshkn.ikollect.presentation.feature.albumwizard.screen.WrapUpScreen
import com.vbshkn.ikollect.presentation.navigation.AlbumCandidateType
import com.vbshkn.ikollect.presentation.navigation.Route
import com.vbshkn.ikollect.util.sharedHiltViewModel
import kotlin.reflect.typeOf

fun NavGraphBuilder.addAlbumGraph(navController: NavHostController) {
    navigation<Route.AlbumWizardRoute>(
        startDestination = Route.AlbumWizardFlow.SeeInfo,
        typeMap = mapOf(typeOf<AlbumCandidate>() to AlbumCandidateType)
    ) {
        val onExit: () -> Unit = { navController.popBackStack<Route.AlbumWizardRoute>(inclusive = true) }
        composable<Route.AlbumWizardFlow.SeeInfo> { backStackEntity ->
            val sharedViewModel =
                backStackEntity.sharedHiltViewModel<AlbumWizardViewModel, Route.AlbumWizardRoute>(navController)

            WizardWrapper(
                title = stringResource(R.string.wizard_title_info),
                onBack = { navController.popBackStack() },
                onNext = { navController.navigate(Route.AlbumWizardFlow.SelectVersion) },
                currentRoute = Route.AlbumWizardFlow.SeeInfo,
                viewModel = sharedViewModel,
                onExit = onExit,
            ) { paddingValues ->
                SeeInfoScreen(sharedViewModel, paddingValues)
            }
        }
        composable<Route.AlbumWizardFlow.SelectVersion> { backStackEntity ->
            val sharedViewModel =
                backStackEntity.sharedHiltViewModel<AlbumWizardViewModel, Route.AlbumWizardRoute>(navController)

            WizardWrapper(
                title = stringResource(R.string.wizard_title_version),
                onBack = { navController.popBackStack() },
                onNext = { navController.navigate(Route.AlbumWizardFlow.DetailsWizard) },
                currentRoute = Route.AlbumWizardFlow.SelectVersion,
                viewModel = sharedViewModel,
                onExit = onExit
            ) { paddingValues ->
                SelectVersionScreen(sharedViewModel, paddingValues)
            }
        }
        composable<Route.AlbumWizardFlow.DetailsWizard> { backStackEntity ->
            val parentEntity = remember(backStackEntity) {
                navController.getBackStackEntry<Route.AlbumWizardRoute>()
            }
            val sharedViewModel =
                backStackEntity.sharedHiltViewModel<AlbumWizardViewModel, Route.AlbumWizardRoute>(
                    navController
                )

            val cameraResult by parentEntity.savedStateHandle
                .getStateFlow<String?>("camera_result", null)
                .collectAsStateWithLifecycle()
            val scannerResult by parentEntity.savedStateHandle
                .getStateFlow<String?>("scanner_result", null)
                .collectAsStateWithLifecycle()

            LaunchedEffect(cameraResult, scannerResult) {
                if (cameraResult != null) {
                    sharedViewModel.onEvent(
                        AlbumWizardContract.Event.OnPictureCaptured(
                            cameraResult!!
                        )
                    )
                    backStackEntity.savedStateHandle["camera_result"] = null
                }
                if (scannerResult != null) {
                    sharedViewModel.onEvent(
                        AlbumWizardContract.Event.OnKomcaCodeChanged(
                            scannerResult!!
                        )
                    )
                    backStackEntity.savedStateHandle["scanner_result"] = null
                }
            }

            WizardWrapper(
                title = stringResource(R.string.wizard_title_details),
                onBack = { navController.popBackStack() },
                onNext = { navController.navigate(Route.AlbumWizardFlow.WrapUp) },
                onExit = onExit,
                onCamera = { navController.navigate(Route.CameraScreen) },
                onScanner = { navController.navigate(Route.KomcaScanner) },
                currentRoute = Route.AlbumWizardFlow.DetailsWizard,
                viewModel = sharedViewModel,
            ) { paddingValues ->
                AddDetailsScreen(sharedViewModel, paddingValues)
            }
        }
        composable<Route.AlbumWizardFlow.WrapUp> { backStackEntity ->
            val parentEntity = remember(backStackEntity) {
                navController.getBackStackEntry<Route.AlbumWizardRoute>()
            }
            val viewModel = hiltViewModel<AlbumWizardViewModel>(parentEntity)

            WizardWrapper(
                title = stringResource(R.string.wizard_title_wrapup),
                viewModel = viewModel,
                onBack = { navController.popBackStack() },
                onNext = {
                    viewModel.onEvent(AlbumWizardContract.Event.OnWrapUp)
                    onExit()
                },
                onExit = onExit,
                isLastScreen = true,
                currentRoute = Route.AlbumWizardFlow.WrapUp
            ) { paddingValues ->
                WrapUpScreen(viewModel, paddingValues)
            }
        }
    }
}