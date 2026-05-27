package com.vbshkn.ikollect.presentation.navigation.graphs

import androidx.compose.runtime.getValue
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import androidx.navigation.navigation
import com.vbshkn.ikollect.presentation.composable.grid.ArtistsGrid
import com.vbshkn.ikollect.presentation.feature.artists.list.ArtistsScreen
import com.vbshkn.ikollect.presentation.feature.artists.list.ArtistsViewModel
import com.vbshkn.ikollect.presentation.feature.artists.search.ArtistSearchViewModel
import com.vbshkn.ikollect.presentation.feature.search.SearchScreen
import com.vbshkn.ikollect.presentation.navigation.Route
import com.vbshkn.ikollect.util.sharedHiltViewModel

fun NavGraphBuilder.artistsGraph(navController: NavHostController) {
    navigation<Route.Artists>(
        startDestination = Route.ArtistsFlow.Main
    ) {
        composable<Route.ArtistsFlow.Main> {
            val viewModel = hiltViewModel<ArtistsViewModel>()
            ArtistsScreen(
                viewModel = viewModel,
                onNavigateToArtist = { id -> navController.navigate(Route.ArtistProfile(id)) },
                onNavigateToSearch = { navController.navigate(Route.ArtistsFlow.Search) }
            )
        }

        composable<Route.ArtistsFlow.Search> {
            val viewModel = hiltViewModel<ArtistSearchViewModel>()
            val uiState by viewModel.uiState.collectAsStateWithLifecycle()
            SearchScreen(
                viewModel = viewModel,
                onNavigateBack = { navController.popBackStack() },
                onNavigateToDetail = { id -> navController.navigate(Route.ArtistProfile(id)) }
            ) {
                ArtistsGrid(
                    items = uiState.results,
                    onClick = { navController.navigate(Route.ArtistProfile(it.artistId)) }
                )
            }
        }
    }
}