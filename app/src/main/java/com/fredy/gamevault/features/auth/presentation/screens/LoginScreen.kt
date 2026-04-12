package com.fredy.gamevault.features.auth.presentation.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.SportsEsports
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.fragment.app.FragmentActivity
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.fredy.gamevault.core.hardware.BiometricManager
import com.fredy.gamevault.core.ui.components.GameVaultButton
import com.fredy.gamevault.core.ui.components.GameVaultTextField
import com.fredy.gamevault.core.ui.theme.GradientCyan
import com.fredy.gamevault.core.ui.theme.GradientPurple
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
    val scrollState = rememberScrollState()

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
                    onSuccess = { onLoginSuccess() },
                    onError = { error -> biometricMessage = error }
                )
            },
            onUsePassword = { showBiometricDialog = false },
            onDismiss = { showBiometricDialog = false }
        )
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(scrollState)
                .padding(horizontal = 28.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(80.dp))

            // Logo / Icono
            Box(
                modifier = Modifier
                    .size(80.dp)
                    .clip(RoundedCornerShape(20.dp))
                    .background(
                        Brush.linearGradient(listOf(GradientPurple, GradientCyan))
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.SportsEsports,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onPrimary,
                    modifier = Modifier.size(44.dp)
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            Text(
                text = "GameVault",
                style = MaterialTheme.typography.displaySmall,
                fontWeight = FontWeight.Black,
                color = MaterialTheme.colorScheme.primary
            )

            Text(
                text = "Tu biblioteca de juegos, organizada",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(top = 4.dp)
            )

            Spacer(modifier = Modifier.height(48.dp))

            // Formulario
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
                horizontalArrangement = Arrangement.Start,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Checkbox(
                    checked = useBiometric,
                    onCheckedChange = { useBiometric = it },
                    colors = CheckboxDefaults.colors(
                        checkedColor = MaterialTheme.colorScheme.primary,
                        uncheckedColor = MaterialTheme.colorScheme.outline
                    )
                )
                Text(
                    text = "Recordar con huella digital",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurface
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Errores
            val authError = uiState.error
            if (authError != null) {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.errorContainer
                    ),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text(
                        text = authError,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.error,
                        modifier = Modifier.padding(12.dp)
                    )
                }
                Spacer(modifier = Modifier.height(12.dp))
            }

            biometricMessage?.let { message ->
                Text(
                    text = message,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.secondary,
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
                        onSuccess = { onLoginSuccess() },
                        useBiometric = useBiometric
                    )
                }
            )

            Spacer(modifier = Modifier.height(16.dp))

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
                                    onSuccess = { biometricMessage = "Acceso con huella configurado" },
                                    onError = { error -> biometricMessage = error }
                                )
                            },
                            onError = { error -> biometricMessage = error }
                        )
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(52.dp),
                    shape = RoundedCornerShape(16.dp),
                    border = ButtonDefaults.outlinedButtonBorder(enabled = true)
                ) {
                    Text(
                        "Configurar acceso con huella",
                        fontWeight = FontWeight.SemiBold
                    )
                }
                Spacer(modifier = Modifier.height(16.dp))
            }

            TextButton(
                onClick = onNavigateToRegister,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = "¿No tienes cuenta? Regístrate aquí",
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.primary
                )
            }

            Spacer(modifier = Modifier.height(32.dp))
        }
    }
}