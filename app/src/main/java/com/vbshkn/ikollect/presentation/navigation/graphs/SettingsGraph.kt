package com.vbshkn.ikollect.presentation.navigation.graphs

import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import androidx.navigation.navigation
import com.vbshkn.ikollect.presentation.feature.settings.language.LanguageSettingsScreen
import com.vbshkn.ikollect.presentation.feature.settings.SettingsScreen
import com.vbshkn.ikollect.presentation.feature.settings.SettingsViewModel
import com.vbshkn.ikollect.presentation.feature.settings.tabs.TabsSettingsScreen
import com.vbshkn.ikollect.presentation.feature.settings.tag.TagSettingsScreen
import com.vbshkn.ikollect.presentation.feature.settings.theme.ThemeSettingsScreen
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
                onNavigateToTabsSettings = { navController.navigate(Route.SettingsFlow.Tabs) },
                onNavigateToTagSettings = { navController.navigate(Route.SettingsFlow.Tags) }
            )
        }
        composable<Route.SettingsFlow.Theme> {
            ThemeSettingsScreen(
                viewModel = hiltViewModel(),
                onNavigateBack = { navController.popBackStack() }
            )
        }
        composable<Route.SettingsFlow.Language> {
            LanguageSettingsScreen(
                viewModel = hiltViewModel(),
                onNavigateBack = { navController.popBackStack() }
            )
        }
        composable<Route.SettingsFlow.Tabs> {
            TabsSettingsScreen(
                viewModel = hiltViewModel(),
                onNavigateBack = { navController.popBackStack() }
            )
        }
        composable<Route.SettingsFlow.Tags> {
            TagSettingsScreen(
                viewModel = hiltViewModel(),
                onNavigateBack = { navController.popBackStack() }
            )
        }
    }
}