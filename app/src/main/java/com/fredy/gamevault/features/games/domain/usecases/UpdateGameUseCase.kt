package com.fredy.gamevault.features.games.domain.usecases

import com.fredy.gamevault.features.games.domain.entities.Game
import com.fredy.gamevault.features.games.domain.entities.GameStatus
import com.fredy.gamevault.features.games.domain.repositories.GameRepository
import javax.inject.Inject

class UpdateGameUseCase @Inject constructor(
    private val repository: GameRepository
) {
    suspend operator fun invoke(
        id: String,
        name: String? = null,
        description: String? = null,
        coverImageUrl: String? = null,
        status: GameStatus? = null,
        completed: Boolean? = null
    ): Result<Game> {
        return repository.updateGame(id, name, description, coverImageUrl, status, completed)
    }
}