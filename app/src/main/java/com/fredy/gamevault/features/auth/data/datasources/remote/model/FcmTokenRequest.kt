package com.fredy.gamevault.features.auth.data.datasources.remote.model

import com.google.gson.annotations.SerializedName

data class FcmTokenRequest(
    @SerializedName("fcm_token") val fcmToken: String
)