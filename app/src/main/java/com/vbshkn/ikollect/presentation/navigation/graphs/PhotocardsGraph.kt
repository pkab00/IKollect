package com.vbshkn.ikollect.presentation.navigation.graphs

import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import androidx.navigation.navigation
import com.vbshkn.ikollect.presentation.composable.grid.PhotocardsGrid
import com.vbshkn.ikollect.presentation.feature.photocards.list.PhotocardsScreen
import com.vbshkn.ikollect.presentation.feature.photocards.list.PhotocardsViewModel
import com.vbshkn.ikollect.presentation.feature.search.SearchScreen
import com.vbshkn.ikollect.presentation.feature.photocards.search.PhotocardsSearchViewModel
import com.vbshkn.ikollect.presentation.feature.search.SearchContract
import com.vbshkn.ikollect.presentation.navigation.Route

fun NavGraphBuilder.photocardsGraph(navController: NavHostController) {
    navigation<Route.Photocards>(
        startDestination = Route.PhotocardsFlow.Main
    ) {
        composable<Route.PhotocardsFlow.Main> {
            val viewModel = hiltViewModel<PhotocardsViewModel>()
            PhotocardsScreen(
                viewModel = viewModel,
                onNavigateToWizard = { navController.navigate(Route.PhotocardWizard) },
                onNavigateToPhotocard = { navController.navigate(Route.PhotocardFlow.Profile(it)) },
                onNavigateToSearch = { navController.navigate(Route.PhotocardsFlow.Search) }
            )
        }

        composable<Route.PhotocardsFlow.Search> {
            val viewModel = hiltViewModel<PhotocardsSearchViewModel>()
            val uiState by viewModel.uiState.collectAsStateWithLifecycle()

            SearchScreen(
                viewModel = viewModel,
                onNavigateBack = { navController.popBackStack() },
                onNavigateToDetail = { navController.navigate(Route.PhotocardFlow.Profile(it)) }
            ) {
                PhotocardsGrid(
                    items = uiState.results,
                    onClick = { viewModel.onEvent(SearchContract.Event.OnNavigateToDetail(it.photocardId)) },
                    onHold = { }
                )
            }
        }
    }
}