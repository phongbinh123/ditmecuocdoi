package com.example.ffridge.ui.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ffridge.ui.navigation.NavScreen
import com.example.ffridge.ui.navigation.bottomNavItems

@Composable
fun BottomNavBar(
    selectedRoute: String,
    onNavigate: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp)
            .shadow(
                elevation = 8.dp,
                shape = RoundedCornerShape(28.dp),
                clip = false
            ),
        shape = RoundedCornerShape(28.dp),
        color = MaterialTheme.colorScheme.surface.copy(alpha = 0.95f),
        tonalElevation = 3.dp
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 12.dp, horizontal = 8.dp),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically
        ) {
            bottomNavItems.forEach { item ->
                BottomNavItemView(
                    item = item,
                    isSelected = selectedRoute == item.route,
                    onClick = { onNavigate(item.route) }
                )
            }
        }
    }
}

@Composable
private fun BottomNavItemView(
    item: NavScreen,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    val animatedScale by animateDpAsState(
        targetValue = if (isSelected) 4.dp else 0.dp,
        animationSpec = spring(dampingRatio = 0.8f),
        label = "scale"
    )

    val backgroundColor by animateColorAsState(
        targetValue = if (isSelected) {
            MaterialTheme.colorScheme.primaryContainer
        } else {
            Color.Transparent
        },
        animationSpec = tween(300),
        label = "background"
    )

    val contentColor by animateColorAsState(
        targetValue = if (isSelected) {
            MaterialTheme.colorScheme.onPrimaryContainer
        } else {
            MaterialTheme.colorScheme.onSurfaceVariant
        },
        animationSpec = tween(300),
        label = "content"
    )

    Surface(
        onClick = onClick,
        modifier = Modifier
            .clip(RoundedCornerShape(20.dp))
            .padding(4.dp),
        color = backgroundColor,
        shape = RoundedCornerShape(20.dp)
    ) {
        Box(
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 10.dp),
            contentAlignment = Alignment.Center
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Icon(
                    imageVector = if (isSelected) item.selectedIcon else item.icon,
                    contentDescription = item.title,
                    tint = contentColor,
                    modifier = Modifier.size(26.dp)
                )

                Text(
                    text = item.title,
                    fontSize = 11.sp,
                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                    color = contentColor
                )

                // Selection indicator
                if (isSelected) {
                    Box(
                        modifier = Modifier
                            .size(width = animatedScale, height = 3.dp)
                            .clip(CircleShape)
                            .background(
                                brush = Brush.horizontalGradient(
                                    colors = listOf(
                                        MaterialTheme.colorScheme.primary,
                                        MaterialTheme.colorScheme.secondary
                                    )
                                )
                            )
                    )
                }
            }
        }
    }
}
