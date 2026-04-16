package com.fredy.gamevault.features.community.presentation.screen

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.fredy.gamevault.features.community.domain.entities.Post
import com.fredy.gamevault.features.community.domain.entities.PostType
import com.fredy.gamevault.features.community.presentation.FeedEvent
import com.fredy.gamevault.features.community.presentation.viewmodels.FeedViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FeedScreen(
    onCreatePost: () -> Unit,
    viewModel: FeedViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    LaunchedEffect(Unit) {
        viewModel.handleEvent(FeedEvent.LoadPosts)
    }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        text = if (uiState.showMyGamesOnly) "Mis Juegos" else "Comunidad",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold
                    )
                },
                actions = {
                    IconButton(onClick = { viewModel.handleEvent(FeedEvent.ToggleFilter) }) {
                        Icon(
                            imageVector = Icons.Default.FilterList,
                            contentDescription = "Filtrar",
                            tint = if (uiState.showMyGamesOnly) MaterialTheme.colorScheme.primary
                            else MaterialTheme.colorScheme.onSurface
                        )
                    }
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = onCreatePost,
                containerColor = MaterialTheme.colorScheme.primary
            ) {
                Icon(Icons.Default.Add, contentDescription = "Crear publicación")
            }
        }
    ) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            when {
                uiState.isLoading -> {
                    CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
                }
                uiState.posts.isEmpty() -> {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(32.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Text(
                            text = if (uiState.showMyGamesOnly)
                                "No hay publicaciones sobre tus juegos"
                            else "No hay publicaciones aún",
                            style = MaterialTheme.typography.titleMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Button(onClick = onCreatePost) {
                            Text("Crear la primera publicación")
                        }
                    }
                }
                else -> {
                    LazyColumn(
                        contentPadding = PaddingValues(vertical = 8.dp)
                    ) {
                        items(uiState.posts) { post ->
                            PostCard(
                                post = post,
                                onReact = { viewModel.handleEvent(FeedEvent.ReactToPost(post.id)) }
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun PostCard(
    post: Post,
    onReact: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 6.dp),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        )
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            // Header: tipo + juego
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                PostTypeBadge(post.postType)
                Text(
                    text = post.gameName,
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.primary,
                    fontWeight = FontWeight.Bold
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Autor
            Text(
                text = "por ${post.authorName}",
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Título
            Text(
                text = post.title,
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )

            // Contenido
            if (post.content.isNotBlank()) {
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = post.content,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 4
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Reacciones
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = onReact) {
                    Icon(
                        imageVector = if (post.hasReacted) Icons.Default.Favorite
                        else Icons.Default.FavoriteBorder,
                        contentDescription = "Reaccionar",
                        tint = if (post.hasReacted) MaterialTheme.colorScheme.tertiary
                        else MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                Text(
                    text = "${post.reactionsCount} recomendaciones",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

@Composable
fun PostTypeBadge(type: PostType) {
    val (bg, fg) = when (type) {
        PostType.TIP -> MaterialTheme.colorScheme.primary to MaterialTheme.colorScheme.onPrimary
        PostType.DISCUSSION -> MaterialTheme.colorScheme.secondary to MaterialTheme.colorScheme.onSecondary
        PostType.REVIEW -> MaterialTheme.colorScheme.tertiary to MaterialTheme.colorScheme.onSecondary
        PostType.QUESTION -> MaterialTheme.colorScheme.error to MaterialTheme.colorScheme.onError
        PostType.NEWS -> MaterialTheme.colorScheme.primaryContainer to MaterialTheme.colorScheme.onPrimaryContainer
    }

    Surface(
        shape = RoundedCornerShape(8.dp),
        color = bg
    ) {
        Text(
            text = type.displayName(),
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
            style = MaterialTheme.typography.labelSmall,
            color = fg,
            fontWeight = FontWeight.Bold
        )
    }
}