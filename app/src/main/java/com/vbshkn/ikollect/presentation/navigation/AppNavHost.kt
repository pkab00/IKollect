package com.vbshkn.ikollect.presentation.navigation

import androidx.compose.runtime.Composable
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navigation
import com.vbshkn.ikollect.presentation.feature.account.AccountScreen
import com.vbshkn.ikollect.presentation.feature.addalbum.SeeDetailsScreen
import com.vbshkn.ikollect.presentation.feature.albums.AlbumsScreen
import com.vbshkn.ikollect.presentation.feature.photocards.PhotocardsScreen
import com.vbshkn.ikollect.presentation.feature.albums.AlbumsViewModel

@Composable
fun AppNavHost(navController: NavHostController) {
    NavHost(
        navController = navController,
        startDestination = NavBarDestinations.ALBUMS.route
    ) {
        composable<Route.Albums> {
            val viewModel = hiltViewModel<AlbumsViewModel>()
            AlbumsScreen(
                viewModel = viewModel,
                toAddAlbumRoute = { navController.navigate(Route.AddAlbumRoute) }
            )
        }
        composable<Route.Photocards> {
            PhotocardsScreen()
        }
        composable<Route.Account> {
            AccountScreen()
        }

        navigation<Route.AddAlbumRoute>(
            startDestination = Route.AddAlbumFlow.SeeDetails
        ) {
            composable<Route.AddAlbumFlow.SeeDetails> {
                SeeDetailsScreen()
            }
            composable<Route.AddAlbumFlow.SelectVersion> {

            }
            composable<Route.AddAlbumFlow.AddNotes> {

            }
        }
    }
}