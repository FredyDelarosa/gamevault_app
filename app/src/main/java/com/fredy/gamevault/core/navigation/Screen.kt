package com.fredy.gamevault.core.navigation

sealed class Screen(val route: String) {
    object Login : Screen("login")
    object Register : Screen("register")
    object Dashboard : Screen("dashboard")
    object Backlog : Screen("backlog")
    object Wishlist : Screen("wishlist")

    object GameForm : Screen("game_form/{gameId}") {
        fun createRoute(gameId: String? = null) = "game_form/${gameId ?: "new"}"
    }
}