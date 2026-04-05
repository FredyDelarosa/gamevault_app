package com.fredy.gamevault.features.auth.presentation.screens

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Fingerprint
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.fragment.app.FragmentActivity
import com.fredy.gamevault.core.hardware.BiometricAvailability
import com.fredy.gamevault.core.hardware.BiometricManager
import com.fredy.gamevault.core.ui.components.GameVaultButton

@Composable
fun BiometricLoginScreen(
    onBiometricSuccess: () -> Unit,
    onUsePassword: () -> Unit,
    onDismiss: () -> Unit
) {
    val context = LocalContext.current
    val activity = context as? FragmentActivity
    val biometricManager = remember { BiometricManager() }
    val biometricAvailability = remember(activity) {
        activity?.let { biometricManager.isBiometricAvailable(it) } ?: BiometricAvailability.UNAVAILABLE
    }

    var showError by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf("") }

    if (activity == null) {
        LaunchedEffect(Unit) {
            errorMessage = "Autenticación biométrica no disponible en esta pantalla"
            showError = true
        }
    } else {
        when (biometricAvailability) {
            BiometricAvailability.AVAILABLE -> {
                // Mostrar diálogo de autenticación automáticamente
                LaunchedEffect(activity) {
                    biometricManager.showBiometricPrompt(
                        activity = activity,
                        title = "Autenticación Biométrica",
                        description = "Usa tu huella digital para acceder a GameVault",
                        negativeButtonText = "Usar contraseña",
                        onSuccess = {
                            onBiometricSuccess()
                        },
                        onError = { error ->
                            errorMessage = error
                            showError = true
                        },
                        onFailed = {
                            errorMessage = "Autenticación fallida. Intenta nuevamente."
                            showError = true
                        }
                    )
                }
            }
            else -> {
                // No disponible, mostrar mensaje
                LaunchedEffect(Unit) {
                    errorMessage = when (biometricAvailability) {
                        BiometricAvailability.NO_HARDWARE -> "Este dispositivo no tiene sensor de huellas"
                        BiometricAvailability.HW_UNAVAILABLE -> "El sensor de huellas no está disponible"
                        BiometricAvailability.NONE_ENROLLED -> "No hay huellas registradas en el dispositivo"
                        else -> "Autenticación biométrica no disponible"
                    }
                    showError = true
                }
            }
        }
    }

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(
            dismissOnBackPress = true,
            dismissOnClickOutside = false
        )
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            shape = MaterialTheme.shapes.large
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Fingerprint,
                    contentDescription = "Huella digital",
                    modifier = Modifier.size(64.dp),
                    tint = MaterialTheme.colorScheme.primary
                )

                Text(
                    text = "Autenticación Biométrica",
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold
                )

                Text(
                    text = "Usa tu huella digital para acceder rápidamente",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                if (showError) {
                    Card(
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.errorContainer
                        )
                    ) {
                        Text(
                            text = errorMessage,
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onErrorContainer,
                            modifier = Modifier.padding(12.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                GameVaultButton(
                    text = "Usar contraseña",
                    onClick = onUsePassword,
                    modifier = Modifier.fillMaxWidth()
                )

                TextButton(
                    onClick = onDismiss,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Cancelar")
                }
            }
        }
    }

    // Prevenir que el botón de retroceso cierre la app sin manejar
    BackHandler(enabled = true) {
        onDismiss()
    }
}