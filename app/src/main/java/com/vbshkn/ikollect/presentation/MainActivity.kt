package com.vbshkn.ikollect.presentation

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.adaptive.currentWindowAdaptiveInfo
import androidx.compose.material3.adaptive.navigationsuite.NavigationSuiteScaffold
import androidx.compose.material3.adaptive.navigationsuite.NavigationSuiteScaffoldDefaults
import androidx.compose.material3.adaptive.navigationsuite.NavigationSuiteType
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.tooling.preview.PreviewScreenSizes
import androidx.navigation.NavDestination.Companion.hasRoute
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.toRoute
import com.vbshkn.ikollect.presentation.navigation.NavBarDestinations
import com.vbshkn.ikollect.presentation.navigation.AppNavHost
import com.vbshkn.ikollect.presentation.navigation.Route
import com.vbshkn.ikollect.presentation.theme.IKollectTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            IKollectTheme(dynamicColor = false) {
                IKollectApp()
            }
        }
    }
}

@PreviewScreenSizes
@Composable
fun IKollectApp() {
    val navController = rememberNavController()
    val bachStackEntity by navController.currentBackStackEntryAsState()
    val currentDestination = bachStackEntity?.destination
    val hideBottomNavBar = currentDestination?.hierarchy?.any {
        it.hasRoute<Route.AddAlbumRoute>()
                || it.hasRoute<Route.CameraScreen>()
    } == true
    val navSuiteType = if (hideBottomNavBar) {
        NavigationSuiteType.None
    } else {
        NavigationSuiteScaffoldDefaults.calculateFromAdaptiveInfo(currentWindowAdaptiveInfo())
    }

    NavigationSuiteScaffold(
        layoutType = navSuiteType,
        navigationSuiteItems = {
            NavBarDestinations.entries.forEach { destination ->
                item(
                    icon = {
                        Icon(
                            imageVector = ImageVector.vectorResource(destination.iconRes),
                            contentDescription = stringResource(destination.labelRes)
                        )
                    },
                    label = { Text(stringResource(destination.labelRes)) },
                    selected = currentDestination?.hierarchy
                        ?.any { it.hasRoute(destination.route::class) } == true,
                    onClick = {
                        navController.navigate(destination.route) {
                            popUpTo(navController.graph.findStartDestination().id)
                            launchSingleTop = true
                            restoreState = true
                        }
                    }
                )
            }
        }
    ) {
        AppNavHost(navController)
    }
}