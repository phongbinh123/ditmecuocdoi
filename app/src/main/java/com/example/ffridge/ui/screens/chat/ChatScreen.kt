package com.example.ffridge.ui.screens.chat

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.ffridge.data.model.ChatMessage
import com.example.ffridge.data.model.MessageRole
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChatScreen(
    modifier: Modifier = Modifier,
    viewModel: ChatViewModel = viewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val listState = rememberLazyListState()
    val coroutineScope = rememberCoroutineScope()
    var showMenu by remember { mutableStateOf(false) }
    var showClearDialog by remember { mutableStateOf(false) }
    var showQuickActions by remember { mutableStateOf(true) }

    // Auto-scroll to bottom when new message arrives
    LaunchedEffect(uiState.messages.size) {
        if (uiState.messages.isNotEmpty()) {
            coroutineScope.launch {
                listState.animateScrollToItem(uiState.messages.size - 1)
            }
        }
    }

    Scaffold(
        topBar = {
            ChatTopBar(
                onMenuClick = { showMenu = true },
                onClearClick = { showClearDialog = true }
            )
        },
        bottomBar = {
            Column {
                // Quick actions
                AnimatedVisibility(
                    visible = showQuickActions && uiState.messages.size <= 1,
                    enter = slideInVertically() + fadeIn(),
                    exit = slideOutVertically() + fadeOut()
                ) {
                    QuickActionButtons(
                        onActionClick = { action ->
                            viewModel.updateInputText(action)
                            showQuickActions = false
                        }
                    )
                }

                // Input bar
                ChatInputBar(
                    inputText = uiState.inputText,
                    onInputChange = { viewModel.updateInputText(it) },
                    onSendClick = {
                        viewModel.sendMessage()
                        showQuickActions = false
                    },
                    isSending = uiState.isSending,
                    isEnabled = !uiState.isSending
                )
            }
        },
        modifier = modifier
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            if (uiState.messages.isEmpty() && !uiState.isLoading) {
                EmptyChatState()
            } else {
                LazyColumn(
                    state = listState,
                    contentPadding = PaddingValues(vertical = 16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                    modifier = Modifier.fillMaxSize()
                ) {
                    items(
                        items = uiState.messages,
                        key = { it.id }
                    ) { message ->
                        AnimatedVisibility(
                            visible = true,
                            enter = fadeIn() + slideInVertically { it / 2 },
                            exit = fadeOut() + slideOutVertically()
                        ) {
                            EnhancedChatBubble(
                                message = message,
                                onCopyClick = { /* TODO: Copy to clipboard */ }
                            )
                        }
                    }

                    // Typing indicator
                    if (uiState.isSending) {
                        item {
                            TypingIndicator()
                        }
                    }
                }
            }

            // Error snackbar
            AnimatedVisibility(
                visible = uiState.error != null,
                enter = slideInVertically { it } + fadeIn(),
                exit = slideOutVertically { it } + fadeOut(),
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(bottom = 80.dp)
            ) {
                Snackbar(
                    modifier = Modifier.padding(16.dp),
                    containerColor = MaterialTheme.colorScheme.errorContainer,
                    contentColor = MaterialTheme.colorScheme.onErrorContainer
                ) {
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.Error,
                            contentDescription = null
                        )
                        Text(uiState.error ?: "")
                    }
                }
            }
        }
    }

    // Clear chat dialog
    if (showClearDialog) {
        AlertDialog(
            onDismissRequest = { showClearDialog = false },
            icon = {
                Icon(
                    imageVector = Icons.Default.DeleteSweep,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.error
                )
            },
            title = {
                Text(
                    text = "Clear Chat History?",
                    fontWeight = FontWeight.Bold
                )
            },
            text = {
                Text("This will delete all chat messages. This action cannot be undone.")
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        viewModel.clearChat()
                        showClearDialog = false
                        showQuickActions = true
                    },
                    colors = ButtonDefaults.textButtonColors(
                        contentColor = MaterialTheme.colorScheme.error
                    )
                ) {
                    Text("Clear", fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { showClearDialog = false }) {
                    Text("Cancel")
                }
            },
            shape = RoundedCornerShape(24.dp)
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ChatTopBar(
    onMenuClick: () -> Unit,
    onClearClick: () -> Unit
) {
    TopAppBar(
        title = {
            Row(
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // AI Avatar
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .clip(CircleShape)
                        .background(
                            brush = Brush.linearGradient(
                                colors = listOf(
                                    MaterialTheme.colorScheme.primary,
                                    MaterialTheme.colorScheme.secondary
                                )
                            )
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "👨‍🍳",
                        fontSize = 24.sp
                    )
                }

                Column {
                    Text(
                        text = "Sous Chef AI",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(8.dp)
                                .clip(CircleShape)
                                .background(Color(0xFF10B981))
                        )
                        Text(
                            text = "Online",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
        },
        actions = {
            IconButton(onClick = onClearClick) {
                Icon(
                    imageVector = Icons.Default.DeleteSweep,
                    contentDescription = "Clear chat"
                )
            }
            IconButton(onClick = onMenuClick) {
                Icon(
                    imageVector = Icons.Default.MoreVert,
                    contentDescription = "More"
                )
            }
        },
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = MaterialTheme.colorScheme.surface
        )
    )
}

@Composable
private fun QuickActionButtons(
    onActionClick: (String) -> Unit
) {
    val quickActions = listOf(
        QuickAction("💡", "Storage tips", "How should I store fresh basil?"),
        QuickAction("🔄", "Substitutes", "What can I substitute for eggs?"),
        QuickAction("⏱️", "Cooking times", "How long to cook chicken breast?"),
        QuickAction("🍽️", "Recipe ideas", "What can I make with pasta and tomatoes?")
    )

    Surface(
        modifier = Modifier.fillMaxWidth(),
        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(
                text = "Quick Questions",
                style = MaterialTheme.typography.labelMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            )

            quickActions.chunked(2).forEach { rowActions ->
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    rowActions.forEach { action ->
                        QuickActionChip(
                            action = action,
                            onClick = { onActionClick(action.query) },
                            modifier = Modifier.weight(1f)
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun QuickActionChip(
    action: QuickAction,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        onClick = onClick,
        modifier = modifier,
        shape = RoundedCornerShape(12.dp),
        color = MaterialTheme.colorScheme.surface,
        border = androidx.compose.foundation.BorderStroke(
            1.dp,
            MaterialTheme.colorScheme.outline.copy(alpha = 0.3f)
        )
    ) {
        Row(
            modifier = Modifier.padding(12.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(text = action.emoji, fontSize = 20.sp)
            Text(
                text = action.label,
                style = MaterialTheme.typography.bodySmall,
                fontWeight = FontWeight.Medium,
                maxLines = 1
            )
        }
    }
}

@Composable
private fun ChatInputBar(
    inputText: String,
    onInputChange: (String) -> Unit,
    onSendClick: () -> Unit,
    isSending: Boolean,
    isEnabled: Boolean,
    modifier: Modifier = Modifier
) {
    val focusManager = LocalFocusManager.current

    Surface(
        modifier = modifier.fillMaxWidth(),
        tonalElevation = 8.dp,
        shadowElevation = 8.dp,
        color = MaterialTheme.colorScheme.surface
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.Bottom,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Text input
            OutlinedTextField(
                value = inputText,
                onValueChange = onInputChange,
                modifier = Modifier.weight(1f),
                placeholder = {
                    Text(
                        text = "Ask me anything...",
                        color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f)
                    )
                },
                maxLines = 4,
                shape = RoundedCornerShape(24.dp),
                enabled = isEnabled,
                keyboardOptions = KeyboardOptions(
                    imeAction = ImeAction.Send
                ),
                keyboardActions = KeyboardActions(
                    onSend = {
                        if (inputText.isNotBlank() && !isSending) {
                            onSendClick()
                            focusManager.clearFocus()
                        }
                    }
                ),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = MaterialTheme.colorScheme.primary,
                    unfocusedBorderColor = MaterialTheme.colorScheme.outline.copy(alpha = 0.5f)
                ),
                trailingIcon = {
                    if (inputText.isNotEmpty() && !isSending) {
                        IconButton(
                            onClick = { onInputChange("") }
                        ) {
                            Icon(
                                imageVector = Icons.Default.Clear,
                                contentDescription = "Clear",
                                tint = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
            )

            // Send button
            FloatingActionButton(
                onClick = {
                    if (inputText.isNotBlank() && !isSending) {
                        onSendClick()
                        focusManager.clearFocus()
                    }
                },
                modifier = Modifier.size(56.dp),
                containerColor = if (inputText.isNotBlank() && !isSending) {
                    MaterialTheme.colorScheme.primary
                } else {
                    MaterialTheme.colorScheme.surfaceVariant
                },
                elevation = FloatingActionButtonDefaults.elevation(
                    defaultElevation = 4.dp,
                    pressedElevation = 8.dp
                )
            ) {
                if (isSending) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(24.dp),
                        color = MaterialTheme.colorScheme.onPrimary,
                        strokeWidth = 3.dp
                    )
                } else {
                    Icon(
                        imageVector = Icons.Default.Send,
                        contentDescription = "Send",
                        tint = if (inputText.isNotBlank()) {
                            MaterialTheme.colorScheme.onPrimary
                        } else {
                            MaterialTheme.colorScheme.onSurfaceVariant
                        }
                    )
                }
            }
        }
    }
}

@Composable
private fun EnhancedChatBubble(
    message: ChatMessage,
    onCopyClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val isUser = message.role == MessageRole.USER

    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        horizontalArrangement = if (isUser) Arrangement.End else Arrangement.Start
    ) {
        if (!isUser) {
            // AI avatar
            Box(
                modifier = Modifier
                    .size(32.dp)
                    .clip(CircleShape)
                    .background(
                        brush = Brush.linearGradient(
                            colors = listOf(
                                MaterialTheme.colorScheme.primary,
                                MaterialTheme.colorScheme.secondary
                            )
                        )
                    ),
                contentAlignment = Alignment.Center
            ) {
                Text(text = "🤖", fontSize = 16.sp)
            }
            Spacer(modifier = Modifier.width(8.dp))
        }

        Column(
            modifier = Modifier.widthIn(max = 280.dp)
        ) {
            Surface(
                shape = RoundedCornerShape(
                    topStart = 20.dp,
                    topEnd = 20.dp,
                    bottomStart = if (isUser) 20.dp else 4.dp,
                    bottomEnd = if (isUser) 4.dp else 20.dp
                ),
                color = if (isUser) {
                    MaterialTheme.colorScheme.primary
                } else {
                    MaterialTheme.colorScheme.surfaceVariant
                },
                shadowElevation = 2.dp
            ) {
                Column(
                    modifier = Modifier.padding(16.dp)
                ) {
                    Text(
                        text = message.text,
                        style = MaterialTheme.typography.bodyMedium,
                        color = if (isUser) {
                            MaterialTheme.colorScheme.onPrimary
                        } else {
                            MaterialTheme.colorScheme.onSurface
                        }
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Row(
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            text = formatTimestamp(message.timestamp),
                            style = MaterialTheme.typography.labelSmall,
                            color = if (isUser) {
                                MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.7f)
                            } else {
                                MaterialTheme.colorScheme.onSurfaceVariant
                            }
                        )

                        if (!isUser) {
                            IconButton(
                                onClick = onCopyClick,
                                modifier = Modifier.size(20.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.ContentCopy,
                                    contentDescription = "Copy",
                                    tint = MaterialTheme.colorScheme.onSurfaceVariant,
                                    modifier = Modifier.size(14.dp)
                                )
                            }
                        }
                    }
                }
            }
        }

        if (isUser) {
            Spacer(modifier = Modifier.width(8.dp))
            // User avatar
            Box(
                modifier = Modifier
                    .size(32.dp)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.primary),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Person,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onPrimary,
                    modifier = Modifier.size(20.dp)
                )
            }
        }
    }
}

@Composable
private fun TypingIndicator() {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        horizontalArrangement = Arrangement.Start
    ) {
        Box(
            modifier = Modifier
                .size(32.dp)
                .clip(CircleShape)
                .background(
                    brush = Brush.linearGradient(
                        colors = listOf(
                            MaterialTheme.colorScheme.primary,
                            MaterialTheme.colorScheme.secondary
                        )
                    )
                ),
            contentAlignment = Alignment.Center
        ) {
            Text(text = "🤖", fontSize = 16.sp)
        }

        Spacer(modifier = Modifier.width(8.dp))

        Surface(
            shape = RoundedCornerShape(20.dp, 20.dp, 20.dp, 4.dp),
            color = MaterialTheme.colorScheme.surfaceVariant
        ) {
            Row(
                modifier = Modifier.padding(16.dp),
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                repeat(3) { index ->
                    val infiniteTransition = rememberInfiniteTransition(label = "typing$index")
                    val scale by infiniteTransition.animateFloat(
                        initialValue = 1f,
                        targetValue = 1.3f,
                        animationSpec = infiniteRepeatable(
                            animation = tween(600, delayMillis = index * 200),
                            repeatMode = RepeatMode.Reverse
                        ),
                        label = "scale$index"
                    )

                    Box(
                        modifier = Modifier
                            .size(8.dp)
                            .clip(CircleShape)
                            .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.6f))
                    )
                }
            }
        }
    }
}

@Composable
private fun EmptyChatState() {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(20.dp),
            modifier = Modifier.padding(32.dp)
        ) {
            // Animated chef icon
            val infiniteTransition = rememberInfiniteTransition(label = "chef")
            val scale by infiniteTransition.animateFloat(
                initialValue = 1f,
                targetValue = 1.1f,
                animationSpec = infiniteRepeatable(
                    animation = tween(1500),
                    repeatMode = RepeatMode.Reverse
                ),
                label = "scale"
            )

            Box(
                modifier = Modifier
                    .size(120.dp)
                    .clip(CircleShape)
                    .background(
                        brush = Brush.linearGradient(
                            colors = listOf(
                                MaterialTheme.colorScheme.primaryContainer,
                                MaterialTheme.colorScheme.secondaryContainer
                            )
                        )
                    ),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "👨‍🍳",
                    fontSize = 64.sp
                )
            }

            Text(
                text = "Your Sous Chef is ready!",
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center
            )

            Text(
                text = "Ask me about storage tips, cooking times, substitutions, or recipe ideas",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center
            )

            // Feature cards
            Column(
                verticalArrangement = Arrangement.spacedBy(12.dp),
                modifier = Modifier.padding(top = 16.dp)
            ) {
                FeatureCard(
                    icon = "💡",
                    title = "Smart Suggestions",
                    description = "Get personalized cooking advice"
                )
                FeatureCard(
                    icon = "📚",
                    title = "Cooking Knowledge",
                    description = "Learn techniques and tips"
                )
                FeatureCard(
                    icon = "🔄",
                    title = "Ingredient Substitutes",
                    description = "Find alternatives for any ingredient"
                )
            }
        }
    }
}

@Composable
private fun FeatureCard(
    icon: String,
    title: String,
    description: String
) {
    Surface(
        shape = RoundedCornerShape(16.dp),
        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(MaterialTheme.colorScheme.primaryContainer),
                contentAlignment = Alignment.Center
            ) {
                Text(text = icon, fontSize = 24.sp)
            }

            Column {
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = description,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

private fun formatTimestamp(timestamp: Long): String {
    val now = System.currentTimeMillis()
    val diff = now - timestamp

    return when {
        diff < 60000 -> "Just now"
        diff < 3600000 -> "${diff / 60000}m ago"
        diff < 86400000 -> "${diff / 3600000}h ago"
        else -> java.text.SimpleDateFormat("HH:mm", java.util.Locale.getDefault())
            .format(java.util.Date(timestamp))
    }
}

private data class QuickAction(
    val emoji: String,
    val label: String,
    val query: String
)
