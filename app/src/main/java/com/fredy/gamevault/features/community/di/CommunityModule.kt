package com.fredy.gamevault.features.community.di

import com.fredy.gamevault.features.community.data.repositories.PostRepositoryImpl
import com.fredy.gamevault.features.community.domain.repositories.PostRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class CommunityModule {

    @Binds
    @Singleton
    abstract fun bindPostRepository(impl: PostRepositoryImpl): PostRepository
}