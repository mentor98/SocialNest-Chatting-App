package com.example.model

import androidx.room.Entity
import androidx.room.PrimaryKey

enum class MessageType {
    TEXT,
    IMAGE,
    VOICE,
    FILE,
    CALL_LOG
}

enum class MessageStatus {
    SENDING,
    SENT,
    DELIVERED,
    READ
}

@Entity(tableName = "messages")
data class Message(
    @PrimaryKey val id: String,
    val conversationId: String,
    val senderId: String, // "user_me" or contactId
    val text: String,
    val type: MessageType = MessageType.TEXT,
    val timestamp: Long = System.currentTimeMillis(),
    val status: MessageStatus = MessageStatus.READ,
    val attachmentUrl: String = "",
    val attachmentName: String = "",
    val attachmentSize: String = "",
    val voiceDurationSeconds: Int = 0,
    val voiceWaveform: String = "", // comma-separated float amplitudes e.g. "0.2,0.5,0.8,0.3"
    val reactions: String = "", // comma-separated reactions or emoji
    val replyToMessageId: String? = null,
    val replyToText: String? = null,
    val isEdited: Boolean = false,
    val isDeleted: Boolean = false
) {
    val isOutgoing: Boolean
        get() = senderId == "user_me"
}
