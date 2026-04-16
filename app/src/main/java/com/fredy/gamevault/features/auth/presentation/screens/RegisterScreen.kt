package com.fredy.gamevault.features.auth.presentation.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PersonAdd
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.fredy.gamevault.core.ui.components.GameVaultButton
import com.fredy.gamevault.core.ui.components.GameVaultTextField
import com.fredy.gamevault.core.ui.theme.GradientCyan
import com.fredy.gamevault.core.ui.theme.GradientPink
import com.fredy.gamevault.features.auth.presentation.viewmodels.AuthViewModel

@Composable
fun RegisterScreen(
    onNavigateToLogin: () -> Unit,
    onRegisterSuccess: () -> Unit,
    viewModel: AuthViewModel = hiltViewModel()
) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var firstName by remember { mutableStateOf("") }
    var lastName by remember { mutableStateOf("") }

    var emailError by remember { mutableStateOf<String?>(null) }
    var passwordError by remember { mutableStateOf<String?>(null) }
    var firstNameError by remember { mutableStateOf<String?>(null) }
    var lastNameError by remember { mutableStateOf<String?>(null) }

    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val scrollState = rememberScrollState()

    LaunchedEffect(uiState.successMessage) {
        if (uiState.successMessage != null) {
            onRegisterSuccess()
        }
    }

    fun validate(): Boolean {
        var valid = true
        firstNameError = when {
            firstName.isBlank() -> { valid = false; "El nombre es requerido" }
            firstName.length < 2 -> { valid = false; "Mínimo 2 caracteres" }
            else -> null
        }
        lastNameError = when {
            lastName.isBlank() -> { valid = false; "El apellido es requerido" }
            lastName.length < 2 -> { valid = false; "Mínimo 2 caracteres" }
            else -> null
        }
        emailError = when {
            email.isBlank() -> { valid = false; "El correo es requerido" }
            !android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches() -> {
                valid = false; "Ingresa un correo válido"
            }
            else -> null
        }
        passwordError = when {
            password.isBlank() -> { valid = false; "La contraseña es requerida" }
            password.length < 6 -> { valid = false; "Mínimo 6 caracteres" }
            else -> null
        }
        return valid
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
            Spacer(modifier = Modifier.height(60.dp))

            Box(
                modifier = Modifier
                    .size(72.dp)
                    .clip(RoundedCornerShape(18.dp))
                    .background(Brush.linearGradient(listOf(GradientPink, GradientCyan))),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.PersonAdd,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onPrimary,
                    modifier = Modifier.size(38.dp)
                )
            }

            Spacer(modifier = Modifier.height(20.dp))

            Text(
                text = "Crear Cuenta",
                style = MaterialTheme.typography.headlineLarge,
                fontWeight = FontWeight.Black,
                color = MaterialTheme.colorScheme.primary
            )
            Text(
                text = "Comienza a organizar tu colección",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(top = 4.dp)
            )

            Spacer(modifier = Modifier.height(36.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                GameVaultTextField(
                    value = firstName,
                    onValueChange = {
                        firstName = it
                        if (firstNameError != null) firstNameError = null
                    },
                    label = "Nombre",
                    placeholder = "Tu nombre",
                    isError = firstNameError != null,
                    errorMessage = firstNameError,
                    modifier = Modifier.weight(1f)
                )
                GameVaultTextField(
                    value = lastName,
                    onValueChange = {
                        lastName = it
                        if (lastNameError != null) lastNameError = null
                    },
                    label = "Apellido",
                    placeholder = "Tu apellido",
                    isError = lastNameError != null,
                    errorMessage = lastNameError,
                    modifier = Modifier.weight(1f)
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            GameVaultTextField(
                value = email,
                onValueChange = {
                    email = it
                    if (emailError != null) emailError = null
                },
                label = "Correo electrónico",
                placeholder = "ejemplo@correo.com",
                keyboardType = KeyboardType.Email,
                isError = emailError != null,
                errorMessage = emailError
            )

            Spacer(modifier = Modifier.height(16.dp))

            GameVaultTextField(
                value = password,
                onValueChange = {
                    password = it
                    if (passwordError != null) passwordError = null
                },
                label = "Contraseña",
                placeholder = "Mínimo 6 caracteres",
                isPassword = true,
                isError = passwordError != null,
                errorMessage = passwordError
            )

            Spacer(modifier = Modifier.height(28.dp))

            uiState.error?.let { error ->
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.errorContainer),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text(
                        text = error,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.error,
                        modifier = Modifier.padding(12.dp)
                    )
                }
                Spacer(modifier = Modifier.height(12.dp))
            }

            GameVaultButton(
                text = "Registrarme",
                isLoading = uiState.isLoading,
                onClick = {
                    if (validate()) {
                        viewModel.register(
                            email = email.trim(),
                            password = password,
                            firstName = firstName.trim(),
                            lastName = lastName.trim(),
                            onSuccess = onRegisterSuccess
                        )
                    }
                }
            )

            Spacer(modifier = Modifier.height(16.dp))

            TextButton(
                onClick = onNavigateToLogin,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = "¿Ya tienes cuenta? Inicia sesión",
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.primary
                )
            }

            Spacer(modifier = Modifier.height(32.dp))
        }
    }
}