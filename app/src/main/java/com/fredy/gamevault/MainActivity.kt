package com.fredy.gamevault

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.fredy.gamevault.core.navigation.GameVaultBottomBar
import com.fredy.gamevault.core.navigation.NavGraph
import com.fredy.gamevault.core.navigation.Screen
import com.fredy.gamevault.core.session.SessionManager
import com.fredy.gamevault.core.ui.theme.GameVaultTheme
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : FragmentActivity() {

    @Inject
    lateinit var sessionManager: SessionManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            GameVaultTheme {
                GameVaultApp(
                    sessionManager = sessionManager
                )
            }
        }
    }
}

@Composable
fun GameVaultApp(
    sessionManager: SessionManager
) {
    val isLoggedIn by sessionManager.isLoggedIn().collectAsStateWithLifecycle(initialValue = false)
    val useBiometricEnabled by sessionManager.useBiometricEnabled.collectAsStateWithLifecycle(initialValue = false)
    val navController = rememberNavController()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    val startDestination = if (isLoggedIn && !useBiometricEnabled) {
        Screen.Dashboard.route
    } else {
        Screen.Login.route
    }

    Scaffold(
        bottomBar = {
            if (isLoggedIn && currentRoute in setOf(
                    Screen.Dashboard.route,
                    Screen.Backlog.route,
                    Screen.Wishlist.route,
                    Screen.Feed.route
                )) {
                GameVaultBottomBar(
                    navController = navController,
                    onNavigateToDashboard = {
                        navController.navigate(Screen.Dashboard.route) {
                            popUpTo(Screen.Dashboard.route) { inclusive = false }
                            launchSingleTop = true
                        }
                    },
                    onNavigateToBacklog = {
                        navController.navigate(Screen.Backlog.route) {
                            popUpTo(Screen.Dashboard.route) { inclusive = false }
                            launchSingleTop = true
                        }
                    },
                    onNavigateToWishlist = {
                        navController.navigate(Screen.Wishlist.route) {
                            popUpTo(Screen.Dashboard.route) { inclusive = false }
                            launchSingleTop = true
                        }
                    },
                    onNavigateToFeed = {
                        navController.navigate(Screen.Feed.route) {
                            popUpTo(Screen.Dashboard.route) { inclusive = false }
                            launchSingleTop = true
                        }
                    }
                )
            }
        }
    ) { paddingValues ->
        Box(modifier = Modifier.padding(paddingValues)) {
            NavGraph(
                navController = navController,
                startDestination = startDestination,
                sessionManager = sessionManager
            )
        }
    }
}