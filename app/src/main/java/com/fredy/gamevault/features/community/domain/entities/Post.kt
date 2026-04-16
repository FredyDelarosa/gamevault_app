package com.fredy.gamevault.features.community.domain.entities

enum class PostType {
    TIP, DISCUSSION, REVIEW, QUESTION, NEWS;

    fun displayName(): String = when (this) {
        TIP -> "Tip"
        DISCUSSION -> "Discusión"
        REVIEW -> "Reseña"
        QUESTION -> "Pregunta"
        NEWS -> "Noticia"
    }

    companion object {
        fun fromString(value: String): PostType = try {
            valueOf(value)
        } catch (e: Exception) {
            DISCUSSION
        }
    }
}

data class Post(
    val id: String,
    val userId: String,
    val authorName: String,
    val gameName: String,
    val title: String,
    val content: String,
    val postType: PostType,
    val reactionsCount: Int,
    val hasReacted: Boolean,
    val createdAt: String,
    val updatedAt: String
)