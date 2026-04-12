package com.fredy.gamevault.core.notifications

import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.util.Log
import androidx.core.app.NotificationCompat
import com.fredy.gamevault.MainActivity
import com.fredy.gamevault.R
import com.fredy.gamevault.core.network.GameVaultApi
import com.fredy.gamevault.core.session.SessionManager
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class GameVaultMessagingService : FirebaseMessagingService() {

    @Inject
    lateinit var api: GameVaultApi

    @Inject
    lateinit var sessionManager: SessionManager

    companion object {
        private const val TAG = "FCMService"
    }

    /**
     * Se llama cuando se recibe un nuevo token FCM.
     * Enviamos el token al backend para que pueda enviar notificaciones a este dispositivo.
     */
    override fun onNewToken(token: String) {
        super.onNewToken(token)
        Log.d(TAG, "Nuevo token FCM recibido: ${token.take(20)}...")
        sendTokenToServer(token)
    }

    /**
     * Se llama cuando se recibe un mensaje push.
     * Construye y muestra la notificación usando el canal apropiado.
     */
    override fun onMessageReceived(message: RemoteMessage) {
        super.onMessageReceived(message)
        Log.d(TAG, "Mensaje recibido de: ${message.from}")

        // Determinar el canal según el tipo de notificación
        val channelType = message.data["channel"] ?: "game_updates"
        val channelId = when (channelType) {
            "sync" -> NotificationChannelManager.CHANNEL_SYNC
            else -> NotificationChannelManager.CHANNEL_GAMES
        }

        // Obtener título y cuerpo (de notification o de data payload)
        val title = message.notification?.title
            ?: message.data["title"]
            ?: "GameVault"
        val body = message.notification?.body
            ?: message.data["body"]
            ?: "Tienes una nueva actualización"

        showNotification(title, body, channelId)
    }

    private fun showNotification(title: String, body: String, channelId: String) {
        val intent = Intent(this, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }

        val pendingIntent = PendingIntent.getActivity(
            this, 0, intent,
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )

        val notification = NotificationCompat.Builder(this, channelId)
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setContentTitle(title)
            .setContentText(body)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)
            .build()

        val notificationManager =
            getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        // Usar el hashCode del timestamp como ID único
        notificationManager.notify(System.currentTimeMillis().toInt(), notification)
    }

    private fun sendTokenToServer(token: String) {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val authToken = sessionManager.getToken()
                if (authToken != null) {
                    api.saveFcmToken(
                        com.fredy.gamevault.features.auth.data.datasources.remote.model.FcmTokenRequest(token)
                    )
                    Log.d(TAG, "Token FCM enviado al servidor exitosamente")
                } else {
                    Log.d(TAG, "Sin sesión activa, token FCM pendiente de enviar")
                }
            } catch (e: Exception) {
                Log.e(TAG, "Error enviando token FCM al servidor: ${e.message}")
            }
        }
    }
}