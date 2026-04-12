package com.fredy.gamevault

import android.app.Application
import android.util.Log
import androidx.hilt.work.HiltWorkerFactory
import androidx.work.Configuration
import com.fredy.gamevault.core.worker.SyncScheduler
import dagger.hilt.android.HiltAndroidApp
import javax.inject.Inject

@HiltAndroidApp
class GameVaultApp : Application(), Configuration.Provider {

    @Inject
    lateinit var workerFactory: HiltWorkerFactory

    @Inject
    lateinit var syncScheduler: SyncScheduler

    override val workManagerConfiguration: Configuration
        get() = Configuration.Builder()
            .setWorkerFactory(workerFactory)
            .setMinimumLoggingLevel(Log.DEBUG)
            .build()

    override fun onCreate() {
        super.onCreate()

        // Programar sincronización periódica con WorkManager
        // Reemplaza el CoroutineScope anterior, respetando batería y conectividad
        syncScheduler.schedulePeriodicSync()

        // Disparar una sincronización inmediata al abrir la app
        // para sincronizar juegos pendientes lo antes posible
        syncScheduler.requestImmediateSync()

        Log.d("GameVaultApp", "WorkManager inicializado y sincronización programada")
    }
}