package com.example.ffridge.data.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import java.util.UUID

@Parcelize
data class ChatMessage(
    val id: String = UUID.randomUUID().toString(),
    val role: MessageRole,
    val text: String,
    val timestamp: Long = System.currentTimeMillis()
) : Parcelable

enum class MessageRole {
    USER, MODEL
}
