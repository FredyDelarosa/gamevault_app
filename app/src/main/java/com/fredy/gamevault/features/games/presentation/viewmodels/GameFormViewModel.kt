package com.fredy.gamevault.features.games.presentation.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.fredy.gamevault.core.hardware.HapticFeedback
import com.fredy.gamevault.features.games.domain.entities.GameStatus
import com.fredy.gamevault.features.games.domain.usecases.CreateGameUseCase
import com.fredy.gamevault.features.games.domain.usecases.GetGameByIdUseCase
import com.fredy.gamevault.features.games.domain.usecases.UpdateGameUseCase
import com.fredy.gamevault.features.games.presentation.GameFormEvent
import com.fredy.gamevault.features.games.presentation.GameFormUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class GameFormViewModel @Inject constructor(
    private val createGameUseCase: CreateGameUseCase,
    private val updateGameUseCase: UpdateGameUseCase,
    private val getGameByIdUseCase: GetGameByIdUseCase,
    private val hapticFeedback: HapticFeedback
) : ViewModel() {

    private val _uiState = MutableStateFlow(GameFormUiState())
    val uiState = _uiState.asStateFlow()

    fun handleEvent(event: GameFormEvent) {
        when (event) {
            is GameFormEvent.NameChanged -> {
                _uiState.update { it.copy(name = event.name, errorMessage = null) }
            }
            is GameFormEvent.DescriptionChanged -> {
                _uiState.update { it.copy(description = event.description, errorMessage = null) }
            }
            is GameFormEvent.CoverImageUrlChanged -> {
                _uiState.update { it.copy(coverImageUrl = event.url, errorMessage = null) }
            }
            is GameFormEvent.StatusChanged -> {
                _uiState.update { it.copy(status = event.status, errorMessage = null) }
            }
            is GameFormEvent.LoadGame -> {
                loadGame(event.gameId)
            }
            is GameFormEvent.Submit -> {
                submitForm()
            }
            is GameFormEvent.Reset -> {
                _uiState.update { GameFormUiState() }
            }
        }
    }

    private fun loadGame(gameId: String) {
        _uiState.update { it.copy(isLoading = true, errorMessage = null) }

        viewModelScope.launch {
            val result = getGameByIdUseCase(gameId)

            result.fold(
                onSuccess = { game ->
                    _uiState.update {
                        it.copy(
                            gameId = game.id,
                            name = game.name,
                            description = game.description,
                            coverImageUrl = game.coverImageUrl,
                            status = game.status,
                            isLoading = false,
                            isEditMode = true
                        )
                    }
                },
                onFailure = { error ->
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            errorMessage = error.message ?: "Error al cargar el juego"
                        )
                    }
                }
            )
        }
    }

    private fun submitForm() {
        val currentState = _uiState.value

        // Validaciones
        if (currentState.name.isBlank()) {
            _uiState.update {
                it.copy(errorMessage = "El nombre del juego es requerido")
            }
            hapticFeedback.vibrateError()
            return
        }

        if (currentState.name.length < 3) {
            _uiState.update {
                it.copy(errorMessage = "El nombre debe tener al menos 3 caracteres")
            }
            hapticFeedback.vibrateError()
            return
        }

        _uiState.update { it.copy(isLoading = true, errorMessage = null) }

        viewModelScope.launch {
            val result = if (currentState.isEditMode && currentState.gameId != null) {
                updateGameUseCase(
                    id = currentState.gameId,
                    name = currentState.name,
                    description = currentState.description,
                    coverImageUrl = currentState.coverImageUrl.takeIf { it.isNotBlank() },
                    status = currentState.status,
                    completed = null
                )
            } else {
                createGameUseCase(
                    name = currentState.name,
                    description = currentState.description,
                    coverImageUrl = currentState.coverImageUrl,
                    status = currentState.status
                )
            }

            result.fold(
                onSuccess = { game ->
                    hapticFeedback.vibrateSuccess()
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            errorMessage = null
                        )
                    }
                    // El dialog se cerrará desde la UI
                },
                onFailure = { error ->
                    hapticFeedback.vibrateError()
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            errorMessage = error.message ?: "Error al guardar el juego"
                        )
                    }
                }
            )
        }
    }
}