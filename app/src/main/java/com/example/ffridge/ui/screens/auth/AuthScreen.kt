package com.example.ffridge.ui.screens.auth

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.ffridge.data.model.User

@Composable
fun AuthScreen(
    onLoginSuccess: (User) -> Unit,
    modifier: Modifier = Modifier,
    viewModel: AuthViewModel = viewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    Box(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(24.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(24.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Spacer(modifier = Modifier.height(32.dp))

            // App logo/icon
            Text(
                text = "🍳",
                fontSize = 80.sp
            )

            // App name
            Text(
                text = "ffridge.",
                fontSize = 48.sp,
                fontWeight = FontWeight.Black,
                style = MaterialTheme.typography.displayLarge.copy(
                    brush = Brush.linearGradient(
                        colors = listOf(
                            MaterialTheme.colorScheme.primary,
                            MaterialTheme.colorScheme.secondary
                        )
                    )
                )
            )

            Text(
                text = "Your Smart Kitchen Assistant",
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Login form card
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = MaterialTheme.shapes.extraLarge,
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surfaceVariant
                )
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(24.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Text(
                        text = "Welcome!",
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.Bold
                    )

                    Text(
                        text = "Enter your details to get started",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    // Display Name field
                    OutlinedTextField(
                        value = uiState.displayName,
                        onValueChange = { viewModel.updateDisplayName(it) },
                        label = { Text("Your Name") },
                        leadingIcon = {
                            Icon(Icons.Default.Person, contentDescription = null)
                        },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true,
                        isError = uiState.error?.contains("name") == true
                    )

                    // Email field
                    OutlinedTextField(
                        value = uiState.email,
                        onValueChange = { viewModel.updateEmail(it) },
                        label = { Text("Email") },
                        leadingIcon = {
                            Icon(Icons.Default.Email, contentDescription = null)
                        },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true,
                        isError = uiState.error?.contains("email") == true
                    )

                    // Error message
                    if (uiState.error != null) {
                        Text(
                            text = uiState.error!!,
                            color = MaterialTheme.colorScheme.error,
                            style = MaterialTheme.typography.bodySmall
                        )
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    // Login button
                    Button(
                        onClick = { viewModel.login(onLoginSuccess) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(56.dp),
                        enabled = !uiState.isLoading,
                        shape = MaterialTheme.shapes.medium
                    ) {
                        if (uiState.isLoading) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(24.dp),
                                color = MaterialTheme.colorScheme.onPrimary
                            )
                        } else {
                            Text(
                                text = "Get Started",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Features list
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                FeatureItem(
                    icon = "📦",
                    title = "Track Ingredients",
                    description = "Never let food go to waste"
                )
                FeatureItem(
                    icon = "🍽️",
                    title = "Generate Recipes",
                    description = "AI-powered recipe suggestions"
                )
                FeatureItem(
                    icon = "👨‍🍳",
                    title = "Ask Your Chef",
                    description = "Get cooking tips and advice"
                )
            }

            Spacer(modifier = Modifier.height(32.dp))
        }
    }
}

@Composable
private fun FeatureItem(
    icon: String,
    title: String,
    description: String
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Surface(
            modifier = Modifier.size(48.dp),
            shape = MaterialTheme.shapes.medium,
            color = MaterialTheme.colorScheme.primaryContainer
        ) {
            Box(contentAlignment = Alignment.Center) {
                Text(text = icon, fontSize = 24.sp)
            }
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
