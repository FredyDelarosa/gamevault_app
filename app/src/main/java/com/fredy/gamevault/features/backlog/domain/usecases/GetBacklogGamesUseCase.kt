package com.fredy.gamevault.features.backlog.domain.usecases

import com.fredy.gamevault.features.games.domain.entities.Game
import com.fredy.gamevault.features.games.domain.entities.GameStatus
import com.fredy.gamevault.features.games.domain.usecases.GetGamesUseCase
import javax.inject.Inject

class GetBacklogGamesUseCase @Inject constructor(
    private val getGamesUseCase: GetGamesUseCase
) {
    suspend operator fun invoke(): Result<List<Game>> {
        return getGamesUseCase(GameStatus.BACKLOG)
    }
}