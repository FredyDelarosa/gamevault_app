package com.fredy.gamevault.features.games.data.datasources.local.mapper

import com.fredy.gamevault.features.games.data.datasources.local.entity.GameEntity
import com.fredy.gamevault.features.games.domain.entities.Game
import com.fredy.gamevault.features.games.domain.entities.GameStatus

fun GameEntity.toDomain(): Game {
    return Game(
        id = id,
        userId = userId,
        name = name,
        description = description,
        coverImageUrl = coverImageUrl,
        status = when (status) {
            "NOW_PLAYING" -> GameStatus.NOW_PLAYING
            "BACKLOG" -> GameStatus.BACKLOG
            else -> GameStatus.WISHLIST
        },
        completed = completed,
        createdAt = createdAt,
        updatedAt = updatedAt
    )
}

fun List<GameEntity>.toDomainList(): List<Game> {
    return this.map { it.toDomain() }
}

fun Game.toEntity(isSynced: Boolean = true): GameEntity {
    return GameEntity(
        id = id,
        userId = userId,
        name = name,
        description = description,
        coverImageUrl = coverImageUrl,
        status = when (status) {
            GameStatus.NOW_PLAYING -> "NOW_PLAYING"
            GameStatus.BACKLOG -> "BACKLOG"
            GameStatus.WISHLIST -> "WISHLIST"
        },
        completed = completed,
        createdAt = createdAt,
        updatedAt = updatedAt,
        isSynced = isSynced
    )
}