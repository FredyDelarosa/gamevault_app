package com.fredy.gamevault.features.games.data.datasources.local.dao

import androidx.room.*
import com.fredy.gamevault.features.games.data.datasources.local.entity.GameEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface GameDao {

    @Query("SELECT * FROM games WHERE status = :status ORDER BY createdAt DESC")
    fun getGamesByStatus(status: String): Flow<List<GameEntity>>

    @Query("SELECT * FROM games WHERE userId = :userId ORDER BY createdAt DESC")
    fun getGamesByUser(userId: String): Flow<List<GameEntity>>

    @Query("SELECT * FROM games WHERE id = :id")
    suspend fun getGameById(id: String): GameEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertGame(game: GameEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertGames(games: List<GameEntity>)

    @Update
    suspend fun updateGame(game: GameEntity)

    @Delete
    suspend fun deleteGame(game: GameEntity)

    @Query("DELETE FROM games WHERE id = :id")
    suspend fun deleteGameById(id: String)

    @Query("DELETE FROM games WHERE userId = :userId")
    suspend fun deleteAllGamesForUser(userId: String)

    @Query("SELECT * FROM games WHERE isSynced = 0")
    suspend fun getUnsyncedGames(): List<GameEntity>

    @Query("UPDATE games SET isSynced = 1 WHERE id = :id")
    suspend fun markAsSynced(id: String)
}