package com.fredy.gamevault.features.community.presentation.screen

import androidx.compose.foundation.layout.*
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

    LaunchedEffect(uiState.success) {
        if (uiState.success) {
            onSuccess()
        }
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
                .padding(24.dp)
                .verticalScroll(scroll)
        ) {
            // Tipo de publicación
            Text(
                text = "Tipo de publicación",
                style = MaterialTheme.typography.labelLarge,
                fontWeight = FontWeight.SemiBold,
                modifier = Modifier.padding(bottom = 8.dp)
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                PostType.values().forEach { type ->
                    FilterChip(
                        selected = uiState.postType == type,
                        onClick = { viewModel.handleEvent(CreatePostEvent.TypeChanged(type)) },
                        label = {
                            Text(
                                type.displayName(),
                                style = MaterialTheme.typography.labelSmall
                            )
                        },
                        modifier = Modifier.weight(1f)
                    )
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            GameVaultTextField(
                value = uiState.gameName,
                onValueChange = { viewModel.handleEvent(CreatePostEvent.GameNameChanged(it)) },
                label = "Juego *",
                placeholder = "Ej: ARK, Minecraft, Zelda..."
            )

            Spacer(modifier = Modifier.height(16.dp))

            GameVaultTextField(
                value = uiState.title,
                onValueChange = { viewModel.handleEvent(CreatePostEvent.TitleChanged(it)) },
                label = "Título *",
                placeholder = "Ej: Cómo domesticar un T-Rex fácil"
            )

            Spacer(modifier = Modifier.height(16.dp))

            OutlinedTextField(
                value = uiState.content,
                onValueChange = { viewModel.handleEvent(CreatePostEvent.ContentChanged(it)) },
                label = { Text("Contenido") },
                placeholder = { Text("Comparte los detalles...") },
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(min = 120.dp),
                shape = RoundedCornerShape(14.dp),
                maxLines = 10
            )

            uiState.errorMessage?.let { error ->
                Spacer(modifier = Modifier.height(12.dp))
                Card(
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.errorContainer
                    )
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
                onClick = { viewModel.handleEvent(CreatePostEvent.Submit) }
            )
        }
    }
}