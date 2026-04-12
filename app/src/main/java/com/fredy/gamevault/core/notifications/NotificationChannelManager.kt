package com.fredy.gamevault.core.notifications

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.util.Log
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class NotificationChannelManager @Inject constructor(
    @ApplicationContext private val context: Context
) {

    companion object {
        const val CHANNEL_GAMES = "game_updates"
        const val CHANNEL_SYNC = "sync_notifications"
        private const val TAG = "NotificationChannels"
    }

    fun createNotificationChannels() {
        val notificationManager =
            context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        // Canal 1: Actualizaciones de juegos
        val gamesChannel = NotificationChannel(
            CHANNEL_GAMES,
            "Actualizaciones de Juegos",
            NotificationManager.IMPORTANCE_DEFAULT
        ).apply {
            description = "Notificaciones sobre cambios en tus juegos"
            enableVibration(true)
        }

        // Canal 2: Sincronización
        val syncChannel = NotificationChannel(
            CHANNEL_SYNC,
            "Sincronización",
            NotificationManager.IMPORTANCE_LOW
        ).apply {
            description = "Notificaciones sobre sincronización de datos"
        }

        notificationManager.createNotificationChannel(gamesChannel)
        notificationManager.createNotificationChannel(syncChannel)

        Log.d(TAG, "Canales de notificación creados exitosamente")
    }
}