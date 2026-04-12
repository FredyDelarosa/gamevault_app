package com.fredy.gamevault.features.wishlist.domain.usecases

import com.fredy.gamevault.features.games.domain.repositories.GameRepository
import javax.inject.Inject

class RemoveFromWishlistUseCase @Inject constructor(
    private val repository: GameRepository
) {
    suspend operator fun invoke(gameId: String): Result<Unit> {
        return repository.deleteGame(gameId)
    }
}