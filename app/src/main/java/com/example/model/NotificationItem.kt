package com.example.model

import androidx.room.Entity
import androidx.room.PrimaryKey

enum class NotificationType {
    MESSAGE,
    MISSED_CALL,
    FRIEND_REQUEST,
    SYSTEM
}

@Entity(tableName = "notifications")
data class NotificationItem(
    @PrimaryKey val id: String,
    val title: String,
    val description: String,
    val timeAgo: String,
    val type: NotificationType = NotificationType.MESSAGE,
    val isRead: Boolean = false,
    val contactId: String? = null,
    val avatarResName: String = "",
    val avatarColorHex: Long = 0xFF568CF5,
    val timestamp: Long = System.currentTimeMillis()
)
