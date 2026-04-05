package com.fredy.gamevault.features.auth.domain.repositories

import com.fredy.gamevault.features.auth.domain.entities.User

interface AuthRepository {
    suspend fun login(email: String, password: String): Result<Pair<User, String>>
    suspend fun register(email: String, password: String, firstName: String, lastName: String): Result<User>
    suspend fun logout()
}