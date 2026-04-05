package com.fredy.gamevault.features.auth.data.datasources.remote.mapper

import com.fredy.gamevault.features.auth.data.datasources.remote.model.UserDto
import com.fredy.gamevault.features.auth.domain.entities.User

fun UserDto.toDomain(): User {
    return User(
        id = id,
        email = email,
        firstName = firstName,
        lastName = lastName
    )
}