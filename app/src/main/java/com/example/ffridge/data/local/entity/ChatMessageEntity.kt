package com.example.ffridge.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "chat_messages")
data class ChatMessageEntity(
    @PrimaryKey val id: String,
    val role: String, // "USER" or "MODEL"
    val text: String,
    val timestamp: Long
)
