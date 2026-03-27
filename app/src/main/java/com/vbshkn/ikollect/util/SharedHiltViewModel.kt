package com.vbshkn.ikollect.util

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.ViewModel
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavController

@Composable
inline fun <reified VM : ViewModel, reified T : Any> NavBackStackEntry.sharedHiltViewModel(
    navController: NavController
): VM {
    val parentEntry = remember(this) {
        navController.getBackStackEntry<T>()
    }
    return hiltViewModel(parentEntry)
}