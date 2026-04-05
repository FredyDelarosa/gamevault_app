package com.fredy.gamevault.features.backlog.domain.usecases

import com.fredy.gamevault.features.games.domain.entities.Game
import com.fredy.gamevault.features.games.domain.entities.GameStatus
import com.fredy.gamevault.features.games.domain.usecases.UpdateGameUseCase
import javax.inject.Inject

class MoveToNowPlayingUseCase @Inject constructor(
    private val updateGameUseCase: UpdateGameUseCase
) {
    suspend operator fun invoke(gameId: String): Result<Game> {
        return updateGameUseCase(
            id = gameId,
            status = GameStatus.NOW_PLAYING
        )
    }
}