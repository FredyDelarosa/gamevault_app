package com.fredy.gamevault.features.games.presentation

import com.fredy.gamevault.features.games.domain.entities.Game
import com.fredy.gamevault.features.games.domain.entities.GameStatus

data class GameFormUiState(
    val gameId: String? = null,
    val name: String = "",
    val description: String = "",
    val coverImageUrl: String = "",
    val status: GameStatus = GameStatus.WISHLIST,
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val isEditMode: Boolean = false
)

sealed class GameFormEvent {
    data class NameChanged(val name: String) : GameFormEvent()
    data class DescriptionChanged(val description: String) : GameFormEvent()
    data class CoverImageUrlChanged(val url: String) : GameFormEvent()
    data class StatusChanged(val status: GameStatus) : GameFormEvent()
    data class LoadGame(val gameId: String) : GameFormEvent()
    object Submit : GameFormEvent()
    object Reset : GameFormEvent()
}