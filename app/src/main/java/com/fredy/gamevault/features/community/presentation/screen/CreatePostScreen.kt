package com.fredy.gamevault.features.community.presentation.screen

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.fredy.gamevault.core.ui.components.GameVaultButton
import com.fredy.gamevault.core.ui.components.GameVaultTextField
import com.fredy.gamevault.features.community.domain.entities.PostType
import com.fredy.gamevault.features.community.presentation.CreatePostEvent
import com.fredy.gamevault.features.community.presentation.viewmodels.CreatePostViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreatePostScreen(
    onBack: () -> Unit,
    onSuccess: () -> Unit,
    viewModel: CreatePostViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val scroll = rememberScrollState()

    var gameNameError by remember { mutableStateOf<String?>(null) }
    var titleError by remember { mutableStateOf<String?>(null) }

    LaunchedEffect(uiState.success) {
        if (uiState.success) {
            onSuccess()
        }
    }

    fun validate(): Boolean {
        var valid = true
        gameNameError = when {
            uiState.gameName.isBlank() -> { valid = false; "El nombre del juego es requerido" }
            uiState.gameName.length < 2 -> { valid = false; "Mínimo 2 caracteres" }
            else -> null
        }
        titleError = when {
            uiState.title.isBlank() -> { valid = false; "El título es requerido" }
            uiState.title.length < 3 -> { valid = false; "Mínimo 3 caracteres" }
            else -> null
        }
        return valid
    }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Nueva publicación", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Volver")
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 24.dp)
                .verticalScroll(scroll)
        ) {
            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "Tipo de publicación",
                style = MaterialTheme.typography.labelLarge,
                fontWeight = FontWeight.SemiBold,
                modifier = Modifier.padding(bottom = 8.dp)
            )

            // LazyRow scrollable horizontal: cada chip muestra su texto completo
            LazyRow(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                contentPadding = PaddingValues(vertical = 4.dp)
            ) {
                items(PostType.values()) { type ->
                    FilterChip(
                        selected = uiState.postType == type,
                        onClick = { viewModel.handleEvent(CreatePostEvent.TypeChanged(type)) },
                        label = {
                            Text(
                                text = type.displayName(),
                                style = MaterialTheme.typography.labelMedium,
                                maxLines = 1
                            )
                        }
                    )
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            GameVaultTextField(
                value = uiState.gameName,
                onValueChange = {
                    viewModel.handleEvent(CreatePostEvent.GameNameChanged(it))
                    if (gameNameError != null) gameNameError = null
                },
                label = "Juego",
                placeholder = "Ej: ARK, Minecraft, Zelda...",
                isError = gameNameError != null,
                errorMessage = gameNameError
            )

            Spacer(modifier = Modifier.height(16.dp))

            GameVaultTextField(
                value = uiState.title,
                onValueChange = {
                    viewModel.handleEvent(CreatePostEvent.TitleChanged(it))
                    if (titleError != null) titleError = null
                },
                label = "Título",
                placeholder = "Ej: Cómo domesticar un T-Rex fácil",
                isError = titleError != null,
                errorMessage = titleError
            )

            Spacer(modifier = Modifier.height(16.dp))

            GameVaultTextField(
                value = uiState.content,
                onValueChange = { viewModel.handleEvent(CreatePostEvent.ContentChanged(it)) },
                label = "Contenido",
                placeholder = "Comparte los detalles...",
                singleLine = false,
                maxLines = 8,
                minHeight = 140
            )

            uiState.errorMessage?.let { error ->
                Spacer(modifier = Modifier.height(12.dp))
                Card(
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.errorContainer
                    ),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text(
                        text = error,
                        color = MaterialTheme.colorScheme.error,
                        modifier = Modifier.padding(12.dp),
                        style = MaterialTheme.typography.bodySmall
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            GameVaultButton(
                text = "Publicar",
                isLoading = uiState.isLoading,
                onClick = {
                    if (validate()) {
                        viewModel.handleEvent(CreatePostEvent.Submit)
                    }
                }
            )

            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}