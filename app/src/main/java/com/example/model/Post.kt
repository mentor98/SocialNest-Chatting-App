package com.example.model

enum class ReactionType(val emoji: String, val label: String, val colorHex: Long) {
    LIKE("👍", "Like", 0xFF1877F2),
    LOVE("❤️", "Love", 0xFFFA3E3E),
    CARE("🥰", "Care", 0xFFF7B125),
    HAHA("😆", "Haha", 0xFFF7B125),
    WOW("😮", "Wow", 0xFFF7B125),
    SAD("😢", "Sad", 0xFFF7B125),
    ANGRY("😡", "Angry", 0xFFE95E39)
}

data class PostComment(
    val id: String,
    val authorName: String,
    val authorAvatarResName: String = "",
    val authorAvatarColorHex: Long = 0xFF1877F2,
    val text: String,
    val timeAgo: String = "Just now",
    val likesCount: Int = 0,
    val isLiked: Boolean = false
)

data class FeedPost(
    val id: String,
    val authorId: String,
    val authorName: String,
    val authorAvatarResName: String = "",
    val authorAvatarColorHex: Long = 0xFF1877F2,
    val isVerified: Boolean = false,
    val timeAgo: String = "1h ago",
    val contentText: String,
    val postGradientIndex: Int = -1, // -1 means regular text or image, 0..4 gradient color cards
    val imageDescription: String = "",
    val likesCount: Int = 12,
    val commentsCount: Int = 4,
    val sharesCount: Int = 2,
    val userReaction: ReactionType? = null,
    val topReactions: List<ReactionType> = listOf(ReactionType.LIKE, ReactionType.LOVE),
    val comments: List<PostComment> = emptyList(),
    val location: String? = null,
    val isPinned: Boolean = false
)
