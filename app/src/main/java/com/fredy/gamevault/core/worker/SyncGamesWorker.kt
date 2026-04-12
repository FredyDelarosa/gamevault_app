package com.fredy.gamevault.core.worker

import android.content.Context
import android.util.Log
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.fredy.gamevault.features.games.data.repositories.GameRepositoryImpl
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject

@HiltWorker
class SyncGamesWorker @AssistedInject constructor(
    @Assisted context: Context,
    @Assisted workerParams: WorkerParameters,
    private val gameRepository: GameRepositoryImpl
) : CoroutineWorker(context, workerParams) {

    companion object {
        const val TAG = "SyncGamesWorker"
        const val WORK_NAME_PERIODIC = "sync_games_periodic"
        const val WORK_NAME_ONE_TIME = "sync_games_one_time"
    }

    override suspend fun doWork(): Result {
        Log.d(TAG, "Iniciando sincronización de juegos pendientes...")

        return try {
            val result = gameRepository.syncPendingGames()

            result.fold(
                onSuccess = {
                    Log.d(TAG, "Sincronización completada exitosamente")
                    Result.success()
                },
                onFailure = { error ->
                    Log.e(TAG, "Error en sincronización: ${error.message}")
                    if (runAttemptCount < 3) {
                        Log.d(TAG, "Reintentando... intento ${runAttemptCount + 1}/3")
                        Result.retry()
                    } else {
                        Log.e(TAG, "Máximo de reintentos alcanzado")
                        Result.failure()
                    }
                }
            )
        } catch (e: Exception) {
            Log.e(TAG, "Excepción durante sincronización: ${e.message}", e)
            if (runAttemptCount < 3) {
                Result.retry()
            } else {
                Result.failure()
            }
        }
    }
}