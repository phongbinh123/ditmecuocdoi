package com.example.ffridge.ui.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.ui.graphics.vector.ImageVector

sealed class NavScreen(
    val route: String,
    val title: String,
    val icon: ImageVector,
    val selectedIcon: ImageVector = icon
) {
    object Inventory : NavScreen(
        route = "inventory",
        title = "Fridge",
        icon = Icons.Default.KitchenOutlined,
        selectedIcon = Icons.Default.Kitchen
    )

    object Add : NavScreen(
        route = "add",
        title = "Add",
        icon = Icons.Default.AddCircleOutline,
        selectedIcon = Icons.Default.AddCircle
    )

    object Recipes : NavScreen(
        route = "recipes",
        title = "Cook",
        icon = Icons.Default.RestaurantMenuOutlined,
        selectedIcon = Icons.Default.RestaurantMenu
    )

    object Chat : NavScreen(
        route = "chat",
        title = "Chef",
        icon = Icons.Default.ChatBubbleOutline,
        selectedIcon = Icons.Default.ChatBubble
    )

    object Settings : NavScreen(
        route = "settings",
        title = "Settings",
        icon = Icons.Default.Settings
    )

    object Auth : NavScreen(
        route = "auth",
        title = "Auth",
        icon = Icons.Default.Login
    )
}

// Extension property for bottom nav items
val bottomNavItems = listOf(
    NavScreen.Inventory,
    NavScreen.Add,
    NavScreen.Recipes,
    NavScreen.Chat
)
