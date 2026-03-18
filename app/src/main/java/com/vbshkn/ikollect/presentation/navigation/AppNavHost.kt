package com.vbshkn.ikollect.presentation.navigation

import androidx.compose.runtime.Composable
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navigation
import com.vbshkn.ikollect.presentation.feature.account.AccountScreen
import com.vbshkn.ikollect.presentation.feature.albums.AlbumsScreen
import com.vbshkn.ikollect.presentation.feature.photocards.PhotocardsScreen
import com.vbshkn.ikollect.presentation.feature.albums.AlbumsViewModel

@Composable
fun AppNavHost(navController: NavHostController) {
    NavHost(
        navController = navController,
        startDestination = AppDestinations.ALBUMS.route
    ) {
        composable(AppDestinations.ALBUMS.route) {
            val viewModel = hiltViewModel<AlbumsViewModel>()
            AlbumsScreen(viewModel)
        }
        composable(AppDestinations.PHOTOCARDS.route) {
            PhotocardsScreen()
        }
        composable(AppDestinations.ACCOUNT.route) {
            AccountScreen()
        }

        navigation(
            startDestination = AddAlbumFlow.DETAILS,
            route = AddAlbumFlow.ROOT
        ) {
            composable(AddAlbumFlow.DETAILS) {

            }
            composable(AddAlbumFlow.SELECT_VERSION) {

            }
            composable(AddAlbumFlow.ADD_NOTES) {

            }
        }
    }
}