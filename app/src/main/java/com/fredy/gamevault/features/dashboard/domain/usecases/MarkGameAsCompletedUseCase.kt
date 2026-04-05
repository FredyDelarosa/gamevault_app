package com.fredy.gamevault.features.dashboard.domain.usecases

import com.fredy.gamevault.features.games.domain.entities.Game
import com.fredy.gamevault.features.games.domain.usecases.UpdateGameUseCase
import javax.inject.Inject

class MarkGameAsCompletedUseCase @Inject constructor(
    private val updateGameUseCase: UpdateGameUseCase
) {
    suspend operator fun invoke(gameId: String, completed: Boolean): Result<Game> {
        return updateGameUseCase(
            id = gameId,
            completed = completed
        )
    }
}