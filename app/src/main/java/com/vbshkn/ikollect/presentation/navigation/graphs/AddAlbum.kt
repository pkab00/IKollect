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
import com.vbshkn.ikollect.presentation.feature.addalbum.AddAlbumContract
import com.vbshkn.ikollect.presentation.feature.addalbum.AddAlbumViewModel
import com.vbshkn.ikollect.presentation.feature.addalbum.screen.AddDetailsScreen
import com.vbshkn.ikollect.presentation.feature.addalbum.screen.SeeInfoScreen
import com.vbshkn.ikollect.presentation.feature.addalbum.screen.SelectVersionScreen
import com.vbshkn.ikollect.presentation.feature.addalbum.screen.WizardWrapper
import com.vbshkn.ikollect.presentation.feature.addalbum.screen.WrapUpScreen
import com.vbshkn.ikollect.presentation.navigation.AlbumCandidateType
import com.vbshkn.ikollect.presentation.navigation.Route
import com.vbshkn.ikollect.util.sharedHiltViewModel
import kotlin.reflect.typeOf

fun NavGraphBuilder.addAlbumGraph(navController: NavHostController) {
    navigation<Route.AddAlbumRoute>(
        startDestination = Route.AddAlbumFlow.SeeInfo,
        typeMap = mapOf(typeOf<AlbumCandidate>() to AlbumCandidateType)
    ) {
        val onExit: () -> Unit = { navController.popBackStack<Route.AddAlbumRoute>(inclusive = true) }
        composable<Route.AddAlbumFlow.SeeInfo> { backStackEntity ->
            val sharedViewModel =
                backStackEntity.sharedHiltViewModel<AddAlbumViewModel, Route.AddAlbumRoute>(navController)

            WizardWrapper(
                title = stringResource(R.string.wizard_title_info),
                onBack = { navController.popBackStack() },
                onNext = { navController.navigate(Route.AddAlbumFlow.SelectVersion) },
                currentRoute = Route.AddAlbumFlow.SeeInfo,
                viewModel = sharedViewModel,
                onExit = onExit,
            ) { paddingValues ->
                SeeInfoScreen(sharedViewModel, paddingValues)
            }
        }
        composable<Route.AddAlbumFlow.SelectVersion> { backStackEntity ->
            val sharedViewModel =
                backStackEntity.sharedHiltViewModel<AddAlbumViewModel, Route.AddAlbumRoute>(navController)

            WizardWrapper(
                title = stringResource(R.string.wizard_title_version),
                onBack = { navController.popBackStack() },
                onNext = { navController.navigate(Route.AddAlbumFlow.AddDetails) },
                currentRoute = Route.AddAlbumFlow.SelectVersion,
                viewModel = sharedViewModel,
                onExit = onExit
            ) { paddingValues ->
                SelectVersionScreen(sharedViewModel, paddingValues)
            }
        }
        composable<Route.AddAlbumFlow.AddDetails> { backStackEntity ->
            val parentEntity = remember(backStackEntity) {
                navController.getBackStackEntry<Route.AddAlbumRoute>()
            }
            val sharedViewModel =
                backStackEntity.sharedHiltViewModel<AddAlbumViewModel, Route.AddAlbumRoute>(
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
                        AddAlbumContract.Event.OnPictureCaptured(
                            cameraResult!!
                        )
                    )
                    backStackEntity.savedStateHandle["camera_result"] = null
                }
                if (scannerResult != null) {
                    sharedViewModel.onEvent(
                        AddAlbumContract.Event.OnKomcaCodeChanged(
                            scannerResult!!
                        )
                    )
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
                viewModel = sharedViewModel,
            ) { paddingValues ->
                AddDetailsScreen(sharedViewModel, paddingValues)
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
}