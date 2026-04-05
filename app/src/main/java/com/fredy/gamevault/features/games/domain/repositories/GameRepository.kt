package com.fredy.gamevault.features.games.domain.repositories

import com.fredy.gamevault.features.games.domain.entities.Game
import com.fredy.gamevault.features.games.domain.entities.GameStatus

interface GameRepository {
    suspend fun getGames(status: GameStatus? = null): Result<List<Game>>
    suspend fun getGameById(id: String): Result<Game>
    suspend fun createGame(
        name: String,
        description: String,
        coverImageUrl: String,
        status: GameStatus
    ): Result<Game>
    suspend fun updateGame(
        id: String,
        name: String?,
        description: String?,
        coverImageUrl: String?,
        status: GameStatus?,
        completed: Boolean?
    ): Result<Game>
    suspend fun deleteGame(id: String): Result<Unit>
}