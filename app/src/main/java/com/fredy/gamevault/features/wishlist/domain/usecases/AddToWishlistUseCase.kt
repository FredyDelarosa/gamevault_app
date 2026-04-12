package com.fredy.gamevault.features.wishlist.domain.usecases

import com.fredy.gamevault.features.games.domain.entities.Game
import com.fredy.gamevault.features.games.domain.entities.GameStatus
import com.fredy.gamevault.features.games.domain.repositories.GameRepository
import javax.inject.Inject

class AddToWishlistUseCase @Inject constructor(
    private val repository: GameRepository
) {
    suspend operator fun invoke(
        name: String,
        description: String,
        coverImageUrl: String
    ): Result<Game> {
        return repository.createGame(
            name = name,
            description = description,
            coverImageUrl = coverImageUrl,
            status = GameStatus.WISHLIST
        )
    }
}