package com.fredy.gamevault.features.backlog.presentation.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.fredy.gamevault.core.hardware.HapticFeedback
import com.fredy.gamevault.features.backlog.domain.usecases.GetBacklogGamesUseCase
import com.fredy.gamevault.features.backlog.domain.usecases.MoveToNowPlayingUseCase
import com.fredy.gamevault.features.backlog.domain.usecases.MoveToWishlistUseCase
import com.fredy.gamevault.features.backlog.presentation.BacklogUiState
import com.fredy.gamevault.features.games.domain.usecases.DeleteGameUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class BacklogViewModel @Inject constructor(
    private val getBacklogGamesUseCase: GetBacklogGamesUseCase,
    private val moveToNowPlayingUseCase: MoveToNowPlayingUseCase,
    private val moveToWishlistUseCase: MoveToWishlistUseCase,
    private val deleteGameUseCase: DeleteGameUseCase,
    private val hapticFeedback: HapticFeedback
) : ViewModel() {

    private val _uiState = MutableStateFlow(BacklogUiState())
    val uiState = _uiState.asStateFlow()

    fun loadBacklogGames() {
        _uiState.update { it.copy(isLoading = true, errorMessage = null) }

        viewModelScope.launch {
            val result = getBacklogGamesUseCase()

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
                            errorMessage = error.message ?: "Error al cargar el backlog"
                        )
                    }
                }
            )
        }
    }

    fun moveToNowPlaying(gameId: String) {
        viewModelScope.launch {
            val result = moveToNowPlayingUseCase(gameId)

            result.fold(
                onSuccess = { game ->
                    hapticFeedback.vibrateSuccess()
                    _uiState.update {
                        it.copy(
                            successMessage = "Juego movido a Now Playing",
                            errorMessage = null
                        )
                    }
                    loadBacklogGames()
                },
                onFailure = { error ->
                    hapticFeedback.vibrateError()
                    _uiState.update {
                        it.copy(
                            errorMessage = error.message ?: "Error al mover a Now Playing"
                        )
                    }
                }
            )
        }
    }

    fun moveToWishlist(gameId: String) {
        viewModelScope.launch {
            val result = moveToWishlistUseCase(gameId)

            result.fold(
                onSuccess = { game ->
                    hapticFeedback.vibrate()
                    _uiState.update {
                        it.copy(
                            successMessage = "Juego movido a Wishlist",
                            errorMessage = null
                        )
                    }
                    loadBacklogGames()
                },
                onFailure = { error ->
                    hapticFeedback.vibrateError()
                    _uiState.update {
                        it.copy(
                            errorMessage = error.message ?: "Error al mover a Wishlist"
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
                    loadBacklogGames()
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