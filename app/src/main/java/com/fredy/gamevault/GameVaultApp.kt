package com.fredy.gamevault

import android.app.Application
import com.fredy.gamevault.features.games.data.repositories.GameRepositoryImpl
import dagger.hilt.android.HiltAndroidApp
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import okhttp3.Dispatcher
import javax.inject.Inject

@HiltAndroidApp
class GameVaultApp : Application(){
    @Inject
    lateinit var gameRepository: GameRepositoryImpl

    override fun onCreate() {
        super.onCreate()
        CoroutineScope(Dispatchers.IO).launch {
            gameRepository.syncPendingGames()
        }
    }
}
