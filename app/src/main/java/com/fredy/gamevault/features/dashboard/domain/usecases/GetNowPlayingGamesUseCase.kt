package com.fredy.gamevault.features.dashboard.domain.usecases

import com.fredy.gamevault.features.games.domain.entities.Game
import com.fredy.gamevault.features.games.domain.entities.GameStatus
import com.fredy.gamevault.features.games.domain.usecases.GetGamesUseCase
import javax.inject.Inject

class GetNowPlayingGamesUseCase @Inject constructor(
    private val getGamesUseCase: GetGamesUseCase
) {
    suspend operator fun invoke(): Result<List<Game>> {
        return getGamesUseCase(GameStatus.NOW_PLAYING)
    }
}