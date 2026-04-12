package com.fredy.gamevault.core.worker

import android.util.Log
import androidx.work.BackoffPolicy
import androidx.work.Constraints
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.ExistingWorkPolicy
import androidx.work.NetworkType
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import java.util.concurrent.TimeUnit
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SyncScheduler @Inject constructor(
    private val workManager: WorkManager
) {

    companion object {
        private const val TAG = "SyncScheduler"
    }

    /**
     * Restricciones para la sincronización:
     * - Requiere conexión a internet (cualquier tipo)
     * - Requiere que la batería no esté baja
     */
    private val syncConstraints = Constraints.Builder()
        .setRequiredNetworkType(NetworkType.CONNECTED)
        .setRequiresBatteryNotLow(true)
        .build()

    /**
     * Programa la sincronización periódica cada 30 minutos.
     * Usa KEEP para no reemplazar si ya está programada.
     */
    fun schedulePeriodicSync() {
        Log.d(TAG, "Programando sincronización periódica cada 30 minutos")

        val periodicRequest = PeriodicWorkRequestBuilder<SyncGamesWorker>(
            repeatInterval = 30,
            repeatIntervalTimeUnit = TimeUnit.MINUTES
        )
            .setConstraints(syncConstraints)
            .setBackoffCriteria(
                BackoffPolicy.EXPONENTIAL,
                backoffDelay = 15,
                timeUnit = TimeUnit.SECONDS
            )
            .addTag(SyncGamesWorker.TAG)
            .build()

        workManager.enqueueUniquePeriodicWork(
            SyncGamesWorker.WORK_NAME_PERIODIC,
            ExistingPeriodicWorkPolicy.KEEP,
            periodicRequest
        )

        Log.d(TAG, "Sincronización periódica programada exitosamente")
    }

    /**
     * Dispara una sincronización inmediata (one-time).
     * Útil cuando el usuario crea/edita un juego en modo offline.
     * Usa REPLACE para cancelar cualquier one-time anterior pendiente.
     */
    fun requestImmediateSync() {
        Log.d(TAG, "Solicitando sincronización inmediata")

        val oneTimeRequest = OneTimeWorkRequestBuilder<SyncGamesWorker>()
            .setConstraints(syncConstraints)
            .setBackoffCriteria(
                BackoffPolicy.EXPONENTIAL,
                backoffDelay = 15,
                timeUnit = TimeUnit.SECONDS
            )
            .addTag(SyncGamesWorker.TAG)
            .build()

        workManager.enqueueUniqueWork(
            SyncGamesWorker.WORK_NAME_ONE_TIME,
            ExistingWorkPolicy.REPLACE,
            oneTimeRequest
        )

        Log.d(TAG, "Sincronización inmediata encolada")
    }

    /**
     * Cancela toda la sincronización programada.
     * Útil al cerrar sesión.
     */
    fun cancelAllSync() {
        Log.d(TAG, "Cancelando toda la sincronización programada")
        workManager.cancelUniqueWork(SyncGamesWorker.WORK_NAME_PERIODIC)
        workManager.cancelUniqueWork(SyncGamesWorker.WORK_NAME_ONE_TIME)
    }
}