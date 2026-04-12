package com.fredy.gamevault

import android.app.Application
import android.util.Log
import androidx.hilt.work.HiltWorkerFactory
import androidx.work.Configuration
import com.fredy.gamevault.core.notifications.NotificationChannelManager
import com.fredy.gamevault.core.worker.SyncScheduler
import dagger.hilt.android.HiltAndroidApp
import javax.inject.Inject

@HiltAndroidApp
class GameVaultApp : Application(), Configuration.Provider {

    @Inject
    lateinit var workerFactory: HiltWorkerFactory

    @Inject
    lateinit var syncScheduler: SyncScheduler

    @Inject
    lateinit var notificationChannelManager: NotificationChannelManager

    override val workManagerConfiguration: Configuration
        get() = Configuration.Builder()
            .setWorkerFactory(workerFactory)
            .setMinimumLoggingLevel(Log.DEBUG)
            .build()

    override fun onCreate() {
        super.onCreate()

        // Crear canales de notificación para FCM
        notificationChannelManager.createNotificationChannels()

        // Programar sincronización periódica con WorkManager
        syncScheduler.schedulePeriodicSync()
        syncScheduler.requestImmediateSync()

        Log.d("GameVaultApp", "App inicializada: canales de notificación y WorkManager configurados")
    }
}