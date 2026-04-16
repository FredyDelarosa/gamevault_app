package com.fredy.gamevault.core.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.Forum
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
import com.fredy.gamevault.features.community.presentation.screen.CreatePostScreen
import com.fredy.gamevault.features.community.presentation.screen.FeedScreen
import com.fredy.gamevault.features.dashboard.presentation.screen.DashboardScreen
import com.fredy.gamevault.features.wishlist.presentation.screen.WishlistScreen

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
                onNavigateToRegister = { navController.navigate(Screen.Register.route) },
                onLoginSuccess = {
                    navController.navigate(Screen.Dashboard.route) {
                        popUpTo(Screen.Login.route) { inclusive = true }
                    }
                }
            )
        }
        composable(Screen.Register.route) {
            RegisterScreen(
                onNavigateToLogin = { navController.popBackStack() },
                onRegisterSuccess = { navController.popBackStack() }
            )
        }
        composable(Screen.Dashboard.route) {
            DashboardScreen(onLogout = { shouldLogout = true })
        }
        composable(Screen.Backlog.route) { BacklogScreen() }
        composable(Screen.Wishlist.route) { WishlistScreen() }
        composable(Screen.Feed.route) {
            FeedScreen(
                onCreatePost = { navController.navigate(Screen.CreatePost.route) }
            )
        }
        composable(Screen.CreatePost.route) {
            CreatePostScreen(
                onBack = { navController.popBackStack() },
                onSuccess = { navController.popBackStack() }
            )
        }
    }
}

@Composable
fun GameVaultBottomBar(
    navController: NavHostController,
    onNavigateToDashboard: () -> Unit,
    onNavigateToBacklog: () -> Unit,
    onNavigateToWishlist: () -> Unit,
    onNavigateToFeed: () -> Unit
) {
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination = navBackStackEntry?.destination

    val items = listOf(
        BottomNavItem.Dashboard,
        BottomNavItem.Backlog,
        BottomNavItem.Wishlist,
        BottomNavItem.Feed
    )

    NavigationBar(
        modifier = Modifier,
        containerColor = MaterialTheme.colorScheme.surface,
        tonalElevation = 3.dp
    ) {
        items.forEach { item ->
            NavigationBarItem(
                icon = { Icon(imageVector = item.icon, contentDescription = item.label) },
                label = { Text(item.label) },
                selected = currentDestination?.hierarchy?.any { it.route == item.route } == true,
                onClick = {
                    when (item.route) {
                        Screen.Dashboard.route -> onNavigateToDashboard()
                        Screen.Backlog.route -> onNavigateToBacklog()
                        Screen.Wishlist.route -> onNavigateToWishlist()
                        Screen.Feed.route -> onNavigateToFeed()
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
    object Dashboard : BottomNavItem(Screen.Dashboard.route, Icons.Default.PlayArrow, "Playing")
    object Backlog : BottomNavItem(Screen.Backlog.route, Icons.AutoMirrored.Filled.List, "Backlog")
    object Wishlist : BottomNavItem(Screen.Wishlist.route, Icons.Default.FavoriteBorder, "Wishlist")
    object Feed : BottomNavItem(Screen.Feed.route, Icons.Default.Forum, "Feed")
}