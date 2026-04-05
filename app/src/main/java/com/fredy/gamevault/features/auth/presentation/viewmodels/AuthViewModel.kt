package com.fredy.gamevault.features.auth.presentation.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.fredy.gamevault.core.hardware.HapticFeedback
import com.fredy.gamevault.core.session.SessionManager
import com.fredy.gamevault.features.auth.domain.usecases.LoginUseCase
import com.fredy.gamevault.features.auth.domain.usecases.RegisterUseCase
import com.fredy.gamevault.features.auth.presentation.screens.AuthUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AuthViewModel @Inject constructor(
    private val loginUseCase: LoginUseCase,
    private val registerUseCase: RegisterUseCase,
    private val sessionManager: SessionManager,
    private val hapticFeedback: HapticFeedback
) : ViewModel() {

    private val _uiState = MutableStateFlow(AuthUiState())
    val uiState = _uiState.asStateFlow()

    fun login(email: String, password: String, onSuccess: () -> Unit, useBiometric: Boolean = false) {
        _uiState.update { it.copy(isLoading = true, error = null, successMessage = null) }

        viewModelScope.launch {
            val result = loginUseCase(email, password)

            result.fold(
                onSuccess = { pair ->
                    hapticFeedback.vibrateSuccess()

                    if (useBiometric) {
                        sessionManager.setUseBiometric(true)
                    }

                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            successMessage = "Bienvenido ${pair.first.firstName}"
                        )
                    }
                    onSuccess()
                },
                onFailure = { error ->
                    hapticFeedback.vibrateError()
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            error = error.message ?: "Error al iniciar sesión"
                        )
                    }
                }
            )
        }
    }

    fun loginWithBiometric(onSuccess: () -> Unit, onError: (String) -> Unit) {
        viewModelScope.launch {
            val token = sessionManager.getToken()
            if (token != null) {
                hapticFeedback.vibrateSuccess()
                onSuccess()
            } else {
                hapticFeedback.vibrateError()
                onError("No hay sesión guardada")
            }
        }
    }

    fun loadBiometricState(onResult: (shouldAutoPrompt: Boolean, hasSavedSession: Boolean) -> Unit) {
        viewModelScope.launch {
            val shouldUseBiometric = sessionManager.shouldUseBiometric()
            val hasSavedSession = sessionManager.getToken() != null
            onResult(shouldUseBiometric && hasSavedSession, hasSavedSession)
        }
    }

    fun enableBiometricForCurrentSession(onSuccess: () -> Unit, onError: (String) -> Unit) {
        viewModelScope.launch {
            val hasSavedSession = sessionManager.getToken() != null
            if (!hasSavedSession) {
                onError("Debes iniciar sesión primero")
                return@launch
            }

            sessionManager.setUseBiometric(true)
            onSuccess()
        }
    }

    fun register(email: String, password: String, firstName: String, lastName: String, onSuccess: () -> Unit) {
        _uiState.update { it.copy(isLoading = true, error = null, successMessage = null) }

        viewModelScope.launch {
            val result = registerUseCase(email, password, firstName, lastName)

            result.fold(
                onSuccess = { user ->
                    hapticFeedback.vibrateSuccess()
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            successMessage = "Usuario ${user.firstName} registrado exitosamente"
                        )
                    }
                    onSuccess()
                },
                onFailure = { error ->
                    hapticFeedback.vibrateError()
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            error = error.message ?: "Error en el registro"
                        )
                    }
                }
            )
        }
    }

    fun clearError() {
        _uiState.update { it.copy(error = null) }
    }
}