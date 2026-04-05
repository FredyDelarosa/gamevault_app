package com.fredy.gamevault.features.games.data.datasources.remote.model

import com.google.gson.annotations.SerializedName

data class GameResponse(
    @SerializedName("id") val id: String,
    @SerializedName("user_id") val userId: String,
    @SerializedName("name") val name: String,
    @SerializedName("description") val description: String,
    @SerializedName("cover_image_url") val coverImageUrl: String,
    @SerializedName("status") val status: String,
    @SerializedName("completed") val completed: Boolean,
    @SerializedName("created_at") val createdAt: String,
    @SerializedName("updated_at") val updatedAt: String
)