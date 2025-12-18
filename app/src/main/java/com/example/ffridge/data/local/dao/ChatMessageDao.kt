package com.example.ffridge.data.local.dao

import androidx.room.*
import com.example.ffridge.data.local.entity.ChatMessageEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface ChatMessageDao {

    @Query("SELECT * FROM chat_messages ORDER BY timestamp ASC")
    fun getAllMessages(): Flow<List<ChatMessageEntity>>

    @Query("SELECT * FROM chat_messages WHERE id = :id")
    suspend fun getMessageById(id: String): ChatMessageEntity?

    @Query("SELECT * FROM chat_messages WHERE role = :role ORDER BY timestamp DESC")
    fun getMessagesByRole(role: String): Flow<List<ChatMessageEntity>>

    @Query("SELECT * FROM chat_messages ORDER BY timestamp DESC LIMIT :limit")
    fun getRecentMessages(limit: Int): Flow<List<ChatMessageEntity>>

    @Query("SELECT COUNT(*) FROM chat_messages")
    fun getMessageCount(): Flow<Int>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMessage(message: ChatMessageEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMessages(messages: List<ChatMessageEntity>)

    @Update
    suspend fun updateMessage(message: ChatMessageEntity)

    @Delete
    suspend fun deleteMessage(message: ChatMessageEntity)

    @Query("DELETE FROM chat_messages WHERE id = :id")
    suspend fun deleteMessageById(id: String)

    @Query("DELETE FROM chat_messages")
    suspend fun deleteAllMessages()

    @Query("""
        DELETE FROM chat_messages 
        WHERE id NOT IN (
            SELECT id FROM chat_messages 
            ORDER BY timestamp DESC 
            LIMIT :keepCount
        )
    """)
    suspend fun deleteOldMessages(keepCount: Int)
}
