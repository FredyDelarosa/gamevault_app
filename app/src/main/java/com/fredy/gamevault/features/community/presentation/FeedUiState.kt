package com.fredy.gamevault.features.community.presentation

import com.fredy.gamevault.features.community.domain.entities.Post

data class FeedUiState(
    val posts: List<Post> = emptyList(),
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val showMyGamesOnly: Boolean = false
)

sealed class FeedEvent {
    object LoadPosts : FeedEvent()
    object ToggleFilter : FeedEvent()
    data class ReactToPost(val postId: String) : FeedEvent()
    object ClearError : FeedEvent()
}