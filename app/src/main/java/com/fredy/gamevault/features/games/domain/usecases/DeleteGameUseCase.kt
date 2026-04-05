package com.fredy.gamevault.features.games.domain.usecases

import com.fredy.gamevault.features.games.domain.repositories.GameRepository
import javax.inject.Inject

class DeleteGameUseCase @Inject constructor(
    private val repository: GameRepository
) {
    suspend operator fun invoke(id: String): Result<Unit> {
        return repository.deleteGame(id)
    }
}