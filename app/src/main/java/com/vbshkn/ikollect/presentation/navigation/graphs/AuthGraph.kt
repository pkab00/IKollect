package com.vbshkn.ikollect.presentation.navigation.graphs

import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import androidx.navigation.compose.navigation
import com.vbshkn.ikollect.presentation.feature.auth.AuthViewModel
import com.vbshkn.ikollect.presentation.feature.auth.LoginScreen
import com.vbshkn.ikollect.presentation.feature.auth.RegistrationScreen
import com.vbshkn.ikollect.presentation.navigation.Route

fun NavGraphBuilder.authGraph(navController: NavHostController) {
    navigation<Route.Auth>(
        startDestination = Route.AuthFlow.Login,
    ) {
        composable<Route.AuthFlow.Login> {
            val viewModel = hiltViewModel<AuthViewModel>()
            LoginScreen(
                viewModel = viewModel,
                navigateToRegistration = {
                    navController.navigate(Route.AuthFlow.Register) {
                        popUpTo(Route.AuthFlow.Login) { inclusive = true }
                    }
                },
                exitFlow = { navController.popBackStack(Route.Auth, inclusive = true) }
            )
        }
        composable<Route.AuthFlow.Register> {
            val viewModel = hiltViewModel<AuthViewModel>()
            RegistrationScreen(
                viewModel = viewModel,
                navigateToLogin = {
                    navController.navigate(Route.AuthFlow.Login) {
                        popUpTo(Route.AuthFlow.Register) { inclusive = true }
                    }
                },
                exitFlow = { navController.popBackStack(Route.Auth, inclusive = true) }
            )
        }
    }
}

