package com.example.ffridge.ui.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ffridge.data.model.Ingredient
import com.example.ffridge.domain.model.ExpiryStatus
import com.example.ffridge.util.Constants
import com.example.ffridge.util.DateUtils
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun IngredientCard(
    ingredient: Ingredient,
    expiryStatus: ExpiryStatus,
    onEdit: () -> Unit,
    onDelete: () -> Unit,
    modifier: Modifier = Modifier
) {
    var showDeleteDialog by remember { mutableStateOf(false) }

    val cardColor by animateColorAsState(
        targetValue = when (expiryStatus) {
            is ExpiryStatus.Expired -> MaterialTheme.colorScheme.errorContainer.copy(alpha = 0.3f)
            is ExpiryStatus.ExpiringToday -> MaterialTheme.colorScheme.errorContainer.copy(alpha = 0.2f)
            is ExpiryStatus.ExpiringSoon -> MaterialTheme.colorScheme.tertiaryContainer.copy(alpha = 0.3f)
            else -> MaterialTheme.colorScheme.surfaceVariant
        },
        label = "cardColor"
    )

    Card(
        onClick = onEdit,
        modifier = modifier
            .fillMaxWidth()
            .shadow(4.dp, RoundedCornerShape(20.dp)),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(
            containerColor = cardColor
        ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 2.dp,
            pressedElevation = 6.dp
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            // Header row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    // Ingredient name with emoji
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text(
                            text = Constants.CATEGORY_ICONS[ingredient.category] ?: "📦",
                            fontSize = 28.sp
                        )
                        Column {
                            Text(
                                text = ingredient.name,
                                style = MaterialTheme.typography.titleLarge,
                                fontWeight = FontWeight.Bold,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                            Text(
                                text = "${ingredient.quantity} ${ingredient.unit}",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                fontWeight = FontWeight.Medium
                            )
                        }
                    }
                }

                // Action buttons
                Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                    IconButton(
                        onClick = onEdit,
                        modifier = Modifier.size(40.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Edit,
                            contentDescription = "Edit",
                            tint = MaterialTheme.colorScheme.primary
                        )
                    }
                    IconButton(
                        onClick = { showDeleteDialog = true },
                        modifier = Modifier.size(40.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Delete,
                            contentDescription = "Delete",
                            tint = MaterialTheme.colorScheme.error
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Category and expiry badges row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Category badge
                CategoryBadge(category = ingredient.category)

                // Expiry status badge
                if (ingredient.expiryDate != null) {
                    ExpiryStatusBadge(expiryStatus)
                }
            }

            // Added date
            Spacer(modifier = Modifier.height(8.dp))
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.CalendarToday,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.size(14.dp)
                )
                Text(
                    text = "Added ${DateUtils.getRelativeTimeString(ingredient.addedDate)}",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            // Notes
            if (!ingredient.notes.isNullOrBlank()) {
                Spacer(modifier = Modifier.height(12.dp))
                Surface(
                    color = MaterialTheme.colorScheme.surface.copy(alpha = 0.5f),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Row(
                        modifier = Modifier.padding(12.dp),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.StickyNote2,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(18.dp)
                        )
                        Text(
                            text = ingredient.notes,
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurface,
                            maxLines = 2,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                }
            }
        }
    }

    // Delete confirmation dialog
    if (showDeleteDialog) {
        AlertDialog(
            onDismissRequest = { showDeleteDialog = false },
            title = { Text("Delete ${ingredient.name}?") },
            text = { Text("This action cannot be undone.") },
            confirmButton = {
                TextButton(
                    onClick = {
                        onDelete()
                        showDeleteDialog = false
                    },
                    colors = ButtonDefaults.textButtonColors(
                        contentColor = MaterialTheme.colorScheme.error
                    )
                ) {
                    Text("Delete")
                }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }
}

@Composable
private fun CategoryBadge(category: String) {
    Surface(
        color = MaterialTheme.colorScheme.primary.copy(alpha = 0.15f),
        shape = RoundedCornerShape(10.dp)
    ) {
        Text(
            text = category.lowercase().capitalize(),
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
            style = MaterialTheme.typography.labelMedium,
            color = MaterialTheme.colorScheme.primary,
            fontWeight = FontWeight.Bold
        )
    }
}

@Composable
private fun ExpiryStatusBadge(status: ExpiryStatus) {
    val (text, color, icon) = when (status) {
        is ExpiryStatus.Expired -> Triple(
            "Expired ${status.daysAgo}d ago",
            MaterialTheme.colorScheme.error,
            Icons.Default.ErrorOutline
        )
        is ExpiryStatus.ExpiringToday -> Triple(
            "Expires today!",
            MaterialTheme.colorScheme.error,
            Icons.Default.Warning
        )
        is ExpiryStatus.ExpiringSoon -> Triple(
            "${status.daysLeft}d left",
            MaterialTheme.colorScheme.tertiary,
            Icons.Default.Schedule
        )
        is ExpiryStatus.ExpiringThisWeek -> Triple(
            "${status.daysLeft}d left",
            MaterialTheme.colorScheme.primary,
            Icons.Default.EventAvailable
        )
        is ExpiryStatus.Fresh -> Triple(
            "${status.daysLeft}d left",
            MaterialTheme.colorScheme.primary.copy(alpha = 0.7f),
            Icons.Default.CheckCircle
        )
        ExpiryStatus.NoExpiry -> Triple(
            "No expiry",
            MaterialTheme.colorScheme.onSurfaceVariant,
            Icons.Default.AllInclusive
        )
    }

    Surface(
        color = color.copy(alpha = 0.15f),
        shape = RoundedCornerShape(10.dp)
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
            horizontalArrangement = Arrangement.spacedBy(6.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = color,
                modifier = Modifier.size(16.dp)
            )
            Text(
                text = text,
                style = MaterialTheme.typography.labelMedium,
                color = color,
                fontWeight = FontWeight.Bold
            )
        }
    }
}
