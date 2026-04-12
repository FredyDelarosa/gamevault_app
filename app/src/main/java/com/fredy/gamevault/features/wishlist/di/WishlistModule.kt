package com.fredy.gamevault.features.wishlist.di

import com.fredy.gamevault.features.games.domain.repositories.GameRepository
import com.fredy.gamevault.features.wishlist.domain.usecases.AddToWishlistUseCase
import com.fredy.gamevault.features.wishlist.domain.usecases.GetWishlistGamesUseCase
import com.fredy.gamevault.features.wishlist.domain.usecases.RemoveFromWishlistUseCase
import com.fredy.gamevault.features.wishlist.domain.usecases.MoveToBacklogUseCase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object WishlistModule {

    @Provides
    @Singleton
    fun provideAddToWishlistUseCase(
        repository: GameRepository
    ): AddToWishlistUseCase {
        return AddToWishlistUseCase(repository)
    }

    @Provides
    @Singleton
    fun provideGetWishlistGamesUseCase(
        repository: GameRepository
    ): GetWishlistGamesUseCase {
        return GetWishlistGamesUseCase(repository)
    }

    @Provides
    @Singleton
    fun provideRemoveFromWishlistUseCase(
        repository: GameRepository
    ): RemoveFromWishlistUseCase {
        return RemoveFromWishlistUseCase(repository)
    }

    @Provides
    @Singleton
    fun provideMoveToBacklogUseCase(
        repository: GameRepository
    ): MoveToBacklogUseCase {
        return MoveToBacklogUseCase(repository)
    }
}