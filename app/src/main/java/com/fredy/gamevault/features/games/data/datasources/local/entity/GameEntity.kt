package com.fredy.gamevault.features.games.data.datasources.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "games")
data class GameEntity(
    @PrimaryKey
    val id: String,
    val userId: String,
    val name: String,
    val description: String,
    val coverImageUrl: String,
    val status: String,
    val completed: Boolean,
    val createdAt: String,
    val updatedAt: String,
    val isSynced: Boolean = false
)