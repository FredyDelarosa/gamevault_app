package com.fredy.gamevault.features.community.domain.repositories

import com.fredy.gamevault.features.community.domain.entities.Post
import com.fredy.gamevault.features.community.domain.entities.PostType

interface PostRepository {
    suspend fun createPost(gameName: String, title: String, content: String, postType: PostType): Result<Post>
    suspend fun getAllPosts(): Result<List<Post>>
    suspend fun getPostsForMyGames(): Result<List<Post>>
    suspend fun getPostById(id: String): Result<Post>
    suspend fun deletePost(id: String): Result<Unit>
    suspend fun toggleReaction(id: String): Result<Boolean>
}