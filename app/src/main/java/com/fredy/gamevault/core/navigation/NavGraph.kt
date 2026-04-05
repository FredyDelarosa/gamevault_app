package com.fredy.gamevault.core.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.fredy.gamevault.core.session.SessionManager
import com.fredy.gamevault.features.auth.presentation.screens.LoginScreen
import com.fredy.gamevault.features.auth.presentation.screens.RegisterScreen
import com.fredy.gamevault.features.backlog.presentation.screen.BacklogScreen
import com.fredy.gamevault.features.dashboard.presentation.screen.DashboardScreen

@Composable
fun NavGraph(
    navController: NavHostController = rememberNavController(),
    startDestination: String,
    sessionManager: SessionManager
) {
    var shouldLogout by remember { mutableStateOf(false) }

    LaunchedEffect(shouldLogout) {
        if (shouldLogout) {
            sessionManager.clearSession()
            navController.navigate(Screen.Login.route) {
                popUpTo(navController.graph.id) { inclusive = true }
                launchSingleTop = true
            }
            shouldLogout = false
        }
    }

    NavHost(
        navController = navController,
        startDestination = startDestination
    ) {
        composable(Screen.Login.route) {
            LoginScreen(
                onNavigateToRegister = {
                    navController.navigate(Screen.Register.route)
                },
                onLoginSuccess = {
                    navController.navigate(Screen.Dashboard.route) {
                        popUpTo(Screen.Login.route) { inclusive = true }
                    }
                }
            )
        }

        composable(Screen.Register.route) {
            RegisterScreen(
                onNavigateToLogin = {
                    navController.popBackStack()
                },
                onRegisterSuccess = {
                    navController.popBackStack()
                }
            )
        }

        composable(Screen.Dashboard.route) {
            DashboardScreen(
                onLogout = {
                    shouldLogout = true
                }
            )
        }

        composable(Screen.Backlog.route) {
            BacklogScreen(
            )
        }
    }
}

@Composable
fun GameVaultBottomBar(
    navController: NavHostController,
    onNavigateToDashboard: () -> Unit,
    onNavigateToBacklog: () -> Unit
) {
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination = navBackStackEntry?.destination

    val items = listOf(
        BottomNavItem.Dashboard,
        BottomNavItem.Backlog
    )

    NavigationBar(
        modifier = Modifier,
        containerColor = MaterialTheme.colorScheme.surface,
        tonalElevation = 3.dp
    ) {
        items.forEach { item ->
            NavigationBarItem(
                icon = {
                    Icon(
                        imageVector = item.icon,
                        contentDescription = item.label
                    )
                },
                label = { Text(item.label) },
                selected = currentDestination?.hierarchy?.any { it.route == item.route } == true,
                onClick = {
                    when (item.route) {
                        Screen.Dashboard.route -> onNavigateToDashboard()
                        Screen.Backlog.route -> onNavigateToBacklog()
                    }
                }
            )
        }
    }
}

sealed class BottomNavItem(
    val route: String,
    val icon: androidx.compose.ui.graphics.vector.ImageVector,
    val label: String
) {
    object Dashboard : BottomNavItem(
        route = Screen.Dashboard.route,
        icon = Icons.Default.PlayArrow,
        label = "Now Playing"
    )

    object Backlog : BottomNavItem(
        route = Screen.Backlog.route,
        icon = Icons.AutoMirrored.Filled.List,
        label = "Backlog"
    )
}