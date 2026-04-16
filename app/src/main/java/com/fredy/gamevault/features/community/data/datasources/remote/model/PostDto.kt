package com.fredy.gamevault.features.community.data.datasources.remote.model

import com.google.gson.annotations.SerializedName

data class CreatePostRequest(
    @SerializedName("game_name") val gameName: String,
    @SerializedName("title") val title: String,
    @SerializedName("content") val content: String,
    @SerializedName("post_type") val postType: String
)

data class PostResponse(
    @SerializedName("id") val id: String,
    @SerializedName("user_id") val userId: String,
    @SerializedName("author_name") val authorName: String,
    @SerializedName("game_name") val gameName: String,
    @SerializedName("title") val title: String,
    @SerializedName("content") val content: String,
    @SerializedName("post_type") val postType: String,
    @SerializedName("reactions_count") val reactionsCount: Int,
    @SerializedName("has_reacted") val hasReacted: Boolean,
    @SerializedName("created_at") val createdAt: String,
    @SerializedName("updated_at") val updatedAt: String
)