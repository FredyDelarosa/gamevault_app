package com.fredy.gamevault.features.auth.presentation.screens

data class AuthUiState(
    val isLoading: Boolean = false,
    val error: String? = null,
    val successMessage: String? = null
)