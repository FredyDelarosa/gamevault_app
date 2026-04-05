package com.fredy.gamevault.features.games.domain.entities

enum class GameStatus {
    NOW_PLAYING,
    BACKLOG,
    WISHLIST
}

data class Game(
    val id: String,
    val userId: String,
    val name: String,
    val description: String,
    val coverImageUrl: String,
    val status: GameStatus,
    val completed: Boolean,
    val createdAt: String,
    val updatedAt: String
)