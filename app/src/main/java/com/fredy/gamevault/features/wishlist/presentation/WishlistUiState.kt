package com.fredy.gamevault.features.wishlist.presentation

import com.fredy.gamevault.features.games.domain.entities.Game

data class WishlistUiState(
    val games: List<Game> = emptyList(),
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val selectedGameId: String? = null
)

sealed class WishlistEvent {
    object LoadGames : WishlistEvent()
    data class DeleteGame(val gameId: String) : WishlistEvent()
    data class MoveGameToBacklog(val gameId: String) : WishlistEvent()
    data class SelectGame(val gameId: String) : WishlistEvent()
    object ClearError : WishlistEvent()
}