package com.example.model

data class StatusStory(
    val id: String,
    val text: String = "",
    val mediaGradientIndex: Int = 0,
    val timeAgo: String = "Just now",
    val timestamp: Long = System.currentTimeMillis(),
    val viewsCount: Int = 24
)

data class UserStatusUpdate(
    val id: String,
    val contactId: String,
    val contactName: String,
    val avatarResName: String = "",
    val avatarColorHex: Long = 0xFF1877F2,
    val isSelf: Boolean = false,
    val stories: List<StatusStory> = emptyList(),
    val lastUpdatedText: String = "Just now",
    val isViewed: Boolean = false
)

data class WhatsAppChannel(
    val id: String,
    val name: String,
    val handle: String,
    val avatarColorHex: Long = 0xFF25D366,
    val verified: Boolean = true,
    val followersCount: String = "1.2M followers",
    val lastUpdateText: String,
    val lastUpdateTime: String = "Today",
    val isFollowing: Boolean = false
)
