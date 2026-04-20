package com.vbshkn.ikollect.presentation.navigation.graphs

import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import androidx.navigation.navigation
import com.vbshkn.ikollect.presentation.feature.photocards.profile.PhotocardProfileScreen
import com.vbshkn.ikollect.presentation.feature.photocards.profile.PhotocardProfileViewModel
import com.vbshkn.ikollect.presentation.feature.photocards.profile.edit.EditPhotocardProfileScreen
import com.vbshkn.ikollect.presentation.feature.photocards.profile.edit.EditPhotocardProfileViewModel
import com.vbshkn.ikollect.presentation.navigation.Route

fun NavGraphBuilder.photocardProfileGraph(navController: NavHostController) {
    navigation<Route.PhotocardProfile>(
        startDestination = Route.PhotocardFlow.Profile(0L)
    ) {
        composable<Route.PhotocardFlow.Profile> {
            val viewModel = hiltViewModel<PhotocardProfileViewModel>()
            PhotocardProfileScreen(
                viewModel = viewModel,
                onNavigateBack = { navController.popBackStack() },
                onNavigateToArtist = { navController.navigate(Route.ArtistProfile(it)) },
                onNavigateToAlbum = { navController.navigate(Route.AlbumFlow.Profile(it)) },
                onNavigateToEdit = { navController.navigate(Route.PhotocardFlow.Edit(it)) }
            )
        }
        composable<Route.PhotocardFlow.Edit> {
            val viewModel = hiltViewModel<EditPhotocardProfileViewModel>()
            EditPhotocardProfileScreen(
                viewModel = viewModel,
                onNavigateBack = { navController.popBackStack() }
            )
        }
    }
}