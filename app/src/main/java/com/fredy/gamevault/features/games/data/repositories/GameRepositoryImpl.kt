package com.fredy.gamevault.features.games.data.repositories

import com.fredy.gamevault.core.network.GameVaultApi
import com.fredy.gamevault.core.session.SessionManager
import com.fredy.gamevault.features.games.data.datasources.local.dao.GameDao
import com.fredy.gamevault.features.games.data.datasources.local.mapper.toDomain
import com.fredy.gamevault.features.games.data.datasources.local.mapper.toDomainList
import com.fredy.gamevault.features.games.data.datasources.local.mapper.toEntity
import com.fredy.gamevault.features.games.data.datasources.remote.mapper.toDomain as toRemoteDomain
import com.fredy.gamevault.features.games.data.datasources.remote.mapper.toDto
import com.fredy.gamevault.features.games.domain.entities.Game
import com.fredy.gamevault.features.games.domain.entities.GameStatus
import com.fredy.gamevault.features.games.domain.repositories.GameRepository
import kotlinx.coroutines.flow.first
import retrofit2.HttpException
import javax.inject.Inject

class GameRepositoryImpl @Inject constructor(
    private val api: GameVaultApi,
    private val gameDao: GameDao,
    private val sessionManager: SessionManager
) : GameRepository {

    override suspend fun getGames(status: GameStatus?): Result<List<Game>> {
        return try {
            val statusString = status?.let {
                when (it) {
                    GameStatus.NOW_PLAYING -> "NOW_PLAYING"
                    GameStatus.BACKLOG -> "BACKLOG"
                    GameStatus.WISHLIST -> "WISHLIST"
                }
            }
            val response = api.getGames(statusString)
            val games = response.map { it.toRemoteDomain() }

            val userId = getCurrentUserId()
            games.forEach { game ->
                if (game.userId == userId) {
                    gameDao.insertGame(game.toEntity(isSynced = true))
                }
            }

            Result.success(games)
        } catch (e: Exception) {
            val userId = getCurrentUserId()
            val localGames = if (status != null) {
                gameDao.getGamesByStatus(
                    when (status) {
                        GameStatus.NOW_PLAYING -> "NOW_PLAYING"
                        GameStatus.BACKLOG -> "BACKLOG"
                        GameStatus.WISHLIST -> "WISHLIST"
                    }
                ).first().filter { it.userId == userId }.toDomainList()
            } else {
                gameDao.getGamesByUser(userId).first().toDomainList()
            }

            if (localGames.isNotEmpty()) {
                Result.success(localGames)
            } else {
                Result.failure(e)
            }
        }
    }

    override suspend fun getGameById(id: String): Result<Game> {
        return try {
            val response = api.getGameById(id)
            val game = response.toRemoteDomain()

            gameDao.insertGame(game.toEntity(isSynced = true))

            Result.success(game)
        } catch (e: Exception) {
            val localGame = gameDao.getGameById(id)?.toDomain()
            if (localGame != null) {
                Result.success(localGame)
            } else {
                Result.failure(e)
            }
        }
    }

    override suspend fun createGame(
        name: String,
        description: String,
        coverImageUrl: String,
        status: GameStatus
    ): Result<Game> {
        return try {
            val response = api.createGame(
                com.fredy.gamevault.features.games.data.datasources.remote.model.GameDto(
                    name = name,
                    description = description,
                    coverImageUrl = coverImageUrl,
                    status = when (status) {
                        GameStatus.NOW_PLAYING -> "NOW_PLAYING"
                        GameStatus.BACKLOG -> "BACKLOG"
                        GameStatus.WISHLIST -> "WISHLIST"
                    },
                    completed = false
                )
            )
            val game = response.toRemoteDomain()

            gameDao.insertGame(game.toEntity(isSynced = true))

            Result.success(game)
        } catch (_: Exception) {
            val userId = getCurrentUserId()
            val localGame = Game(
                id = java.util.UUID.randomUUID().toString(),
                userId = userId,
                name = name,
                description = description,
                coverImageUrl = coverImageUrl,
                status = status,
                completed = false,
                createdAt = java.time.Instant.now().toString(),
                updatedAt = java.time.Instant.now().toString()
            )
            gameDao.insertGame(localGame.toEntity(isSynced = false))
            Result.success(localGame)
        }
    }

    override suspend fun updateGame(
        id: String,
        name: String?,
        description: String?,
        coverImageUrl: String?,
        status: GameStatus?,
        completed: Boolean?
    ): Result<Game> {
        return try {
            val currentGame = api.getGameById(id)

            val gameDto = com.fredy.gamevault.features.games.data.datasources.remote.model.GameDto(
                name = name ?: currentGame.name,
                description = description ?: currentGame.description,
                coverImageUrl = coverImageUrl ?: currentGame.coverImageUrl,
                status = status?.let {
                    when (it) {
                        GameStatus.NOW_PLAYING -> "NOW_PLAYING"
                        GameStatus.BACKLOG -> "BACKLOG"
                        GameStatus.WISHLIST -> "WISHLIST"
                    }
                } ?: currentGame.status,
                completed = completed ?: currentGame.completed
            )
            val response = api.updateGame(id, gameDto)
            val game = response.toRemoteDomain()

            gameDao.updateGame(game.toEntity(isSynced = true))

            Result.success(game)
        } catch (e: Exception) {
            val existingGame = gameDao.getGameById(id)
            if (existingGame != null) {
                val updatedGame = existingGame.copy(
                    name = name ?: existingGame.name,
                    description = description ?: existingGame.description,
                    coverImageUrl = coverImageUrl ?: existingGame.coverImageUrl,
                    status = status?.let {
                        when (it) {
                            GameStatus.NOW_PLAYING -> "NOW_PLAYING"
                            GameStatus.BACKLOG -> "BACKLOG"
                            GameStatus.WISHLIST -> "WISHLIST"
                        }
                    } ?: existingGame.status,
                    completed = completed ?: existingGame.completed,
                    isSynced = false
                ).toDomain()

                gameDao.updateGame(updatedGame.toEntity(isSynced = false))
                Result.success(updatedGame)
            } else {
                Result.failure(e)
            }
        }
    }

    override suspend fun deleteGame(id: String): Result<Unit> {
        return try {
            val response = api.deleteGame(id)
            if (response.message == "game deleted successfully") {
                gameDao.deleteGameById(id)
                Result.success(Unit)
            } else {
                Result.failure(Exception(response.message))
            }
        } catch (_: Exception) {
            gameDao.deleteGameById(id)
            Result.success(Unit)
        }
    }

    suspend fun syncPendingGames(): Result<Unit> {
        val unsyncedGames = gameDao.getUnsyncedGames()
        for (gameEntity in unsyncedGames) {
            try {
                val game = gameEntity.toDomain()

                val existsInBackend = try {
                    api.getGameById(game.id)
                    true
                } catch (e: HttpException) {
                    if (e.code() == 404) {
                        false
                    } else {
                        throw e
                    }
                }

                if (existsInBackend) {
                    val updatedGame = api.updateGame(game.id, game.toDto()).toRemoteDomain()
                    gameDao.insertGame(updatedGame.toEntity(isSynced = true))
                } else {
                    val createdGame = api.createGame(game.toDto()).toRemoteDomain()

                    if (createdGame.id != game.id) {
                        gameDao.deleteGameById(game.id)
                    }
                    gameDao.insertGame(createdGame.toEntity(isSynced = true))
                }
            } catch (_: Exception) {
            }
        }
        return Result.success(Unit)
    }

    private suspend fun getCurrentUserId(): String {
        return sessionManager.getUserId() ?: ""
    }
}