package com.fredy.gamevault.core.network

import com.fredy.gamevault.features.auth.data.datasources.remote.model.AuthResponse
import com.fredy.gamevault.features.auth.data.datasources.remote.model.LoginRequest
import com.fredy.gamevault.features.auth.data.datasources.remote.model.RegisterRequest
import com.fredy.gamevault.features.games.data.datasources.remote.model.GameDto
import com.fredy.gamevault.features.games.data.datasources.remote.model.GameResponse
import com.fredy.gamevault.features.shared.model.GenericResponse
import retrofit2.http.*

interface GameVaultApi {

    // Auth endpoints
    @POST("auth/register")
    suspend fun register(@Body request: RegisterRequest): AuthResponse

    @POST("auth/login")
    suspend fun login(@Body request: LoginRequest): AuthResponse

    // Game endpoints (protegidos)
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
}