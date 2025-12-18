package com.example.ffridge.ui.screens.chat

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.ffridge.data.model.ChatMessage
import com.example.ffridge.data.model.MessageRole
import com.example.ffridge.data.remote.GeminiService
import com.example.ffridge.data.repository.ChatRepository
import com.example.ffridge.data.repository.RepositoryProvider
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.util.UUID

data class ChatUiState(
    val messages: List<ChatMessage> = emptyList(),
    val inputText: String = "",
    val isSending: Boolean = false,
    val isLoading: Boolean = false,
    val error: String? = null
)

class ChatViewModel : ViewModel() {

    private val chatRepository: ChatRepository =
        RepositoryProvider.getChatRepository()
    private val geminiService = GeminiService()

    private val _uiState = MutableStateFlow(ChatUiState())
    val uiState: StateFlow<ChatUiState> = _uiState.asStateFlow()

    init {
        loadMessages()
    }

    private fun loadMessages() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }

            chatRepository.getAllMessages()
                .catch { e ->
                    _uiState.update {
                        it.copy(isLoading = false, error = e.message)
                    }
                }
                .collect { messages ->
                    _uiState.update {
                        it.copy(
                            messages = messages,
                            isLoading = false,
                            error = null
                        )
                    }
                }
        }
    }

    fun updateInputText(text: String) {
        _uiState.update { it.copy(inputText = text) }
    }

    fun sendMessage() {
        val text = _uiState.value.inputText.trim()
        if (text.isBlank()) return

        viewModelScope.launch {
            _uiState.update {
                it.copy(isSending = true, inputText = "", error = null)
            }

            // Save user message
            val userMessage = ChatMessage(
                id = UUID.randomUUID().toString(),
                role = MessageRole.USER,
                text = text,
                timestamp = System.currentTimeMillis()
            )
            chatRepository.insertMessage(userMessage)

            // Get AI response
            try {
                val conversationHistory = chatRepository.getConversationHistory(10)
                val response = geminiService.sendMessage(text, conversationHistory)

                // Save model response
                val modelMessage = ChatMessage(
                    id = UUID.randomUUID().toString(),
                    role = MessageRole.MODEL,
                    text = response,
                    timestamp = System.currentTimeMillis()
                )
                chatRepository.insertMessage(modelMessage)

                _uiState.update { it.copy(isSending = false) }
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(
                        isSending = false,
                        error = e.message ?: "Failed to get response"
                    )
                }
            }
        }
    }

    fun clearChat() {
        viewModelScope.launch {
            chatRepository.deleteAllMessages()

            // Re-add welcome message
            val welcomeMessage = ChatMessage(
                id = UUID.randomUUID().toString(),
                role = MessageRole.MODEL,
                text = "Hello! I'm your Sous Chef. Ask me about storage tips, cooking times, or substitutions!",
                timestamp = System.currentTimeMillis()
            )
            chatRepository.insertMessage(welcomeMessage)
        }
    }

    fun deleteMessage(message: ChatMessage) {
        viewModelScope.launch {
            chatRepository.deleteMessage(message)
        }
    }
}
