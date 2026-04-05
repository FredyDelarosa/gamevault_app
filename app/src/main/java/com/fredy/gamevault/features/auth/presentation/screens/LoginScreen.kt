package com.fredy.gamevault.features.auth.presentation.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.fragment.app.FragmentActivity
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.fredy.gamevault.core.hardware.BiometricManager
import com.fredy.gamevault.core.ui.components.GameVaultButton
import com.fredy.gamevault.core.ui.components.GameVaultTextField
import com.fredy.gamevault.features.auth.presentation.viewmodels.AuthViewModel

@Composable
fun LoginScreen(
    onNavigateToRegister: () -> Unit,
    onLoginSuccess: () -> Unit,
    viewModel: AuthViewModel = hiltViewModel()
) {
    val context = LocalContext.current
    val activity = context as? FragmentActivity
    val biometricManager = remember { BiometricManager() }

    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var useBiometric by remember { mutableStateOf(false) }
    var showBiometricDialog by remember { mutableStateOf(false) }
    var hasSavedSession by remember { mutableStateOf(false) }
    var biometricMessage by remember { mutableStateOf<String?>(null) }
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    LaunchedEffect(Unit) {
        viewModel.loadBiometricState { shouldAutoPrompt, hasSession ->
            showBiometricDialog = shouldAutoPrompt
            hasSavedSession = hasSession
        }
    }

    if (showBiometricDialog) {
        BiometricLoginScreen(
            onBiometricSuccess = {
                viewModel.loginWithBiometric(
                    onSuccess = {
                        onLoginSuccess()
                    },
                    onError = { error ->
                        biometricMessage = error
                    }
                )
            },
            onUsePassword = {
                showBiometricDialog = false
            },
            onDismiss = {
                showBiometricDialog = false
            }
        )
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 32.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "GameVault",
            style = MaterialTheme.typography.displaySmall,
            fontWeight = FontWeight.ExtraBold,
            color = MaterialTheme.colorScheme.primary
        )

        Text(
            text = "Organiza tu biblioteca de juegos",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.padding(top = 8.dp)
        )

        Spacer(modifier = Modifier.height(48.dp))

        GameVaultTextField(
            value = email,
            onValueChange = { email = it },
            label = "Correo electrónico",
            placeholder = "ejemplo@correo.com"
        )

        Spacer(modifier = Modifier.height(16.dp))

        GameVaultTextField(
            value = password,
            onValueChange = { password = it },
            label = "Contraseña",
            placeholder = "••••••••"
        )

        Spacer(modifier = Modifier.height(12.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Start
        ) {
            Checkbox(
                checked = useBiometric,
                onCheckedChange = { useBiometric = it }
            )
            Text(
                text = "Recordar con huella digital",
                modifier = Modifier.align(Alignment.CenterVertically)
            )
        }

        Spacer(modifier = Modifier.height(24.dp))

        val authError = uiState.error
        if (authError != null) {
            Text(
                text = authError,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.error,
                modifier = Modifier.padding(bottom = 12.dp)
            )
        }

        biometricMessage?.let { message ->
            Text(
                text = message,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.error,
                modifier = Modifier.padding(bottom = 12.dp)
            )
        }

        GameVaultButton(
            text = "Iniciar Sesión",
            isLoading = uiState.isLoading,
            onClick = {
                viewModel.login(
                    email = email,
                    password = password,
                    onSuccess = {
                        onLoginSuccess()
                    },
                    useBiometric = useBiometric
                )
            }
        )

        Spacer(modifier = Modifier.height(12.dp))

        if (hasSavedSession) {
            OutlinedButton(
                onClick = {
                    if (activity == null) {
                        biometricMessage = "No se pudo abrir la autenticación biométrica"
                        return@OutlinedButton
                    }

                    biometricManager.showBiometricPrompt(
                        activity = activity,
                        title = "Configurar autenticación biométrica",
                        description = "Usa tu huella para acceder rápidamente",
                        negativeButtonText = "Cancelar",
                        onSuccess = {
                            viewModel.enableBiometricForCurrentSession(
                                onSuccess = {
                                    biometricMessage = "Acceso con huella configurado"
                                },
                                onError = { error ->
                                    biometricMessage = error
                                }
                            )
                        },
                        onError = { error ->
                            biometricMessage = error
                        }
                    )
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Configurar acceso con huella")
            }

            Spacer(modifier = Modifier.height(16.dp))
        } else {
            Spacer(modifier = Modifier.height(16.dp))
        }

        TextButton(
            onClick = onNavigateToRegister,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(
                text = "¿No tienes cuenta? Regístrate aquí",
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.SemiBold
            )
        }
    }
}