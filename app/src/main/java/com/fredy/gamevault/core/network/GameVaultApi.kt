package com.fredy.gamevault.core.network

import com.fredy.gamevault.features.auth.data.datasources.remote.model.AuthResponse
import com.fredy.gamevault.features.auth.data.datasources.remote.model.FcmTokenRequest
import com.fredy.gamevault.features.auth.data.datasources.remote.model.LoginRequest
import com.fredy.gamevault.features.auth.data.datasources.remote.model.RegisterRequest
import com.fredy.gamevault.features.community.data.datasources.remote.model.CreatePostRequest
import com.fredy.gamevault.features.community.data.datasources.remote.model.PostResponse
import com.fredy.gamevault.features.games.data.datasources.remote.model.GameDto
import com.fredy.gamevault.features.games.data.datasources.remote.model.GameResponse
import com.fredy.gamevault.features.shared.model.GenericResponse
import retrofit2.http.*

interface GameVaultApi {

    @POST("auth/register")
    suspend fun register(@Body request: RegisterRequest): AuthResponse

    @POST("auth/login")
    suspend fun login(@Body request: LoginRequest): AuthResponse

    @GET("games")
    suspend fun getGames(@Query("status") status: String? = null): List<GameResponse>

    @GET("games/{id}")
    suspend fun getGameById(@Path("id") id: String): GameResponse

    @POST("games")
    suspend fun createGame(@Body game: GameDto): GameResponse

    @PUT("games/{id}")
    suspend fun updateGame(@Path("id") id: String, @Body game: GameDto): GameResponse

    @DELETE("games/{id}")
    suspend fun deleteGame(@Path("id") id: String): GenericResponse

    @POST("notifications/token")
    suspend fun saveFcmToken(@Body request: FcmTokenRequest): GenericResponse

    // Community / Posts
    @POST("posts")
    suspend fun createPost(@Body request: CreatePostRequest): PostResponse

    @GET("posts")
    suspend fun getAllPosts(
        @Query("limit") limit: Int = 20,
        @Query("offset") offset: Int = 0
    ): List<PostResponse>

    @GET("posts/my-games")
    suspend fun getPostsForMyGames(
        @Query("limit") limit: Int = 20,
        @Query("offset") offset: Int = 0
    ): List<PostResponse>

    @GET("posts/{id}")
    suspend fun getPostById(@Path("id") id: String): PostResponse

    @DELETE("posts/{id}")
    suspend fun deletePost(@Path("id") id: String): GenericResponse

    @POST("posts/{id}/react")
    suspend fun reactToPost(@Path("id") id: String): GenericResponse
}