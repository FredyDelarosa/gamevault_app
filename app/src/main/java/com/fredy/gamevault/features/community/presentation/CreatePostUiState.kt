package com.fredy.gamevault.features.community.presentation

import com.fredy.gamevault.features.community.domain.entities.PostType

data class CreatePostUiState(
    val gameName: String = "",
    val title: String = "",
    val content: String = "",
    val postType: PostType = PostType.DISCUSSION,
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val success: Boolean = false
)

sealed class CreatePostEvent {
    data class GameNameChanged(val name: String) : CreatePostEvent()
    data class TitleChanged(val title: String) : CreatePostEvent()
    data class ContentChanged(val content: String) : CreatePostEvent()
    data class TypeChanged(val type: PostType) : CreatePostEvent()
    object Submit : CreatePostEvent()
    object Reset : CreatePostEvent()
}