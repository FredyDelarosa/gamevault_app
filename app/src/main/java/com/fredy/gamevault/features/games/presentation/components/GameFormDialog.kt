package com.fredy.gamevault.features.games.presentation.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.rememberAsyncImagePainter
import com.fredy.gamevault.core.hardware.CameraScreen
import com.fredy.gamevault.core.hardware.HapticFeedback
import com.fredy.gamevault.core.ui.components.GameVaultButton
import com.fredy.gamevault.core.ui.components.GameVaultTextField
import com.fredy.gamevault.features.games.domain.entities.GameStatus
import com.fredy.gamevault.features.games.presentation.GameFormEvent
import com.fredy.gamevault.features.games.presentation.viewmodels.GameFormViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GameFormDialog(
    gameId: String? = null,
    onDismiss: () -> Unit,
    onSuccess: () -> Unit,
    viewModel: GameFormViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val scrollState = rememberScrollState()
    var showCamera by remember { mutableStateOf(false) }
    var hasSubmitted by remember { mutableStateOf(false) }
    val context = LocalContext.current
    val hapticFeedback = remember(context) {
        HapticFeedback(context)
    }

    LaunchedEffect(gameId) {
        if (gameId != null && gameId != "new") {
            viewModel.handleEvent(GameFormEvent.LoadGame(gameId))
        }
    }

    LaunchedEffect(uiState.isLoading) {
        if (hasSubmitted && !uiState.isLoading && uiState.errorMessage == null && uiState.name.isNotBlank()) {
            onSuccess()
            onDismiss()
        }
    }

    if (showCamera) {
        CameraScreen(
            onPhotoCaptured = { uri ->
                viewModel.handleEvent(GameFormEvent.CoverImageUrlChanged(uri))
                showCamera = false
            },
            onDismiss = { showCamera = false }
        )
    } else {
        AlertDialog(
            onDismissRequest = onDismiss,
            title = {
                Text(
                    text = if (uiState.isEditMode) "Editar Juego" else "Agregar Juego",
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold
                )
            },
            text = {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .verticalScroll(scrollState)
                        .padding(vertical = 8.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        if (uiState.coverImageUrl.isNotBlank()) {
                            Image(
                                painter = rememberAsyncImagePainter(uiState.coverImageUrl),
                                contentDescription = "Portada",
                                modifier = Modifier
                                    .size(80.dp)
                                    .clickable { showCamera = true }
                            )
                        }

                        OutlinedButton(
                            onClick = {
                                hapticFeedback.vibrateTick()
                                showCamera = true
                            },
                            modifier = Modifier.weight(1f)
                        ) {
                            Icon(
                                imageVector = Icons.Default.CameraAlt,
                                contentDescription = "Tomar foto",
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Tomar portada")
                        }
                    }

                    GameVaultTextField(
                        value = uiState.name,
                        onValueChange = { viewModel.handleEvent(GameFormEvent.NameChanged(it)) },
                        label = "Nombre del juego *",
                        placeholder = "Ej: The Legend of Zelda",
                        isError = uiState.errorMessage != null && uiState.name.isBlank(),
                        errorMessage = if (uiState.errorMessage != null && uiState.name.isBlank())
                            "El nombre es requerido" else null
                    )

                    GameVaultTextField(
                        value = uiState.description,
                        onValueChange = { viewModel.handleEvent(GameFormEvent.DescriptionChanged(it)) },
                        label = "Descripción",
                        placeholder = "Describe brevemente el juego...",
                        singleLine = false,
                        maxLines = 4,
                        minHeight = 80
                    )

                    GameVaultTextField(
                        value = uiState.coverImageUrl,
                        onValueChange = { viewModel.handleEvent(GameFormEvent.CoverImageUrlChanged(it)) },
                        label = "URL de la portada (opcional)",
                        placeholder = "https://ejemplo.com/imagen.jpg",
                        keyboardType = androidx.compose.ui.text.input.KeyboardType.Uri
                    )

                    Text(
                        text = "Estado",
                        style = MaterialTheme.typography.labelLarge,
                        fontWeight = FontWeight.Medium
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        FilterChip(
                            selected = uiState.status == GameStatus.WISHLIST,
                            onClick = {
                                hapticFeedback.vibrateTick()
                                viewModel.handleEvent(GameFormEvent.StatusChanged(GameStatus.WISHLIST))
                            },
                            label = { Text("Wishlist") },
                            modifier = Modifier.weight(1f)
                        )

                        FilterChip(
                            selected = uiState.status == GameStatus.BACKLOG,
                            onClick = {
                                hapticFeedback.vibrateTick()
                                viewModel.handleEvent(GameFormEvent.StatusChanged(GameStatus.BACKLOG))
                            },
                            label = { Text("Backlog") },
                            modifier = Modifier.weight(1f)
                        )

                        FilterChip(
                            selected = uiState.status == GameStatus.NOW_PLAYING,
                            onClick = {
                                hapticFeedback.vibrateTick()
                                viewModel.handleEvent(GameFormEvent.StatusChanged(GameStatus.NOW_PLAYING))
                            },
                            label = { Text("Now Playing") },
                            modifier = Modifier.weight(1f)
                        )
                    }

                    if (uiState.errorMessage != null) {
                        Text(
                            text = uiState.errorMessage!!,
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.error
                        )
                    }
                }
            },
            confirmButton = {
                GameVaultButton(
                    text = if (uiState.isEditMode) "Actualizar" else "Guardar",
                    onClick = {
                        hasSubmitted = true
                        viewModel.handleEvent(GameFormEvent.Submit)
                    },
                    isLoading = uiState.isLoading,
                    modifier = Modifier.fillMaxWidth()
                )
            },
            dismissButton = {
                TextButton(
                    onClick = onDismiss,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Cancelar")
                }
            },
            shape = MaterialTheme.shapes.large
        )
    }
}