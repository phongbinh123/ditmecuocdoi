package com.example.ffridge.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.ffridge.data.repository.RepositoryProvider
import com.example.ffridge.ui.screens.add.AddScreen
import com.example.ffridge.ui.screens.auth.AuthScreen
import com.example.ffridge.ui.screens.chat.ChatScreen
import com.example.ffridge.ui.screens.inventory.InventoryScreen
import com.example.ffridge.ui.screens.recipes.RecipesScreen
import com.example.ffridge.ui.screens.settings.SettingsScreen

@Composable
fun AppNavGraph(
    navController: NavHostController,
    onLogout: () -> Unit
) {
    val userRepository = RepositoryProvider.getUserRepository()
    val isLoggedIn by userRepository.isLoggedIn().collectAsState(initial = false)

    NavHost(
        navController = navController,
        startDestination = if (isLoggedIn) Screen.Inventory.route else Screen.Auth.route
    ) {
        // Auth Screen
        composable(Screen.Auth.route) {
            AuthScreen(
                onLoginSuccess = { user ->
                    navController.navigate(Screen.Inventory.route) {
                        popUpTo(Screen.Auth.route) { inclusive = true }
                    }
                }
            )
        }

        // Inventory Screen
        composable(Screen.Inventory.route) {
            InventoryScreen(
                onAddClick = {
                    navController.navigate(Screen.Add.route)
                },
                onEditClick = { ingredientId ->
                    navController.navigate("${Screen.Add.route}/$ingredientId")
                },
                onSettingsClick = {
                    navController.navigate(Screen.Settings.route)
                }
            )
        }

        // Add/Edit Screen
        composable(Screen.Add.route) {
            AddScreen(
                onSaveSuccess = {
                    navController.popBackStack()
                },
                onCancel = {
                    navController.popBackStack()
                }
            )
        }

        // Recipes Screen
        composable(Screen.Recipes.route) {
            RecipesScreen()
        }

        // Chat Screen
        composable(Screen.Chat.route) {
            ChatScreen()
        }

        // Settings Screen
        composable(Screen.Settings.route) {
            SettingsScreen(
                onBack = {
                    navController.popBackStack()
                },
                onLogout = {
                    onLogout()
                    navController.navigate(Screen.Auth.route) {
                        popUpTo(0) { inclusive = true }
                    }
                }
            )
        }
    }
}

sealed class Screen(val route: String) {
    object Auth : Screen("auth")
    object Inventory : Screen("inventory")
    object Add : Screen("add")
    object Recipes : Screen("recipes")
    object Chat : Screen("chat")
    object Settings : Screen("settings")
}
