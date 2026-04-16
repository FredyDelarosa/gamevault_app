package com.fredy.gamevault.core.navigation

sealed class Screen(val route: String) {
    object Login : Screen("login")
    object Register : Screen("register")
    object Dashboard : Screen("dashboard")
    object Backlog : Screen("backlog")
    object Wishlist : Screen("wishlist")
    object Feed : Screen("feed")
    object CreatePost : Screen("create_post")

    object GameForm : Screen("game_form/{gameId}") {
        fun createRoute(gameId: String? = null) = "game_form/${gameId ?: "new"}"
    }
}