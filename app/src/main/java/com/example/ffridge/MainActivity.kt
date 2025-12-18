package com.example.ffridge

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.ffridge.data.repository.RepositoryProvider
import com.example.ffridge.ui.components.BottomNavBar
import com.example.ffridge.ui.components.TopBar
import com.example.ffridge.ui.navigation.AppNavGraph
import com.example.ffridge.ui.navigation.Screen
import com.example.ffridge.ui.theme.FfridgeTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            val navController = rememberNavController()
            val userRepository = RepositoryProvider.getUserRepository()

            // Observe theme setting
            val settings by userRepository.getSettings().collectAsState(
                initial = com.example.ffridge.data.model.UserSettings()
            )

            // Observe user login state
            val isLoggedIn by userRepository.isLoggedIn().collectAsState(initial = false)

            FfridgeTheme(appTheme = settings.theme) {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    val navBackStackEntry by navController.currentBackStackEntryAsState()
                    val currentRoute = navBackStackEntry?.destination?.route

                    // Show bottom nav only when logged in and not on auth/settings screen
                    val showBottomNav = isLoggedIn &&
                            currentRoute != Screen.Auth.route &&
                            currentRoute != Screen.Settings.route

                    // Show top bar only when logged in and not on auth screen
                    val showTopBar = isLoggedIn && currentRoute != Screen.Auth.route

                    Scaffold(
                        topBar = {
                            if (showTopBar) {
                                TopBar(
                                    itemCount = 0, // TODO: Get from ViewModel
                                    onSettingsClick = {
                                        navController.navigate(Screen.Settings.route)
                                    }
                                )
                            }
                        },
                        bottomBar = {
                            if (showBottomNav) {
                                BottomNavBar(
                                    selectedRoute = currentRoute ?: Screen.Inventory.route,
                                    onNavigate = { route ->
                                        navController.navigate(route) {
                                            // Pop up to inventory and save state
                                            popUpTo(Screen.Inventory.route) {
                                                saveState = true
                                            }
                                            // Avoid multiple copies
                                            launchSingleTop = true
                                            // Restore state when reselecting
                                            restoreState = true
                                        }
                                    }
                                )
                            }
                        }
                    ) { paddingValues ->
                        Box(modifier = Modifier.padding(paddingValues)) {
                            AppNavGraph(
                                navController = navController,
                                onLogout = {
                                    // Handle logout
                                }
                            )
                        }
                    }
                }
            }
        }
    }
}
