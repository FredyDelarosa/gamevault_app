package com.fredy.gamevault.features.backlog.presentation

import com.fredy.gamevault.features.games.domain.entities.Game

data class BacklogUiState(
    val games: List<Game> = emptyList(),
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val successMessage: String? = null
)