package com.example.ffridge.ui.screens.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.ffridge.data.model.AppTheme
import com.example.ffridge.data.model.User
import com.example.ffridge.data.model.UserSettings
import com.example.ffridge.data.repository.RepositoryProvider
import com.example.ffridge.data.repository.UserRepository
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

data class SettingsUiState(
    val user: User? = null,
    val settings: UserSettings = UserSettings(),
    val isLoading: Boolean = false,
    val error: String? = null,
    val showLogoutConfirmation: Boolean = false
)

class SettingsViewModel : ViewModel() {

    private val userRepository: UserRepository =
        RepositoryProvider.getUserRepository()

    private val _uiState = MutableStateFlow(SettingsUiState())
    val uiState: StateFlow<SettingsUiState> = _uiState.asStateFlow()

    init {
        loadUserData()
        loadSettings()
    }

    private fun loadUserData() {
        viewModelScope.launch {
            userRepository.getUser()
                .collect { user ->
                    _uiState.update { it.copy(user = user) }
                }
        }
    }

    private fun loadSettings() {
        viewModelScope.launch {
            userRepository.getSettings()
                .collect { settings ->
                    _uiState.update { it.copy(settings = settings) }
                }
        }
    }

    fun updateExpiryNotifications(enabled: Boolean) {
        viewModelScope.launch {
            userRepository.updateExpiryNotifications(enabled)
        }
    }

    fun updateTheme(theme: AppTheme) {
        viewModelScope.launch {
            userRepository.updateTheme(theme)
        }
    }

    fun updateUiScale(scale: Float) {
        viewModelScope.launch {
            userRepository.updateUiScale(scale)
        }
    }

    fun updateNotificationTime(time: String) {
        viewModelScope.launch {
            userRepository.updateNotificationTime(time)
        }
    }

    fun showLogoutConfirmation() {
        _uiState.update { it.copy(showLogoutConfirmation = true) }
    }

    fun hideLogoutConfirmation() {
        _uiState.update { it.copy(showLogoutConfirmation = false) }
    }

    fun logout(onLogoutSuccess: () -> Unit) {
        viewModelScope.launch {
            userRepository.logout()
            onLogoutSuccess()
        }
    }
}
