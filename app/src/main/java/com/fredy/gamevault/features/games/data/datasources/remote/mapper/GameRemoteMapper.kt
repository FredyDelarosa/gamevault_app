package com.fredy.gamevault.features.games.data.datasources.remote.mapper

import com.fredy.gamevault.features.games.data.datasources.remote.model.GameDto
import com.fredy.gamevault.features.games.data.datasources.remote.model.GameResponse
import com.fredy.gamevault.features.games.domain.entities.Game
import com.fredy.gamevault.features.games.domain.entities.GameStatus

fun GameResponse.toDomain(): Game {
    return Game(
        id = id,
        userId = userId,
        name = name,
        description = description,
        coverImageUrl = coverImageUrl,
        status = when (status) {
            "NOW_PLAYING" -> GameStatus.NOW_PLAYING
            "BACKLOG" -> GameStatus.BACKLOG
            "WISHLIST" -> GameStatus.WISHLIST
            else -> GameStatus.WISHLIST
        },
        completed = completed,
        createdAt = createdAt,
        updatedAt = updatedAt
    )
}

fun List<GameResponse>.toDomainList(): List<Game> {
    return this.map { it.toDomain() }
}

fun Game.toDto(): GameDto {
    return GameDto(
        name = name,
        description = description,
        coverImageUrl = coverImageUrl,
        status = when (status) {
            GameStatus.NOW_PLAYING -> "NOW_PLAYING"
            GameStatus.BACKLOG -> "BACKLOG"
            GameStatus.WISHLIST -> "WISHLIST"
        },
        completed = completed
    )
}