package com.fredy.gamevault.features.auth.domain.usecases

import com.fredy.gamevault.features.auth.domain.entities.User
import com.fredy.gamevault.features.auth.domain.repositories.AuthRepository
import javax.inject.Inject

class LoginUseCase @Inject constructor(
    private val repository: AuthRepository
) {
    suspend operator fun invoke(email: String, password: String): Result<Pair<User, String>> {
        return repository.login(email, password)
    }
}