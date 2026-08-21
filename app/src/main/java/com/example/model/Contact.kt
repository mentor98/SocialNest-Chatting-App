package com.example.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "contacts")
data class Contact(
    @PrimaryKey val id: String,
    val name: String,
    val username: String,
    val avatarResName: String = "",
    val avatarColorHex: Long = 0xFF568CF5,
    val statusMessage: String,
    val isOnline: Boolean = false,
    val lastSeenText: String = "Online",
    val initialLetter: String,
    val phone: String = "+1 (555) 019-2834",
    val email: String = "",
    val visitsCount: Int = 69,
    val messagesCount: Int = 18,
    val callsCount: Int = 87,
    val notificationsAllowed: Boolean = true,
    val remarks: String = "",
    val dynamicMoments: String = "Playing latest Nintendo Switch games & reading design trends",
    val isFavorite: Boolean = false
)
