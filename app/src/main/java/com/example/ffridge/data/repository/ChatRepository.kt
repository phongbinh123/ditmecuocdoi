package com.example.ffridge.data.repository

import com.example.ffridge.data.local.dao.ChatMessageDao
import com.example.ffridge.data.mapper.toChatMessage
import com.example.ffridge.data.mapper.toChatMessageList
import com.example.ffridge.data.mapper.toEntity
import com.example.ffridge.data.model.ChatMessage
import com.example.ffridge.data.model.MessageRole
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class ChatRepository(
    private val chatMessageDao: ChatMessageDao
) {

    /**
     * Get all chat messages
     */
    fun getAllMessages(): Flow<List<ChatMessage>> {
        return chatMessageDao.getAllMessages().map { entities ->
            entities.toChatMessageList()
        }
    }

    /**
     * Get message by ID
     */
    suspend fun getMessageById(id: String): ChatMessage? {
        return chatMessageDao.getMessageById(id)?.toChatMessage()
    }

    /**
     * Get messages by role
     */
    fun getMessagesByRole(role: MessageRole): Flow<List<ChatMessage>> {
        return chatMessageDao.getMessagesByRole(role.name).map { entities ->
            entities.toChatMessageList()
        }
    }

    /**
     * Get recent messages
     */
    fun getRecentMessages(limit: Int = 50): Flow<List<ChatMessage>> {
        return chatMessageDao.getRecentMessages(limit).map { entities ->
            entities.toChatMessageList()
        }
    }

    /**
     * Get message count
     */
    fun getMessageCount(): Flow<Int> {
        return chatMessageDao.getMessageCount()
    }

    /**
     * Insert message
     */
    suspend fun insertMessage(message: ChatMessage) {
        chatMessageDao.insertMessage(message.toEntity())
    }

    /**
     * Insert multiple messages
     */
    suspend fun insertMessages(messages: List<ChatMessage>) {
        val entities = messages.map { it.toEntity() }
        chatMessageDao.insertMessages(entities)
    }

    /**
     * Update message
     */
    suspend fun updateMessage(message: ChatMessage) {
        chatMessageDao.updateMessage(message.toEntity())
    }

    /**
     * Delete message
     */
    suspend fun deleteMessage(message: ChatMessage) {
        chatMessageDao.deleteMessage(message.toEntity())
    }

    /**
     * Delete message by ID
     */
    suspend fun deleteMessageById(id: String) {
        chatMessageDao.deleteMessageById(id)
    }

    /**
     * Delete all messages
     */
    suspend fun deleteAllMessages() {
        chatMessageDao.deleteAllMessages()
    }

    /**
     * Delete old messages, keep only the most recent ones
     */
    suspend fun deleteOldMessages(keepCount: Int = 100) {
        chatMessageDao.deleteOldMessages(keepCount)
    }

    /**
     * Get user messages only
     */
    fun getUserMessages(): Flow<List<ChatMessage>> {
        return getMessagesByRole(MessageRole.USER)
    }

    /**
     * Get model messages only
     */
    fun getModelMessages(): Flow<List<ChatMessage>> {
        return getMessagesByRole(MessageRole.MODEL)
    }

    /**
     * Get conversation history formatted for Gemini API
     */
    suspend fun getConversationHistory(limit: Int = 10): List<ChatMessage> {
        var messages = listOf<ChatMessage>()
        getRecentMessages(limit).collect { msgs ->
            messages = msgs.reversed() // Reverse to get chronological order
        }
        return messages
    }
}
