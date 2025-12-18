package com.example.ffridge.data.mapper

import com.example.ffridge.data.local.entity.ChatMessageEntity
import com.example.ffridge.data.model.ChatMessage
import com.example.ffridge.data.model.MessageRole

fun ChatMessageEntity.toChatMessage(): ChatMessage {
    return ChatMessage(
        id = this.id,
        role = MessageRole.valueOf(this.role),
        text = this.text,
        timestamp = this.timestamp
    )
}

fun ChatMessage.toEntity(): ChatMessageEntity {
    return ChatMessageEntity(
        id = this.id,
        role = this.role.name,
        text = this.text,
        timestamp = this.timestamp
    )
}

fun List<ChatMessageEntity>.toChatMessageList(): List<ChatMessage> {
    return this.map { it.toChatMessage() }
}

fun List<ChatMessage>.toChatMessageEntityList(): List<ChatMessageEntity> {
    return this.map { it.toEntity() }
}
