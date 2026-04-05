package com.fredy.gamevault.features.games.domain.usecases

import com.fredy.gamevault.features.games.domain.entities.Game
import com.fredy.gamevault.features.games.domain.entities.GameStatus
import com.fredy.gamevault.features.games.domain.repositories.GameRepository
import javax.inject.Inject

class CreateGameUseCase @Inject constructor(
    private val repository: GameRepository
) {
    suspend operator fun invoke(
        name: String,
        description: String,
        coverImageUrl: String,
        status: GameStatus
    ): Result<Game> {
        return repository.createGame(name, description, coverImageUrl, status)
    }
}