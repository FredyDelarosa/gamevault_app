package com.fredy.gamevault.features.auth.data.repositories

import com.fredy.gamevault.core.network.GameVaultApi
import com.fredy.gamevault.core.session.SessionManager
import com.fredy.gamevault.features.auth.data.datasources.remote.mapper.toDomain
import com.fredy.gamevault.features.auth.data.datasources.remote.model.LoginRequest
import com.fredy.gamevault.features.auth.data.datasources.remote.model.RegisterRequest
import com.fredy.gamevault.features.auth.domain.entities.User
import com.fredy.gamevault.features.auth.domain.repositories.AuthRepository
import android.util.Log
import javax.inject.Inject

class AuthRepositoryImpl @Inject constructor(
    private val api: GameVaultApi,
    private val sessionManager: SessionManager
) : AuthRepository {

    companion object {
        private const val TAG = "AuthRepositoryImpl"
    }

    override suspend fun login(email: String, password: String): Result<Pair<User, String>> {
        return try {
            val response = api.login(LoginRequest(email, password))
            val user = response.user.toDomain()
            val token = response.token

            // Guardar sesión
            sessionManager.saveAuthToken(token)
            sessionManager.saveUserSession(user.id, user.email, user.firstName, user.lastName)

            Result.success(Pair(user, token))
        } catch (e: Exception) {
            Log.e(TAG, "Login request failed", e)
            Result.failure(e)
        }
    }

    override suspend fun register(
        email: String,
        password: String,
        firstName: String,
        lastName: String
    ): Result<User> {
        return try {
            val response = api.register(RegisterRequest(email, password, firstName, lastName))
            Result.success(response.user.toDomain())
        } catch (e: Exception) {
            Log.e(TAG, "Register request failed", e)
            Result.failure(e)
        }
    }

    override suspend fun logout() {
        sessionManager.clearSession()
    }
}