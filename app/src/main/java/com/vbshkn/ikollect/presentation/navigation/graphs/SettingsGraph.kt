package com.vbshkn.ikollect.presentation.navigation.graphs

import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import androidx.navigation.navigation
import com.vbshkn.ikollect.presentation.feature.settings.LanguageSettingsScreen
import com.vbshkn.ikollect.presentation.feature.settings.SettingsScreen
import com.vbshkn.ikollect.presentation.feature.settings.SettingsViewModel
import com.vbshkn.ikollect.presentation.feature.settings.TabsSettingsScreen
import com.vbshkn.ikollect.presentation.feature.settings.ThemeSettingsScreen
import com.vbshkn.ikollect.presentation.navigation.Route

fun NavGraphBuilder.settingsGraph(navController: NavHostController) {
    navigation<Route.Settings>(
        startDestination = Route.SettingsFlow.Main
    ) {
        composable<Route.SettingsFlow.Main> {
            val viewModel = hiltViewModel<SettingsViewModel>()
            SettingsScreen(
                viewModel = viewModel,
                onNavigateBack = { navController.popBackStack() },
                onNavigateToThemeSettings = { navController.navigate(Route.SettingsFlow.Theme) },
                onNavigateToLanguageSettings = { navController.navigate(Route.SettingsFlow.Language) },
                onNavigateToTabsSettings = { navController.navigate(Route.SettingsFlow.Tabs) }
            )
        }
        composable<Route.SettingsFlow.Theme> {
            val viewModel = hiltViewModel<SettingsViewModel>()
            ThemeSettingsScreen(
                viewModel = viewModel,
                onNavigateBack = { navController.popBackStack() }
            )
        }
        composable<Route.SettingsFlow.Language> {
            val viewModel = hiltViewModel<SettingsViewModel>()
            LanguageSettingsScreen(
                viewModel = viewModel,
                onNavigateBack = { navController.popBackStack() }
            )
        }
        composable<Route.SettingsFlow.Tabs> {
            val viewModel = hiltViewModel<SettingsViewModel>()
            TabsSettingsScreen(
                viewModel = viewModel,
                onNavigateBack = { navController.popBackStack() }
            )
        }
    }
}