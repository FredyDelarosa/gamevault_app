package com.fredy.gamevault.features.community.presentation.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.fredy.gamevault.core.hardware.HapticFeedback
import com.fredy.gamevault.features.community.domain.repositories.PostRepository
import com.fredy.gamevault.features.community.presentation.CreatePostEvent
import com.fredy.gamevault.features.community.presentation.CreatePostUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CreatePostViewModel @Inject constructor(
    private val postRepository: PostRepository,
    private val hapticFeedback: HapticFeedback
) : ViewModel() {

    private val _uiState = MutableStateFlow(CreatePostUiState())
    val uiState = _uiState.asStateFlow()

    fun handleEvent(event: CreatePostEvent) {
        when (event) {
            is CreatePostEvent.GameNameChanged -> _uiState.update { it.copy(gameName = event.name, errorMessage = null) }
            is CreatePostEvent.TitleChanged -> _uiState.update { it.copy(title = event.title, errorMessage = null) }
            is CreatePostEvent.ContentChanged -> _uiState.update { it.copy(content = event.content) }
            is CreatePostEvent.TypeChanged -> _uiState.update { it.copy(postType = event.type) }
            is CreatePostEvent.Submit -> submit()
            is CreatePostEvent.Reset -> _uiState.update { CreatePostUiState() }
        }
    }

    private fun submit() {
        val state = _uiState.value

        if (state.gameName.isBlank()) {
            _uiState.update { it.copy(errorMessage = "El nombre del juego es requerido") }
            hapticFeedback.vibrateError()
            return
        }
        if (state.title.isBlank()) {
            _uiState.update { it.copy(errorMessage = "El título es requerido") }
            hapticFeedback.vibrateError()
            return
        }

        _uiState.update { it.copy(isLoading = true, errorMessage = null) }

        viewModelScope.launch {
            val result = postRepository.createPost(
                gameName = state.gameName,
                title = state.title,
                content = state.content,
                postType = state.postType
            )

            result.fold(
                onSuccess = {
                    hapticFeedback.vibrateSuccess()
                    _uiState.update { it.copy(isLoading = false, success = true) }
                },
                onFailure = { error ->
                    hapticFeedback.vibrateError()
                    _uiState.update { it.copy(isLoading = false, errorMessage = error.message ?: "Error al publicar") }
                }
            )
        }
    }
}