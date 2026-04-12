package com.fredy.gamevault.features.wishlist.domain.usecases

import com.fredy.gamevault.features.games.domain.entities.Game
import com.fredy.gamevault.features.games.domain.entities.GameStatus
import com.fredy.gamevault.features.games.domain.repositories.GameRepository
import javax.inject.Inject

class GetWishlistGamesUseCase @Inject constructor(
    private val repository: GameRepository
) {
    suspend operator fun invoke(): Result<List<Game>> {
        return repository.getGames(GameStatus.WISHLIST)
    }
}