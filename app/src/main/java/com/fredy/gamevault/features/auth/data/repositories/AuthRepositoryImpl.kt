package com.fredy.gamevault.features.auth.data.repositories

import com.fredy.gamevault.core.network.GameVaultApi
import com.fredy.gamevault.core.session.SessionManager
import com.fredy.gamevault.core.worker.SyncScheduler
import com.fredy.gamevault.features.auth.data.datasources.remote.mapper.toDomain
import com.fredy.gamevault.features.auth.data.datasources.remote.model.FcmTokenRequest
import com.fredy.gamevault.features.auth.data.datasources.remote.model.LoginRequest
import com.fredy.gamevault.features.auth.data.datasources.remote.model.RegisterRequest
import com.fredy.gamevault.features.auth.domain.entities.User
import com.fredy.gamevault.features.auth.domain.repositories.AuthRepository
import android.util.Log
import com.google.firebase.messaging.FirebaseMessaging
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class AuthRepositoryImpl @Inject constructor(
    private val api: GameVaultApi,
    private val sessionManager: SessionManager,
    private val syncScheduler: SyncScheduler
) : AuthRepository {

    companion object {
        private const val TAG = "AuthRepositoryImpl"
    }

    override suspend fun login(email: String, password: String): Result<Pair<User, String>> {
        return try {
            val response = api.login(LoginRequest(email, password))
            val user = response.user.toDomain()
            val token = response.token

            sessionManager.saveAuthToken(token)
            sessionManager.saveUserSession(user.id, user.email, user.firstName, user.lastName)

            // Enviar token FCM al backend después del login exitoso
            sendFcmTokenToServer()

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
        syncScheduler.cancelAllSync()
        sessionManager.clearSession()
    }

    /**
     * Obtiene el token FCM del dispositivo y lo envía al backend.
     * Esto permite que el servidor envíe push notifications a este dispositivo.
     */
    private suspend fun sendFcmTokenToServer() {
        try {
            val fcmToken = FirebaseMessaging.getInstance().token.await()
            Log.d(TAG, "Token FCM obtenido: ${fcmToken.take(20)}...")

            api.saveFcmToken(FcmTokenRequest(fcmToken))
            Log.d(TAG, "Token FCM enviado al servidor exitosamente")
        } catch (e: Exception) {
            Log.e(TAG, "Error enviando token FCM: ${e.message}")
            // No fallar el login si el token FCM no se puede enviar
        }
    }
}