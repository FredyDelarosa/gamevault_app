package com.fredy.gamevault.features.auth.domain.usecases

import com.fredy.gamevault.features.auth.domain.entities.User
import com.fredy.gamevault.features.auth.domain.repositories.AuthRepository
import javax.inject.Inject

class RegisterUseCase @Inject constructor(
    private val repository: AuthRepository
) {
    suspend operator fun invoke(
        email: String,
        password: String,
        firstName: String,
        lastName: String
    ): Result<User> {
        return repository.register(email, password, firstName, lastName)
    }
}