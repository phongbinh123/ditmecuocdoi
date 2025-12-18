package com.example.ffridge.ui.screens.auth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.ffridge.data.model.User
import com.example.ffridge.data.repository.RepositoryProvider
import com.example.ffridge.data.repository.UserRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.util.UUID

data class AuthUiState(
    val email: String = "",
    val displayName: String = "",
    val isLoading: Boolean = false,
    val error: String? = null
)

class AuthViewModel : ViewModel() {

    private val userRepository: UserRepository =
        RepositoryProvider.getUserRepository()

    private val _uiState = MutableStateFlow(AuthUiState())
    val uiState: StateFlow<AuthUiState> = _uiState.asStateFlow()

    fun updateEmail(email: String) {
        _uiState.update { it.copy(email = email, error = null) }
    }

    fun updateDisplayName(name: String) {
        _uiState.update { it.copy(displayName = name, error = null) }
    }

    fun login(onSuccess: (User) -> Unit) {
        viewModelScope.launch {
            val state = _uiState.value

            // Simple validation
            if (state.email.isBlank()) {
                _uiState.update { it.copy(error = "Please enter email") }
                return@launch
            }
            if (state.displayName.isBlank()) {
                _uiState.update { it.copy(error = "Please enter your name") }
                return@launch
            }

            _uiState.update { it.copy(isLoading = true, error = null) }

            try {
                val user = User(
                    id = UUID.randomUUID().toString(),
                    email = state.email,
                    displayName = state.displayName,
                    avatarUrl = null
                )

                userRepository.saveUser(user)

                _uiState.update { it.copy(isLoading = false) }
                onSuccess(user)
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        error = e.message ?: "Login failed"
                    )
                }
            }
        }
    }
}
