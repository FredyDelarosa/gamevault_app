package com.fredy.gamevault.features.wishlist.domain.usecases

import com.fredy.gamevault.features.games.domain.entities.GameStatus
import com.fredy.gamevault.features.games.domain.repositories.GameRepository
import javax.inject.Inject

class MoveToBacklogUseCase @Inject constructor(
    private val repository: GameRepository
) {
    suspend operator fun invoke(gameId: String): Result<Unit> {
        val result = repository.updateGame(
            id = gameId,
            name = null,
            description = null,
            coverImageUrl = null,
            status = GameStatus.BACKLOG,
            completed = null
        )
        return result.mapCatching { }
    }
}