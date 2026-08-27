package com.example.ui.screens

import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.model.*
import com.example.ui.components.AvatarView
import com.example.ui.components.BottomNavigationDock
import com.example.ui.theme.*
import com.example.viewmodel.ChatViewModel
import com.example.viewmodel.NavTab
import com.example.viewmodel.Screen

val postGradients = listOf(
    Brush.linearGradient(listOf(Color(0xFFFF5E62), Color(0xFFFF9966))), // Sunset
    Brush.linearGradient(listOf(Color(0xFF0052D4), Color(0xFF4364F7), Color(0xFF6FB1FC))), // Ocean
    Brush.linearGradient(listOf(Color(0xFF11998E), Color(0xFF38EF7D))), // Emerald
    Brush.linearGradient(listOf(Color(0xFF8A2387), Color(0xFFE94057), Color(0xFFF27121))), // Berry Fire
    Brush.linearGradient(listOf(Color(0xFF4776E6), Color(0xFF8E54E9))) // Royal Purple
)

@Composable
fun FeedScreen(
    viewModel: ChatViewModel,
    modifier: Modifier = Modifier
) {
    val uiState by viewModel.uiState.collectAsState()
    var showCreatePostSheet by remember { mutableStateOf(false) }
    var showShareDialogForPost by remember { mutableStateOf<FeedPost?>(null) }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(FacebookBackground)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
        ) {
            // Top Meta Facebook & WhatsApp Header
            FeedTopHeader(
                unreadChatsCount = uiState.conversations.sumOf { it.unreadCount },
                onSearchClick = { viewModel.navigateTo(Screen.Search) },
                onMessengerClick = { viewModel.setActiveNavTab(NavTab.CONVERSATIONS) },
                onCreatePostClick = { showCreatePostSheet = true }
            )

            // Posts & Stories Feed
            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                contentPadding = PaddingValues(bottom = 85.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                // "What's on your mind?" Card
                item {
                    CreatePostPromptCard(
                        user = uiState.currentUser,
                        onClick = { showCreatePostSheet = true }
                    )
                }

                // Stories Tray (Facebook & WhatsApp Stories)
                item {
                    StoriesHorizontalTray(
                        user = uiState.currentUser,
                        statusUpdates = uiState.statusUpdates,
                        onAddStory = { viewModel.setActiveNavTab(NavTab.UPDATES) },
                        onViewStory = { status -> viewModel.openStoryViewer(status) }
                    )
                }

                // Feed Posts List
                items(uiState.feedPosts, key = { it.id }) { post ->
                    FeedPostCard(
                        post = post,
                        isPickerOpen = uiState.activeReactionPostId == post.id,
                        onReactionButtonClick = {
                            if (post.userReaction != null) {
                                viewModel.togglePostReaction(post.id, post.userReaction)
                            } else {
                                viewModel.togglePostReaction(post.id, ReactionType.LIKE)
                            }
                        },
                        onLongPressReaction = {
                            viewModel.openReactionPicker(if (uiState.activeReactionPostId == post.id) null else post.id)
                        },
                        onSelectReaction = { reaction ->
                            viewModel.togglePostReaction(post.id, reaction)
                        },
                        onCommentClick = {
                            viewModel.openPostComments(post)
                        },
                        onShareClick = {
                            showShareDialogForPost = post
                        },
                        onAuthorClick = {
                            viewModel.navigateTo(Screen.ContactProfile(post.authorId))
                        }
                    )
                }
            }
        }

        // Floating Bottom Navigation
        BottomNavigationDock(
            activeTab = uiState.activeNavTab,
            onTabSelected = { tab -> viewModel.setActiveNavTab(tab) },
            unreadNotificationsCount = uiState.conversations.sumOf { it.unreadCount },
            modifier = Modifier.align(Alignment.BottomCenter)
        )

        // Create Post Bottom Sheet
        if (showCreatePostSheet) {
            CreatePostBottomSheet(
                user = uiState.currentUser,
                onDismiss = { showCreatePostSheet = false },
                onPostCreated = { text, gradientIndex, location ->
                    showCreatePostSheet = false
                    viewModel.createNewPost(text, gradientIndex, location)
                }
            )
        }

        // Comments Bottom Sheet
        if (uiState.activeCommentsPost != null) {
            val activePost = uiState.activeCommentsPost!!
            CommentsBottomSheet(
                post = activePost,
                onDismiss = { viewModel.openPostComments(null) },
                onAddComment = { text -> viewModel.addPostComment(activePost.id, text) },
                onLikeComment = { commentId -> viewModel.likePostComment(activePost.id, commentId) }
            )
        }

        // Share Dialog
        if (showShareDialogForPost != null) {
            val postToShare = showShareDialogForPost!!
            SharePostDialog(
                post = postToShare,
                contacts = uiState.contacts,
                onDismiss = { showShareDialogForPost = null },
                onShareToWhatsApp = { contact ->
                    showShareDialogForPost = null
                    viewModel.openChatWithContact(contact)
                    viewModel.sendMessage("Shared from Feed: \"${postToShare.contentText}\"")
                    viewModel.showToast("Shared to ${contact.name}")
                },
                onShareToFeed = {
                    showShareDialogForPost = null
                    viewModel.createNewPost("Shared: ${postToShare.contentText}", postToShare.postGradientIndex)
                },
                onCopyLink = {
                    showShareDialogForPost = null
                    viewModel.showToast("Post link copied to clipboard")
                }
            )
        }

        // Status Story Full-Screen Viewer
        if (uiState.activeStoryViewer != null) {
            StatusViewerDialog(
                statusUpdate = uiState.activeStoryViewer!!,
                onDismiss = { viewModel.openStoryViewer(null) },
                onReply = { replyText ->
                    viewModel.replyToStatus(uiState.activeStoryViewer!!.contactName, replyText)
                }
            )
        }
    }
}

@Composable
fun FeedTopHeader(
    unreadChatsCount: Int,
    onSearchClick: () -> Unit,
    onMessengerClick: () -> Unit,
    onCreatePostClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.surface)
            .padding(horizontal = 16.dp, vertical = 10.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        // Facebook Branding + WhatsApp Indicator
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            Text(
                text = "SocialNest",
                style = MaterialTheme.typography.titleLarge.copy(
                    fontWeight = FontWeight.Black,
                    fontSize = 26.sp,
                    letterSpacing = (-1).sp
                ),
                color = FacebookBlue
            )

            // Hybrid WhatsApp Badge
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(10.dp))
                    .background(WhatsAppLightGreen)
                    .padding(horizontal = 6.dp, vertical = 2.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(3.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(6.dp)
                            .clip(CircleShape)
                            .background(WhatsAppGreen)
                    )
                    Text(
                        text = "+WA",
                        color = WhatsAppDarkGreen,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }

        // Action Icons: Add Post, Search, Messenger / WhatsApp Chats
        Row(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Add Post Button
            Box(
                modifier = Modifier
                    .size(38.dp)
                    .clip(CircleShape)
                    .background(FacebookBackground)
                    .clickable { onCreatePostClick() }
                    .testTag("feed_create_post_icon"),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = "Create Post",
                    tint = TextPrimary,
                    modifier = Modifier.size(20.dp)
                )
            }

            // Search Button
            Box(
                modifier = Modifier
                    .size(38.dp)
                    .clip(CircleShape)
                    .background(FacebookBackground)
                    .clickable { onSearchClick() }
                    .testTag("feed_search_icon"),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Search,
                    contentDescription = "Search",
                    tint = TextPrimary,
                    modifier = Modifier.size(20.dp)
                )
            }

            // Messenger / WhatsApp Button
            Box(
                modifier = Modifier
                    .size(38.dp)
                    .clip(CircleShape)
                    .background(FacebookBackground)
                    .clickable { onMessengerClick() }
                    .testTag("feed_messenger_icon"),
                contentAlignment = Alignment.Center
            ) {
                if (unreadChatsCount > 0) {
                    BadgedBox(
                        badge = {
                            Badge(
                                containerColor = WhatsAppGreen,
                                contentColor = Color.White
                            ) {
                                Text(unreadChatsCount.toString(), fontSize = 10.sp)
                            }
                        }
                    ) {
                        Icon(
                            imageVector = Icons.Filled.ChatBubble,
                            contentDescription = "Chats",
                            tint = FacebookBlue,
                            modifier = Modifier.size(19.dp)
                        )
                    }
                } else {
                    Icon(
                        imageVector = Icons.Filled.ChatBubble,
                        contentDescription = "Chats",
                        tint = FacebookBlue,
                        modifier = Modifier.size(19.dp)
                    )
                }
            }
        }
    }
}

@Composable
fun CreatePostPromptCard(
    user: User?,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .testTag("create_post_prompt_card"),
        shape = RoundedCornerShape(0.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Column(modifier = Modifier.padding(horizontal = 14.dp, vertical = 10.dp)) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(10.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                AvatarView(
                    name = user?.name ?: "Alex Rivera",
                    avatarResName = user?.avatarResName ?: "ic_chat_logo",
                    avatarColorHex = 0xFF1877F2,
                    size = 40.dp
                )

                Box(
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(24.dp))
                        .border(1.dp, FacebookDivider, RoundedCornerShape(24.dp))
                        .background(FacebookBackground)
                        .padding(horizontal = 16.dp, vertical = 9.dp)
                ) {
                    Text(
                        text = "What's on your mind?",
                        color = TextSecondary,
                        fontSize = 14.sp
                    )
                }

                Icon(
                    imageVector = Icons.Outlined.PhotoLibrary,
                    contentDescription = "Add Photo",
                    tint = WhatsAppGreen,
                    modifier = Modifier.size(24.dp)
                )
            }

            HorizontalDivider(
                modifier = Modifier.padding(top = 10.dp, bottom = 6.dp),
                color = FacebookDivider.copy(alpha = 0.5f)
            )

            // Live, Photo, Feeling Quick Actions
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceAround
            ) {
                QuickActionChip(icon = Icons.Default.Videocam, text = "Live", tint = Color(0xFFE94057)) { onClick() }
                QuickActionChip(icon = Icons.Default.Photo, text = "Photo", tint = WhatsAppGreen) { onClick() }
                QuickActionChip(icon = Icons.Default.EmojiEmotions, text = "Feeling", tint = ReactionHahaYellow) { onClick() }
            }
        }
    }
}

@Composable
fun QuickActionChip(icon: androidx.compose.ui.graphics.vector.ImageVector, text: String, tint: Color, onClick: () -> Unit) {
    Row(
        modifier = Modifier
            .clip(RoundedCornerShape(8.dp))
            .clickable { onClick() }
            .padding(horizontal = 8.dp, vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        Icon(imageVector = icon, contentDescription = null, tint = tint, modifier = Modifier.size(18.dp))
        Text(text = text, fontSize = 12.5.sp, fontWeight = FontWeight.SemiBold, color = TextSecondary)
    }
}

@Composable
fun StoriesHorizontalTray(
    user: User?,
    statusUpdates: List<UserStatusUpdate>,
    onAddStory: () -> Unit,
    onViewStory: (UserStatusUpdate) -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(0.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        LazyRow(
            contentPadding = PaddingValues(horizontal = 12.dp, vertical = 10.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            // "Add to Story" Card
            item {
                Box(
                    modifier = Modifier
                        .width(105.dp)
                        .height(160.dp)
                        .clip(RoundedCornerShape(14.dp))
                        .background(FacebookBackground)
                        .border(1.dp, FacebookDivider, RoundedCornerShape(14.dp))
                        .clickable { onAddStory() }
                        .testTag("add_story_card"),
                    contentAlignment = Alignment.BottomCenter
                ) {
                    Column(
                        modifier = Modifier.fillMaxSize(),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        // Top user photo preview
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .weight(1f)
                                .background(PrimaryBlueSoft),
                            contentAlignment = Alignment.Center
                        ) {
                            AvatarView(
                                name = user?.name ?: "Alex",
                                avatarResName = user?.avatarResName ?: "ic_chat_logo",
                                avatarColorHex = 0xFF1877F2,
                                size = 48.dp
                            )
                        }

                        // Bottom Create Story label with + button
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(56.dp)
                                .background(MaterialTheme.colorScheme.surface),
                            contentAlignment = Alignment.Center
                        ) {
                            // Floating + badge
                            Box(
                                modifier = Modifier
                                    .offset(y = (-20).dp)
                                    .size(32.dp)
                                    .clip(CircleShape)
                                    .background(FacebookBlue)
                                    .border(2.dp, MaterialTheme.colorScheme.surface, CircleShape),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Add,
                                    contentDescription = "Add Story",
                                    tint = Color.White,
                                    modifier = Modifier.size(20.dp)
                                )
                            }

                            Text(
                                text = "Create Story",
                                fontSize = 11.5.sp,
                                fontWeight = FontWeight.Bold,
                                color = TextPrimary,
                                modifier = Modifier.padding(top = 14.dp)
                            )
                        }
                    }
                }
            }

            // Contact Stories
            items(statusUpdates.filter { !it.isSelf }, key = { it.id }) { status ->
                val storyGradient = remember(status.id) {
                    val idx = (status.stories.firstOrNull()?.mediaGradientIndex ?: 0) % postGradients.size
                    postGradients[idx]
                }

                Box(
                    modifier = Modifier
                        .width(105.dp)
                        .height(160.dp)
                        .clip(RoundedCornerShape(14.dp))
                        .background(storyGradient)
                        .clickable { onViewStory(status) }
                        .padding(8.dp)
                ) {
                    Column(
                        modifier = Modifier.fillMaxSize(),
                        verticalArrangement = Arrangement.SpaceBetween
                    ) {
                        // Top Avatar with WhatsApp Green / Facebook Blue Ring
                        Box(
                            modifier = Modifier
                                .size(36.dp)
                                .border(
                                    2.5.dp,
                                    if (status.isViewed) Color.White.copy(alpha = 0.5f) else WhatsAppGreen,
                                    CircleShape
                                )
                                .padding(2.dp)
                        ) {
                            AvatarView(
                                name = status.contactName,
                                avatarResName = status.avatarResName,
                                avatarColorHex = status.avatarColorHex,
                                size = 30.dp
                            )
                        }

                        // Caption Preview
                        Text(
                            text = status.stories.firstOrNull()?.text ?: status.contactName,
                            color = Color.White,
                            fontSize = 11.5.sp,
                            fontWeight = FontWeight.SemiBold,
                            maxLines = 3,
                            overflow = TextOverflow.Ellipsis,
                            modifier = Modifier
                                .background(Color.Black.copy(alpha = 0.35f), RoundedCornerShape(6.dp))
                                .padding(4.dp)
                        )

                        // Bottom Contact Name
                        Text(
                            text = status.contactName,
                            color = Color.White,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun FeedPostCard(
    post: FeedPost,
    isPickerOpen: Boolean,
    onReactionButtonClick: () -> Unit,
    onLongPressReaction: () -> Unit,
    onSelectReaction: (ReactionType) -> Unit,
    onCommentClick: () -> Unit,
    onShareClick: () -> Unit,
    onAuthorClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .testTag("feed_post_${post.id}"),
        shape = RoundedCornerShape(0.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Column(modifier = Modifier.fillMaxWidth()) {
            // Post Author Header
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 14.dp, vertical = 10.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(modifier = Modifier.clickable { onAuthorClick() }) {
                    AvatarView(
                        name = post.authorName,
                        avatarResName = post.authorAvatarResName,
                        avatarColorHex = post.authorAvatarColorHex,
                        size = 42.dp
                    )
                }

                Spacer(modifier = Modifier.width(10.dp))

                Column(modifier = Modifier.weight(1f)) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Text(
                            text = post.authorName,
                            fontWeight = FontWeight.Bold,
                            fontSize = 15.sp,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        if (post.isVerified) {
                            Icon(
                                imageVector = Icons.Default.CheckCircle,
                                contentDescription = "Verified",
                                tint = FacebookBlue,
                                modifier = Modifier.size(15.dp)
                            )
                        }
                    }

                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Text(
                            text = post.timeAgo,
                            fontSize = 12.sp,
                            color = TextSecondary
                        )
                        if (post.location != null) {
                            Text(text = "• 📍 ${post.location}", fontSize = 12.sp, color = TextSecondary)
                        }
                    }
                }

                IconButton(onClick = {}) {
                    Icon(
                        imageVector = Icons.Default.MoreHoriz,
                        contentDescription = "Options",
                        tint = TextSecondary
                    )
                }
            }

            // Post Content Text / Colored Gradient Card
            if (post.postGradientIndex >= 0 && post.postGradientIndex < postGradients.size) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .heightIn(min = 180.dp)
                        .background(postGradients[post.postGradientIndex])
                        .padding(24.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = post.contentText,
                        color = Color.White,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        lineHeight = 28.sp
                    )
                }
            } else {
                Text(
                    text = post.contentText,
                    fontSize = 15.sp,
                    color = MaterialTheme.colorScheme.onSurface,
                    lineHeight = 22.sp,
                    modifier = Modifier.padding(horizontal = 14.dp, vertical = 6.dp)
                )
            }

            // Reactions & Comments Count Bar
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 14.dp, vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                // Top Reactions Emojis + Count
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Row {
                        post.topReactions.forEach { r ->
                            Text(text = r.emoji, fontSize = 15.sp)
                        }
                    }
                    Text(
                        text = "${post.likesCount}",
                        fontSize = 13.sp,
                        color = TextSecondary,
                        fontWeight = FontWeight.Medium
                    )
                }

                // Comments & Shares Count
                Text(
                    text = "${post.commentsCount} comments • ${post.sharesCount} shares",
                    fontSize = 13.sp,
                    color = TextSecondary
                )
            }

            HorizontalDivider(
                modifier = Modifier.padding(horizontal = 12.dp),
                color = FacebookDivider.copy(alpha = 0.4f)
            )

            // Facebook 7-Reactions Floating Bar if open
            if (isPickerOpen) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 14.dp, vertical = 4.dp),
                    contentAlignment = Alignment.CenterStart
                ) {
                    Card(
                        shape = RoundedCornerShape(24.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                        modifier = Modifier.shadow(12.dp, RoundedCornerShape(24.dp))
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 6.dp),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            ReactionType.values().forEach { reaction ->
                                Box(
                                    modifier = Modifier
                                        .size(34.dp)
                                        .clip(CircleShape)
                                        .clickable { onSelectReaction(reaction) },
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(text = reaction.emoji, fontSize = 22.sp)
                                }
                            }
                        }
                    }
                }
            }

            // Action Buttons: Like, Comment, Share
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 6.dp, vertical = 2.dp),
                horizontalArrangement = Arrangement.SpaceAround
            ) {
                // Reaction Button
                Row(
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .clickable { onReactionButtonClick() }
                        .padding(horizontal = 12.dp, vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    val userReaction = post.userReaction
                    if (userReaction != null) {
                        Text(text = userReaction.emoji, fontSize = 18.sp)
                        Text(
                            text = userReaction.label,
                            fontWeight = FontWeight.Bold,
                            fontSize = 13.5.sp,
                            color = Color(userReaction.colorHex)
                        )
                    } else {
                        Icon(
                            imageVector = Icons.Outlined.ThumbUp,
                            contentDescription = "Like",
                            tint = TextSecondary,
                            modifier = Modifier.size(18.dp)
                        )
                        Text(
                            text = "Like",
                            fontWeight = FontWeight.SemiBold,
                            fontSize = 13.5.sp,
                            color = TextSecondary
                        )
                    }
                }

                // Comment Button
                Row(
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .clickable { onCommentClick() }
                        .padding(horizontal = 12.dp, vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Icon(
                        imageVector = Icons.Outlined.ChatBubbleOutline,
                        contentDescription = "Comment",
                        tint = TextSecondary,
                        modifier = Modifier.size(18.dp)
                    )
                    Text(
                        text = "Comment",
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 13.5.sp,
                        color = TextSecondary
                    )
                }

                // Share Button
                Row(
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .clickable { onShareClick() }
                        .padding(horizontal = 12.dp, vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Icon(
                        imageVector = Icons.Outlined.Share,
                        contentDescription = "Share",
                        tint = TextSecondary,
                        modifier = Modifier.size(18.dp)
                    )
                    Text(
                        text = "Share",
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 13.5.sp,
                        color = TextSecondary
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreatePostBottomSheet(
    user: User?,
    onDismiss: () -> Unit,
    onPostCreated: (String, Int, String?) -> Unit
) {
    var postText by remember { mutableStateOf("") }
    var selectedGradient by remember { mutableStateOf(-1) }
    var selectedFeeling by remember { mutableStateOf<String?>(null) }
    var selectedLocation by remember { mutableStateOf<String?>(null) }

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        containerColor = MaterialTheme.colorScheme.surface,
        shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 8.dp)
                .navigationBarsPadding()
        ) {
            // Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "Create Post",
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp
                )
                Button(
                    onClick = {
                        val finalLoc = selectedLocation ?: selectedFeeling?.let { "Feeling $it" }
                        onPostCreated(postText, selectedGradient, finalLoc)
                    },
                    enabled = postText.isNotBlank(),
                    colors = ButtonDefaults.buttonColors(containerColor = FacebookBlue),
                    shape = RoundedCornerShape(20.dp)
                ) {
                    Text("Post", color = Color.White, fontWeight = FontWeight.Bold)
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // User Info & Privacy
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                AvatarView(
                    name = user?.name ?: "Alex",
                    avatarResName = user?.avatarResName ?: "ic_chat_logo",
                    avatarColorHex = 0xFF1877F2,
                    size = 40.dp
                )

                Column {
                    Text(
                        text = user?.name ?: "Alex Rivera",
                        fontWeight = FontWeight.Bold,
                        fontSize = 15.sp
                    )
                    Row(
                        modifier = Modifier
                            .clip(RoundedCornerShape(6.dp))
                            .background(FacebookBackground)
                            .padding(horizontal = 6.dp, vertical = 2.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Icon(imageVector = Icons.Default.Public, contentDescription = null, modifier = Modifier.size(12.dp), tint = TextSecondary)
                        Text("Public", fontSize = 11.sp, color = TextSecondary, fontWeight = FontWeight.Medium)
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Post Text Area (Optionally Gradient Card)
            if (selectedGradient >= 0 && selectedGradient < postGradients.size) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(160.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(postGradients[selectedGradient])
                        .padding(16.dp),
                    contentAlignment = Alignment.Center
                ) {
                    TextField(
                        value = postText,
                        onValueChange = { postText = it },
                        placeholder = { Text("What's on your mind?", color = Color.White.copy(alpha = 0.8f), fontSize = 18.sp, fontWeight = FontWeight.Bold) },
                        colors = TextFieldDefaults.colors(
                            focusedContainerColor = Color.Transparent,
                            unfocusedContainerColor = Color.Transparent,
                            focusedTextColor = Color.White,
                            unfocusedTextColor = Color.White,
                            focusedIndicatorColor = Color.Transparent,
                            unfocusedIndicatorColor = Color.Transparent
                        ),
                        textStyle = MaterialTheme.typography.bodyLarge.copy(fontSize = 18.sp, fontWeight = FontWeight.Bold, color = Color.White)
                    )
                }
            } else {
                TextField(
                    value = postText,
                    onValueChange = { postText = it },
                    placeholder = { Text("What's on your mind?", color = TextSecondary, fontSize = 16.sp) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .heightIn(min = 120.dp),
                    colors = TextFieldDefaults.colors(
                        focusedContainerColor = Color.Transparent,
                        unfocusedContainerColor = Color.Transparent,
                        focusedIndicatorColor = Color.Transparent,
                        unfocusedIndicatorColor = Color.Transparent
                    )
                )
            }

            // Facebook Background Gradient Picker
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 10.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                // Default No Gradient
                Box(
                    modifier = Modifier
                        .size(30.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .background(Color.LightGray)
                        .border(if (selectedGradient == -1) 2.dp else 0.dp, FacebookBlue, RoundedCornerShape(8.dp))
                        .clickable { selectedGradient = -1 },
                    contentAlignment = Alignment.Center
                ) {
                    Text("Aa", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Color.Black)
                }

                postGradients.forEachIndexed { index, gradient ->
                    Box(
                        modifier = Modifier
                            .size(30.dp)
                            .clip(RoundedCornerShape(8.dp))
                            .background(gradient)
                            .border(if (selectedGradient == index) 2.dp else 0.dp, FacebookBlue, RoundedCornerShape(8.dp))
                            .clickable { selectedGradient = index }
                    )
                }
            }

            // Additional Action Buttons: Photo, Feeling, Location
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp))
                    .background(FacebookBackground)
                    .padding(horizontal = 12.dp, vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text("Add to your post", fontSize = 13.sp, fontWeight = FontWeight.SemiBold)
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    IconButton(onClick = { selectedGradient = 0 }) {
                        Icon(imageVector = Icons.Default.PhotoLibrary, contentDescription = "Photo", tint = WhatsAppGreen)
                    }
                    IconButton(onClick = { selectedFeeling = "Happy 😊" }) {
                        Icon(imageVector = Icons.Default.EmojiEmotions, contentDescription = "Feeling", tint = ReactionHahaYellow)
                    }
                    IconButton(onClick = { selectedLocation = "San Francisco, CA" }) {
                        Icon(imageVector = Icons.Default.LocationOn, contentDescription = "Location", tint = Color(0xFFFA3E3E))
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CommentsBottomSheet(
    post: FeedPost,
    onDismiss: () -> Unit,
    onAddComment: (String) -> Unit,
    onLikeComment: (String) -> Unit
) {
    var commentText by remember { mutableStateOf("") }

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        containerColor = MaterialTheme.colorScheme.surface,
        shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 8.dp)
                .navigationBarsPadding()
        ) {
            Text(
                text = "Comments (${post.comments.size})",
                fontWeight = FontWeight.Bold,
                fontSize = 18.sp,
                modifier = Modifier.padding(bottom = 12.dp)
            )

            // Comments List
            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(max = 300.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                items(post.comments, key = { it.id }) { comment ->
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        AvatarView(
                            name = comment.authorName,
                            avatarResName = comment.authorAvatarResName,
                            avatarColorHex = comment.authorAvatarColorHex,
                            size = 36.dp
                        )

                        Column(modifier = Modifier.weight(1f)) {
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(16.dp))
                                    .background(FacebookBackground)
                                    .padding(horizontal = 12.dp, vertical = 8.dp)
                            ) {
                                Column {
                                    Text(
                                        text = comment.authorName,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 13.5.sp
                                    )
                                    Text(
                                        text = comment.text,
                                        fontSize = 13.5.sp,
                                        color = MaterialTheme.colorScheme.onSurface
                                    )
                                }
                            }

                            Row(
                                modifier = Modifier.padding(start = 6.dp, top = 3.dp),
                                horizontalArrangement = Arrangement.spacedBy(12.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = comment.timeAgo,
                                    fontSize = 11.5.sp,
                                    color = TextSecondary
                                )
                                Text(
                                    text = if (comment.isLiked) "Liked" else "Like",
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = if (comment.isLiked) FacebookBlue else TextSecondary,
                                    modifier = Modifier.clickable { onLikeComment(comment.id) }
                                )
                                if (comment.likesCount > 0) {
                                    Text(
                                        text = "👍 ${comment.likesCount}",
                                        fontSize = 11.5.sp,
                                        color = TextSecondary
                                    )
                                }
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Comment Input
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                TextField(
                    value = commentText,
                    onValueChange = { commentText = it },
                    placeholder = { Text("Write a comment...", fontSize = 14.sp) },
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(24.dp)),
                    colors = TextFieldDefaults.colors(
                        focusedContainerColor = FacebookBackground,
                        unfocusedContainerColor = FacebookBackground,
                        focusedIndicatorColor = Color.Transparent,
                        unfocusedIndicatorColor = Color.Transparent
                    )
                )

                IconButton(
                    onClick = {
                        onAddComment(commentText)
                        commentText = ""
                    },
                    enabled = commentText.isNotBlank()
                ) {
                    Icon(
                        imageVector = Icons.Default.Send,
                        contentDescription = "Send Comment",
                        tint = if (commentText.isNotBlank()) FacebookBlue else TextTertiary
                    )
                }
            }
        }
    }
}

@Composable
fun SharePostDialog(
    post: FeedPost,
    contacts: List<Contact>,
    onDismiss: () -> Unit,
    onShareToWhatsApp: (Contact) -> Unit,
    onShareToFeed: () -> Unit,
    onCopyLink: () -> Unit
) {
    Dialog(onDismissRequest = onDismiss) {
        Card(
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            modifier = Modifier.padding(16.dp)
        ) {
            Column(
                modifier = Modifier.padding(20.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                Text(
                    text = "Share Post",
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp
                )

                // Share to WhatsApp Contacts
                Text(
                    text = "Send via WhatsApp",
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 13.sp,
                    color = WhatsAppDarkGreen
                )

                LazyRow(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    items(contacts.take(5), key = { it.id }) { contact ->
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            modifier = Modifier
                                .clickable { onShareToWhatsApp(contact) }
                                .padding(4.dp)
                        ) {
                            AvatarView(
                                name = contact.name,
                                avatarResName = contact.avatarResName,
                                avatarColorHex = contact.avatarColorHex,
                                size = 44.dp
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = contact.name.split(" ").first(),
                                fontSize = 11.5.sp,
                                fontWeight = FontWeight.Medium
                            )
                        }
                    }
                }

                HorizontalDivider(color = FacebookDivider.copy(alpha = 0.5f))

                // Share to News Feed
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { onShareToFeed() }
                        .padding(vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Icon(imageVector = Icons.Default.DynamicFeed, contentDescription = null, tint = FacebookBlue)
                    Text("Share now to Feed", fontWeight = FontWeight.SemiBold, fontSize = 14.5.sp)
                }

                // Copy Link
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { onCopyLink() }
                        .padding(vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Icon(imageVector = Icons.Default.Link, contentDescription = null, tint = TextSecondary)
                    Text("Copy link to post", fontWeight = FontWeight.SemiBold, fontSize = 14.5.sp)
                }
            }
        }
    }
}

@Composable
fun StatusViewerDialog(
    statusUpdate: UserStatusUpdate,
    onDismiss: () -> Unit,
    onReply: (String) -> Unit
) {
    var replyText by remember { mutableStateOf("") }
    val story = statusUpdate.stories.firstOrNull() ?: StatusStory(id = "s_0", text = "Status update")
    val gradient = remember(statusUpdate.id) {
        val idx = story.mediaGradientIndex % postGradients.size
        postGradients[idx]
    }

    Dialog(onDismissRequest = onDismiss) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black)
        ) {
            // Main Story Background
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(gradient)
                    .padding(24.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = story.text,
                    color = Color.White,
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    lineHeight = 32.sp
                )
            }

            // Top Status Header & Progress Bar
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .statusBarsPadding()
                    .padding(16.dp)
            ) {
                // Timer Progress Bar
                LinearProgressIndicator(
                    progress = { 1f },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(3.dp)
                        .clip(RoundedCornerShape(2.dp)),
                    color = Color.White,
                    trackColor = Color.White.copy(alpha = 0.3f)
                )

                Spacer(modifier = Modifier.height(10.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        AvatarView(
                            name = statusUpdate.contactName,
                            avatarResName = statusUpdate.avatarResName,
                            avatarColorHex = statusUpdate.avatarColorHex,
                            size = 36.dp
                        )
                        Column {
                            Text(
                                text = statusUpdate.contactName,
                                color = Color.White,
                                fontWeight = FontWeight.Bold,
                                fontSize = 14.5.sp
                            )
                            Text(
                                text = statusUpdate.lastUpdatedText,
                                color = Color.White.copy(alpha = 0.8f),
                                fontSize = 11.5.sp
                            )
                        }
                    }

                    IconButton(onClick = onDismiss) {
                        Icon(imageVector = Icons.Default.Close, contentDescription = "Close", tint = Color.White)
                    }
                }
            }

            // Bottom Reply via WhatsApp
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .align(Alignment.BottomCenter)
                    .navigationBarsPadding()
                    .padding(16.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                TextField(
                    value = replyText,
                    onValueChange = { replyText = it },
                    placeholder = { Text("Reply to ${statusUpdate.contactName}...", color = Color.White.copy(alpha = 0.7f), fontSize = 14.sp) },
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(24.dp)),
                    colors = TextFieldDefaults.colors(
                        focusedContainerColor = Color.Black.copy(alpha = 0.5f),
                        unfocusedContainerColor = Color.Black.copy(alpha = 0.5f),
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White,
                        focusedIndicatorColor = Color.Transparent,
                        unfocusedIndicatorColor = Color.Transparent
                    )
                )

                Box(
                    modifier = Modifier
                        .size(44.dp)
                        .clip(CircleShape)
                        .background(WhatsAppGreen)
                        .clickable {
                            if (replyText.isNotBlank()) {
                                onReply(replyText)
                            }
                        },
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Send,
                        contentDescription = "Send Reply",
                        tint = Color.White,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }
        }
    }
}
