package com.fredy.gamevault.features.community.data.repositories

import com.fredy.gamevault.core.network.GameVaultApi
import com.fredy.gamevault.features.community.data.datasources.remote.mapper.toDomain
import com.fredy.gamevault.features.community.data.datasources.remote.model.CreatePostRequest
import com.fredy.gamevault.features.community.domain.entities.Post
import com.fredy.gamevault.features.community.domain.entities.PostType
import com.fredy.gamevault.features.community.domain.repositories.PostRepository
import javax.inject.Inject

class PostRepositoryImpl @Inject constructor(
    private val api: GameVaultApi
) : PostRepository {

    override suspend fun createPost(
        gameName: String, title: String, content: String, postType: PostType
    ): Result<Post> = try {
        val response = api.createPost(
            CreatePostRequest(gameName, title, content, postType.name)
        )
        Result.success(response.toDomain())
    } catch (e: Exception) {
        Result.failure(e)
    }

    override suspend fun getAllPosts(): Result<List<Post>> = try {
        val response = api.getAllPosts()
        Result.success(response.map { it.toDomain() })
    } catch (e: Exception) {
        Result.failure(e)
    }

    override suspend fun getPostsForMyGames(): Result<List<Post>> = try {
        val response = api.getPostsForMyGames()
        Result.success(response.map { it.toDomain() })
    } catch (e: Exception) {
        Result.failure(e)
    }

    override suspend fun getPostById(id: String): Result<Post> = try {
        val response = api.getPostById(id)
        Result.success(response.toDomain())
    } catch (e: Exception) {
        Result.failure(e)
    }

    override suspend fun deletePost(id: String): Result<Unit> = try {
        api.deletePost(id)
        Result.success(Unit)
    } catch (e: Exception) {
        Result.failure(e)
    }

    override suspend fun toggleReaction(id: String): Result<Boolean> = try {
        val response = api.reactToPost(id)
        Result.success(response.message == "reaction added")
    } catch (e: Exception) {
        Result.failure(e)
    }
}