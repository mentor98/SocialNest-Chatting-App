package com.example.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "conversations")
data class Conversation(
    @PrimaryKey val id: String,
    val contactId: String,
    val contactName: String,
    val contactAvatarResName: String = "",
    val contactAvatarColorHex: Long = 0xFF568CF5,
    val isOnline: Boolean = false,
    val lastMessageText: String = "",
    val lastMessageTime: Long = System.currentTimeMillis(),
    val unreadCount: Int = 0,
    val isPinned: Boolean = false,
    val isArchived: Boolean = false,
    val draftText: String = ""
)
