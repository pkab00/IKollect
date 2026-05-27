package com.vbshkn.ikollect.presentation.navigation.graphs

import androidx.compose.runtime.getValue
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import androidx.navigation.navigation
import com.vbshkn.ikollect.presentation.composable.grid.AlbumsGrid
import com.vbshkn.ikollect.presentation.feature.albums.list.AlbumsScreen
import com.vbshkn.ikollect.presentation.feature.albums.list.AlbumsViewModel
import com.vbshkn.ikollect.presentation.feature.albums.search.AlbumsSearchViewModel
import com.vbshkn.ikollect.presentation.feature.search.SearchContract
import com.vbshkn.ikollect.presentation.feature.search.SearchScreen
import com.vbshkn.ikollect.presentation.navigation.Route

fun NavGraphBuilder.albumsGraph(navController: NavHostController) {
    navigation<Route.Albums>(
        startDestination = Route.AlbumsFlow.Main
    ) {
        composable<Route.AlbumsFlow.Main> {
            val viewModel = hiltViewModel<AlbumsViewModel>()
            AlbumsScreen(
                viewModel = viewModel,
                onGoToWizard = { navController.navigate(Route.AlbumWizard(it)) },
                onAlbumClick = { navController.navigate(Route.AlbumFlow.Profile(it)) },
                onGoToSearch = { navController.navigate(Route.AlbumsFlow.Search) }
            )
        }

        composable<Route.AlbumsFlow.Search> {
            val viewModel = hiltViewModel<AlbumsSearchViewModel>()
            val uiState by viewModel.uiState.collectAsStateWithLifecycle()
            SearchScreen(
                viewModel = viewModel,
                onNavigateBack = { navController.popBackStack() },
                onNavigateToDetail = { navController.navigate(Route.AlbumFlow.Profile(it)) },
            ) {
                AlbumsGrid(
                    items = uiState.results,
                    onClick = { viewModel.onEvent(SearchContract.Event.OnNavigateToDetail(it.albumId)) }
                )
            }
        }
    }
}
