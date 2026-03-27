package com.vbshkn.ikollect.presentation.navigation.graphs

import androidx.compose.runtime.getValue
import androidx.compose.ui.res.stringResource
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import androidx.navigation.navigation
import com.vbshkn.ikollect.R
import com.vbshkn.ikollect.presentation.feature.artists.list.AccountScreen
import com.vbshkn.ikollect.presentation.feature.artists.list.AllArtistsScreen
import com.vbshkn.ikollect.presentation.feature.artists.list.ArtistsViewModel
import com.vbshkn.ikollect.presentation.navigation.Route
import com.vbshkn.ikollect.util.sharedHiltViewModel

fun NavGraphBuilder.artistsGraph(navController: NavHostController) {
    navigation<Route.ArtistsRoute>(
        startDestination = Route.ArtistsFlow.Main
    ) {
        composable<Route.ArtistsFlow.Main> { backStackEntry ->
            val sharedViewModel =
                backStackEntry.sharedHiltViewModel<ArtistsViewModel, Route.ArtistsRoute>(navController)
            AccountScreen(
                viewModel = sharedViewModel,
                onShowAllGroupsClick = { navController.navigate(Route.ArtistsFlow.AllGroups) },
                onShowAllSoloistsClick = { navController.navigate(Route.ArtistsFlow.AllSoloists) },
                onArtistClick = { id -> navController.navigate(Route.ArtistProfile(id)) }
            )
        }
        composable<Route.ArtistsFlow.AllGroups> { backStackEntry ->
            val sharedViewModel =
                backStackEntry.sharedHiltViewModel<ArtistsViewModel, Route.ArtistsRoute>(navController)
            val state by sharedViewModel.uiState.collectAsStateWithLifecycle()

            AllArtistsScreen(
                title = stringResource(R.string.title_groups),
                artists = state.groupOverviews,
                onArtistClick = { id -> navController.navigate(Route.ArtistProfile(id)) },
                onBackClick = { navController.popBackStack() }
            )
        }
        composable<Route.ArtistsFlow.AllSoloists> { backStackEntry ->
            val sharedViewModel =
                backStackEntry.sharedHiltViewModel<ArtistsViewModel, Route.ArtistsRoute>(navController)
            val state by sharedViewModel.uiState.collectAsStateWithLifecycle()

            AllArtistsScreen(
                title = stringResource(R.string.title_soloists),
                artists = state.soloistsOverviews,
                onArtistClick = { id -> navController.navigate(Route.ArtistProfile(id)) },
                onBackClick = { navController.popBackStack() }
            )
        }
    }
}