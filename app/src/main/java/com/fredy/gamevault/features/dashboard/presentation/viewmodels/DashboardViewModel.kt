package com.fredy.gamevault.features.dashboard.presentation.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.fredy.gamevault.core.hardware.HapticFeedback
import com.fredy.gamevault.features.dashboard.domain.usecases.GetNowPlayingGamesUseCase
import com.fredy.gamevault.features.dashboard.domain.usecases.MarkGameAsCompletedUseCase
import com.fredy.gamevault.features.dashboard.domain.usecases.MoveGameToBacklogUseCase
import com.fredy.gamevault.features.dashboard.presentation.DashboardUiState
import com.fredy.gamevault.features.games.domain.usecases.DeleteGameUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class DashboardViewModel @Inject constructor(
    private val getNowPlayingGamesUseCase: GetNowPlayingGamesUseCase,
    private val markGameAsCompletedUseCase: MarkGameAsCompletedUseCase,
    private val moveGameToBacklogUseCase: MoveGameToBacklogUseCase,
    private val deleteGameUseCase: DeleteGameUseCase,
    private val hapticFeedback: HapticFeedback
) : ViewModel() {

    private val _uiState = MutableStateFlow(DashboardUiState())
    val uiState = _uiState.asStateFlow()

    fun loadNowPlayingGames() {
        _uiState.update { it.copy(isLoading = true, errorMessage = null) }

        viewModelScope.launch {
            val result = getNowPlayingGamesUseCase()

            result.fold(
                onSuccess = { games ->
                    _uiState.update {
                        it.copy(
                            games = games,
                            isLoading = false
                        )
                    }
                },
                onFailure = { error ->
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            errorMessage = error.message ?: "Error al cargar los juegos"
                        )
                    }
                }
            )
        }
    }

    fun markGameAsCompleted(gameId: String) {
        viewModelScope.launch {
            val result = markGameAsCompletedUseCase(gameId, true)

            result.fold(
                onSuccess = { _ ->
                    hapticFeedback.vibrateSuccess()
                    _uiState.update {
                        it.copy(
                            successMessage = "¡Juego completado!",
                            errorMessage = null
                        )
                    }
                    loadNowPlayingGames()
                },
                onFailure = { error ->
                    hapticFeedback.vibrateError()
                    _uiState.update {
                        it.copy(
                            errorMessage = error.message ?: "Error al marcar como completado"
                        )
                    }
                }
            )
        }
    }

    fun moveGameToBacklog(gameId: String) {
        viewModelScope.launch {
            val result = moveGameToBacklogUseCase(gameId)

            result.fold(
                onSuccess = { _ ->
                    hapticFeedback.vibrate()
                    _uiState.update {
                        it.copy(
                            successMessage = "Juego movido a Backlog",
                            errorMessage = null
                        )
                    }
                    loadNowPlayingGames()
                },
                onFailure = { error ->
                    hapticFeedback.vibrateError()
                    _uiState.update {
                        it.copy(
                            errorMessage = error.message ?: "Error al mover a Backlog"
                        )
                    }
                }
            )
        }
    }

    fun deleteGame(gameId: String) {
        viewModelScope.launch {
            val result = deleteGameUseCase(gameId)

            result.fold(
                onSuccess = {
                    hapticFeedback.vibrate()
                    _uiState.update {
                        it.copy(
                            successMessage = "Juego eliminado",
                            errorMessage = null
                        )
                    }
                    loadNowPlayingGames()
                },
                onFailure = { error ->
                    hapticFeedback.vibrateError()
                    _uiState.update {
                        it.copy(
                            errorMessage = error.message ?: "Error al eliminar juego"
                        )
                    }
                }
            )
        }
    }

    fun clearMessages() {
        _uiState.update {
            it.copy(
                successMessage = null,
                errorMessage = null
            )
        }
    }
}