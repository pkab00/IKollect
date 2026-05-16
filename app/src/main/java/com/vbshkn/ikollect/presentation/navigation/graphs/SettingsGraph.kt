package com.vbshkn.ikollect.presentation.navigation.graphs

import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import androidx.navigation.navigation
import com.vbshkn.ikollect.presentation.feature.settings.SettingsScreen
import com.vbshkn.ikollect.presentation.feature.settings.SettingsViewModel
import com.vbshkn.ikollect.presentation.navigation.Route

fun NavGraphBuilder.settingsGraph(navController: NavHostController) {
    navigation<Route.Settings>(
        startDestination = Route.SettingsFlow.Main
    ) {
        composable<Route.SettingsFlow.Main> {
            val viewModel = hiltViewModel<SettingsViewModel>()
            SettingsScreen(
                viewModel = viewModel,
                onNavigateBack = { navController.popBackStack() }
            )
        }
    }
}