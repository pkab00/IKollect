package com.vbshkn.ikollect.presentation.navigation.graphs

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.ui.Modifier
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import androidx.navigation.navigation
import com.vbshkn.ikollect.presentation.feature.photocards.list.PhotocardsViewModel
import com.vbshkn.ikollect.presentation.feature.photocards.wizard.PhotocardWizardViewModel
import com.vbshkn.ikollect.presentation.feature.photocards.wizard.screen.WizardWrapper
import com.vbshkn.ikollect.presentation.navigation.Route
import com.vbshkn.ikollect.util.sharedHiltViewModel

fun NavGraphBuilder.photocardWizardGraph(navController: NavHostController) {
    navigation<Route.PhotocardWizardRoute>(
        startDestination = Route.PhotocardWizardFlow.SelectPhoto
    ) {
        composable<Route.PhotocardWizardFlow.SelectPhoto> { backStackEntry ->
            val sharedViewModel = backStackEntry
                .sharedHiltViewModel<PhotocardWizardViewModel, Route.PhotocardWizardRoute>(navController)

            WizardWrapper(viewModel = sharedViewModel) { paddingValues ->
                Box(Modifier.fillMaxSize().padding(paddingValues))

            }
        }
    }
}