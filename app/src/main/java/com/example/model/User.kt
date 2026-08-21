package com.example.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "users")
data class User(
    @PrimaryKey val id: String = "user_me",
    val name: String = "Alex Rivera",
    val username: String = "alex_rivera",
    val avatarResName: String = "ic_chat_logo",
    val bio: String = "Designing modern interfaces and connecting with friends.",
    val status: String = "Available for chat",
    val phone: String = "+1 (555) 382-9910",
    val email: String = "alex.rivera@example.com",
    val isOnline: Boolean = true,
    val isVerified: Boolean = true,
    val visitsCount: Int = 142,
    val messagesCount: Int = 389,
    val callsCount: Int = 94,
    val notificationsAllowed: Boolean = true
)
