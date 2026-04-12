package com.fredy.gamevault.features.wishlist.presentation.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.fredy.gamevault.core.hardware.HapticFeedback
import com.fredy.gamevault.features.wishlist.domain.usecases.GetWishlistGamesUseCase
import com.fredy.gamevault.features.wishlist.domain.usecases.RemoveFromWishlistUseCase
import com.fredy.gamevault.features.wishlist.domain.usecases.AddToWishlistUseCase
import com.fredy.gamevault.features.wishlist.domain.usecases.MoveToBacklogUseCase
import com.fredy.gamevault.features.wishlist.presentation.WishlistEvent
import com.fredy.gamevault.features.wishlist.presentation.WishlistUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class WishlistViewModel @Inject constructor(
    private val getWishlistGamesUseCase: GetWishlistGamesUseCase,
    private val addToWishlistUseCase: AddToWishlistUseCase,
    private val removeFromWishlistUseCase: RemoveFromWishlistUseCase,
    private val moveToBacklogUseCase: MoveToBacklogUseCase,
    private val hapticFeedback: HapticFeedback
) : ViewModel() {

    private val _uiState = MutableStateFlow(WishlistUiState())
    val uiState = _uiState.asStateFlow()

    fun handleEvent(event: WishlistEvent) {
        when (event) {
            is WishlistEvent.LoadGames -> loadWishlistGames()
            is WishlistEvent.DeleteGame -> deleteGame(event.gameId)
            is WishlistEvent.MoveGameToBacklog -> moveGameToBacklog(event.gameId)
            is WishlistEvent.SelectGame -> selectGame(event.gameId)
            is WishlistEvent.ClearError -> clearError()
        }
    }

    private fun loadWishlistGames() {
        _uiState.update { it.copy(isLoading = true, errorMessage = null) }

        viewModelScope.launch {
            val result = getWishlistGamesUseCase()

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
                            errorMessage = error.message ?: "Error al cargar la wishlist"
                        )
                    }
                }
            )
        }
    }

    private fun deleteGame(gameId: String) {
        viewModelScope.launch {
            val result = removeFromWishlistUseCase(gameId)

            result.fold(
                onSuccess = {
                    hapticFeedback.vibrateSuccess()
                    _uiState.update {
                        it.copy(
                            games = it.games.filter { game -> game.id != gameId }
                        )
                    }
                },
                onFailure = { error ->
                    hapticFeedback.vibrateError()
                    _uiState.update {
                        it.copy(
                            errorMessage = error.message ?: "Error al eliminar el juego"
                        )
                    }
                }
            )
        }
    }

    private fun moveGameToBacklog(gameId: String) {
        val currentGame = _uiState.value.games.find { it.id == gameId }
        if (currentGame == null) {
            _uiState.update {
                it.copy(errorMessage = "Juego no encontrado")
            }
            return
        }

        viewModelScope.launch {
            val result = moveToBacklogUseCase(gameId)

            result.fold(
                onSuccess = {
                    hapticFeedback.vibrateSuccess()
                    _uiState.update {
                        it.copy(
                            games = it.games.filter { game -> game.id != gameId },
                            errorMessage = "Juego movido a Backlog"
                        )
                    }
                },
                onFailure = { error ->
                    hapticFeedback.vibrateError()
                    _uiState.update {
                        it.copy(
                            errorMessage = error.message ?: "Error al mover el juego a Backlog"
                        )
                    }
                }
            )
        }
    }

    private fun selectGame(gameId: String) {
        _uiState.update { it.copy(selectedGameId = gameId) }
    }

    private fun clearError() {
        _uiState.update { it.copy(errorMessage = null) }
    }
}