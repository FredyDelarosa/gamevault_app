package com.fredy.gamevault.features.dashboard.domain.usecases

import com.fredy.gamevault.features.games.domain.entities.Game
import com.fredy.gamevault.features.games.domain.entities.GameStatus
import com.fredy.gamevault.features.games.domain.usecases.UpdateGameUseCase
import javax.inject.Inject

class MoveGameToBacklogUseCase @Inject constructor(
    private val updateGameUseCase: UpdateGameUseCase
) {
    suspend operator fun invoke(gameId: String): Result<Game> {
        return updateGameUseCase(
            id = gameId,
            status = GameStatus.BACKLOG
        )
    }
}