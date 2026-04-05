package com.fredy.gamevault.features.games.data.datasources.remote.model

import com.google.gson.annotations.SerializedName

data class GameDto(
    @SerializedName("name") val name: String,
    @SerializedName("description") val description: String,
    @SerializedName("cover_image_url") val coverImageUrl: String,
    @SerializedName("status") val status: String,
    @SerializedName("completed") val completed: Boolean = false
)