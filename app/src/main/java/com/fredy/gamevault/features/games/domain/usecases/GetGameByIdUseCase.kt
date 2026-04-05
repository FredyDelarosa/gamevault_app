package com.fredy.gamevault.features.games.domain.usecases

import com.fredy.gamevault.features.games.domain.entities.Game
import com.fredy.gamevault.features.games.domain.repositories.GameRepository
import javax.inject.Inject

class GetGameByIdUseCase @Inject constructor(
    private val repository: GameRepository
) {
    suspend operator fun invoke(id: String): Result<Game> {
        return repository.getGameById(id)
    }
}