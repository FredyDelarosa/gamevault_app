package com.fredy.gamevault.features.community.presentation.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.fredy.gamevault.core.hardware.HapticFeedback
import com.fredy.gamevault.features.community.domain.repositories.PostRepository
import com.fredy.gamevault.features.community.presentation.FeedEvent
import com.fredy.gamevault.features.community.presentation.FeedUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class FeedViewModel @Inject constructor(
    private val postRepository: PostRepository,
    private val hapticFeedback: HapticFeedback
) : ViewModel() {

    private val _uiState = MutableStateFlow(FeedUiState())
    val uiState = _uiState.asStateFlow()

    fun handleEvent(event: FeedEvent) {
        when (event) {
            is FeedEvent.LoadPosts -> loadPosts()
            is FeedEvent.ToggleFilter -> toggleFilter()
            is FeedEvent.ReactToPost -> reactToPost(event.postId)
            is FeedEvent.ClearError -> clearError()
        }
    }

    private fun loadPosts() {
        _uiState.update { it.copy(isLoading = true, errorMessage = null) }

        viewModelScope.launch {
            val result = if (_uiState.value.showMyGamesOnly) {
                postRepository.getPostsForMyGames()
            } else {
                postRepository.getAllPosts()
            }

            result.fold(
                onSuccess = { posts ->
                    _uiState.update { it.copy(posts = posts, isLoading = false) }
                },
                onFailure = { error ->
                    _uiState.update {
                        it.copy(isLoading = false, errorMessage = error.message ?: "Error al cargar posts")
                    }
                }
            )
        }
    }

    private fun toggleFilter() {
        _uiState.update { it.copy(showMyGamesOnly = !it.showMyGamesOnly) }
        loadPosts()
    }

    private fun reactToPost(postId: String) {
        viewModelScope.launch {
            val result = postRepository.toggleReaction(postId)
            result.fold(
                onSuccess = { reacted ->
                    hapticFeedback.vibrateTick()
                    // Actualizar el post localmente
                    _uiState.update { state ->
                        state.copy(
                            posts = state.posts.map { post ->
                                if (post.id == postId) {
                                    post.copy(
                                        hasReacted = reacted,
                                        reactionsCount = if (reacted) post.reactionsCount + 1
                                        else (post.reactionsCount - 1).coerceAtLeast(0)
                                    )
                                } else post
                            }
                        )
                    }
                },
                onFailure = { error ->
                    hapticFeedback.vibrateError()
                    _uiState.update { it.copy(errorMessage = error.message) }
                }
            )
        }
    }

    private fun clearError() {
        _uiState.update { it.copy(errorMessage = null) }
    }
}