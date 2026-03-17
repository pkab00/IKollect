package com.vbshkn.ikollect.presentation.navigation

import androidx.compose.runtime.Composable
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.vbshkn.ikollect.presentation.screen.AccountScreen
import com.vbshkn.ikollect.presentation.screen.AlbumsScreen
import com.vbshkn.ikollect.presentation.screen.PhotocardsScreen
import com.vbshkn.ikollect.presentation.viewmodel.AlbumsViewModel

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
    }
}