package com.fredy.gamevault.features.auth.domain.entities

data class User(
    val id: String,
    val email: String,
    val firstName: String,
    val lastName: String
)