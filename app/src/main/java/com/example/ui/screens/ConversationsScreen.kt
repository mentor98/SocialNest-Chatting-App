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
import com.example.model.Contact
import com.example.model.Conversation
import com.example.ui.components.AvatarView
import com.example.ui.components.BottomNavigationDock
import com.example.ui.components.SearchBar
import com.example.ui.theme.*
import com.example.viewmodel.ChatViewModel
import com.example.viewmodel.NavTab
import com.example.viewmodel.Screen
import java.text.SimpleDateFormat
import java.util.*

enum class ChatFilter {
    ALL,
    UNREAD,
    GROUPS,
    FAVORITES
}

data class StoryItem(
    val id: String,
    val contactName: String,
    val avatarResName: String = "",
    val avatarColorHex: Long = 0xFF568CF5,
    val isOnline: Boolean = true,
    val caption: String = "",
    val timeAgo: String = "Just now",
    val isSelf: Boolean = false
)

@Composable
fun ConversationsScreen(
    viewModel: ChatViewModel,
    modifier: Modifier = Modifier
) {
    val uiState by viewModel.uiState.collectAsState()
    var activeFilter by remember { mutableStateOf(ChatFilter.ALL) }
    var selectedStory by remember { mutableStateOf<StoryItem?>(null) }
    var showNewChatSheet by remember { mutableStateOf(false) }
    var showCreateGroupDialog by remember { mutableStateOf(false) }
    var showStoryPostDialog by remember { mutableStateOf(false) }

    val totalUnread = remember(uiState.conversations) {
        uiState.conversations.sumOf { it.unreadCount }
    }

    // Filtered conversations
    val filteredConversations = remember(uiState.conversations, activeFilter, uiState.searchQuery) {
        val list = uiState.conversations.filter { conv ->
            if (uiState.searchQuery.isBlank()) true
            else conv.contactName.contains(uiState.searchQuery, ignoreCase = true) ||
                    conv.lastMessageText.contains(uiState.searchQuery, ignoreCase = true)
        }

        when (activeFilter) {
            ChatFilter.ALL -> list
            ChatFilter.UNREAD -> list.filter { it.unreadCount > 0 }
            ChatFilter.GROUPS -> list.filter { it.contactName.contains("Group", ignoreCase = true) || it.contactName.contains("Squad", ignoreCase = true) || it.contactName.contains("Club", ignoreCase = true) }
            ChatFilter.FAVORITES -> list.filter { it.isPinned }
        }
    }

    val storiesList = remember(uiState.contacts) {
        listOf(
            StoryItem(
                id = "story_me",
                contactName = "Your Story",
                avatarResName = "ic_chat_logo",
                avatarColorHex = 0xFF568CF5,
                isOnline = true,
                caption = "Working on the Editorial design system 📱✨",
                timeAgo = "10m ago",
                isSelf = true
            ),
            StoryItem(
                id = "story_martin",
                contactName = "Martin",
                avatarResName = "avatar_martin",
                avatarColorHex = 0xFF568CF5,
                isOnline = true,
                caption = "Zelda OLED Switch session with coffee ☕🎮",
                timeAgo = "15m ago"
            ),
            StoryItem(
                id = "story_merry",
                contactName = "Merry",
                avatarResName = "avatar_merry",
                avatarColorHex = 0xFFFF86A8,
                isOnline = true,
                caption = "New color palette drop! Soft lavender & royal blue vibes 🎨💜",
                timeAgo = "45m ago"
            ),
            StoryItem(
                id = "story_bella",
                contactName = "Bella",
                avatarResName = "",
                avatarColorHex = 0xFFFB7185,
                isOnline = true,
                caption = "Designing wireframes for the new mobile chat release 📐🚀",
                timeAgo = "1h ago"
            ),
            StoryItem(
                id = "story_david",
                contactName = "David",
                avatarResName = "",
                avatarColorHex = 0xFF38BDF8,
                isOnline = true,
                caption = "Coffee tasting workshop downtown ☕☕",
                timeAgo = "2h ago"
            ),
            StoryItem(
                id = "story_sophia",
                contactName = "Sophia",
                avatarResName = "",
                avatarColorHex = 0xFFF59E0B,
                isOnline = true,
                caption = "Sunset at the rooftop studio 🌅📸",
                timeAgo = "3h ago"
            )
        )
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
        ) {
            // Top Bar: User Profile, Title "Chats", Search & Add Action
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 10.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                // User Profile avatar button
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .clip(CircleShape)
                        .clickable { viewModel.navigateTo(Screen.UserProfile) }
                        .testTag("conversations_profile_button"),
                    contentAlignment = Alignment.Center
                ) {
                    AvatarView(
                        name = uiState.currentUser?.name ?: "Alex",
                        avatarResName = "ic_chat_logo",
                        avatarColorHex = 0xFF568CF5,
                        size = 38.dp,
                        isOnline = true,
                        showOnlineIndicator = true
                    )
                }

                // Center Title "Chats" with Unread Badge
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        text = "Chats",
                        style = MaterialTheme.typography.titleLarge.copy(
                            fontWeight = FontWeight.Bold,
                            fontSize = 22.sp,
                            letterSpacing = (-0.3).sp
                        ),
                        color = MaterialTheme.colorScheme.onSurface
                    )

                    if (totalUnread > 0) {
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(12.dp))
                                .background(AccentPink)
                                .padding(horizontal = 8.dp, vertical = 2.dp)
                        ) {
                            Text(
                                text = "$totalUnread new",
                                color = Color.White,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }

                // Action Icons (Search + New Chat Menu)
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(38.dp)
                            .clip(CircleShape)
                            .background(MaterialTheme.colorScheme.surface)
                            .clickable { viewModel.navigateTo(Screen.Search) }
                            .testTag("conversations_search_button"),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Search,
                            contentDescription = "Search",
                            tint = MaterialTheme.colorScheme.onSurface,
                            modifier = Modifier.size(20.dp)
                        )
                    }

                    Box(
                        modifier = Modifier
                            .size(38.dp)
                            .clip(CircleShape)
                            .background(PrimaryBlue)
                            .clickable { showNewChatSheet = true }
                            .testTag("conversations_new_chat_button"),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Add,
                            contentDescription = "New Chat",
                            tint = Color.White,
                            modifier = Modifier.size(22.dp)
                        )
                    }
                }
            }

            // Quick Search Pill
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 4.dp)
            ) {
                SearchBar(
                    query = uiState.searchQuery,
                    onQueryChange = { viewModel.updateSearchQuery(it) },
                    placeholder = "Search chats and messages...",
                    onMicClick = {
                        viewModel.showToast("Voice search activated")
                    }
                )
            }

            // Active Now / Stories Tray (Classic Social Chat Feature)
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 10.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp, vertical = 4.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = "Active Now",
                        style = MaterialTheme.typography.titleSmall.copy(
                            fontWeight = FontWeight.SemiBold,
                            fontSize = 13.sp
                        ),
                        color = TextSecondary
                    )

                    Text(
                        text = "See All",
                        style = MaterialTheme.typography.bodySmall.copy(
                            fontWeight = FontWeight.Medium,
                            fontSize = 12.sp
                        ),
                        color = PrimaryBlue,
                        modifier = Modifier.clickable { viewModel.navigateTo(Screen.Contacts) }
                    )
                }

                LazyRow(
                    contentPadding = PaddingValues(horizontal = 16.dp, vertical = 6.dp),
                    horizontalArrangement = Arrangement.spacedBy(14.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    items(storiesList, key = { it.id }) { story ->
                        StoryAvatarItem(
                            story = story,
                            onClick = {
                                if (story.isSelf) {
                                    showStoryPostDialog = true
                                } else {
                                    selectedStory = story
                                }
                            }
                        )
                    }
                }
            }

            // Filter Chips Row (All, Unread, Groups, Favorites)
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 8.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                ChatFilterChip(
                    label = "All",
                    count = uiState.conversations.size,
                    isSelected = activeFilter == ChatFilter.ALL,
                    onClick = { activeFilter = ChatFilter.ALL }
                )

                ChatFilterChip(
                    label = "Unread",
                    count = uiState.conversations.count { it.unreadCount > 0 },
                    isSelected = activeFilter == ChatFilter.UNREAD,
                    onClick = { activeFilter = ChatFilter.UNREAD }
                )

                ChatFilterChip(
                    label = "Groups",
                    count = uiState.conversations.count { it.contactName.contains("Group", ignoreCase = true) || it.contactName.contains("Squad", ignoreCase = true) || it.contactName.contains("Club", ignoreCase = true) },
                    isSelected = activeFilter == ChatFilter.GROUPS,
                    onClick = { activeFilter = ChatFilter.GROUPS }
                )

                ChatFilterChip(
                    label = "Pinned",
                    count = uiState.conversations.count { it.isPinned },
                    isSelected = activeFilter == ChatFilter.FAVORITES,
                    onClick = { activeFilter = ChatFilter.FAVORITES }
                )
            }

            // Main Conversations List
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
            ) {
                if (filteredConversations.isEmpty()) {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Outlined.ChatBubbleOutline,
                                contentDescription = null,
                                tint = TextTertiary,
                                modifier = Modifier.size(48.dp)
                            )
                            Text(
                                text = if (activeFilter == ChatFilter.UNREAD) "No unread chats" else "No conversations found",
                                style = MaterialTheme.typography.bodyMedium,
                                color = TextTertiary
                            )
                            Button(
                                onClick = { showNewChatSheet = true },
                                colors = ButtonDefaults.buttonColors(containerColor = PrimaryBlue),
                                shape = RoundedCornerShape(16.dp)
                            ) {
                                Text("Start a Chat")
                            }
                        }
                    }
                } else {
                    LazyColumn(
                        contentPadding = PaddingValues(start = 16.dp, end = 16.dp, top = 4.dp, bottom = 85.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp),
                        modifier = Modifier
                            .fillMaxSize()
                            .testTag("conversations_lazy_list")
                    ) {
                        items(
                            items = filteredConversations,
                            key = { it.id }
                        ) { conversation ->
                            ConversationRowItem(
                                conversation = conversation,
                                onClick = {
                                    viewModel.navigateTo(Screen.Chat(conversation.id, conversation.contactId))
                                },
                                onAvatarClick = {
                                    viewModel.navigateTo(Screen.ContactProfile(conversation.contactId))
                                }
                            )
                        }
                    }
                }
            }
        }

        // Floating Bottom Navigation Bar
        BottomNavigationDock(
            activeTab = uiState.activeNavTab,
            onTabSelected = { tab ->
                viewModel.setActiveNavTab(tab)
            },
            unreadNotificationsCount = totalUnread,
            modifier = Modifier.align(Alignment.BottomCenter)
        )

        // Story Viewer Dialog
        selectedStory?.let { story ->
            StoryViewerDialog(
                story = story,
                onDismiss = { selectedStory = null },
                onReply = {
                    val targetContact = uiState.contacts.firstOrNull { it.name.equals(story.contactName, ignoreCase = true) }
                    selectedStory = null
                    if (targetContact != null) {
                        viewModel.openChatWithContact(targetContact)
                    } else {
                        viewModel.showToast("Replying to ${story.contactName}'s story ✨")
                    }
                }
            )
        }

        // Add Story Dialog
        if (showStoryPostDialog) {
            PostStoryDialog(
                onDismiss = { showStoryPostDialog = false },
                onPost = { text ->
                    showStoryPostDialog = false
                    viewModel.showToast("Story shared: $text 🎉")
                }
            )
        }

        // New Chat Sheet
        if (showNewChatSheet) {
            NewChatBottomSheet(
                contacts = uiState.contacts,
                onDismiss = { showNewChatSheet = false },
                onSelectContact = { contact ->
                    showNewChatSheet = false
                    viewModel.openChatWithContact(contact)
                },
                onCreateGroup = {
                    showNewChatSheet = false
                    showCreateGroupDialog = true
                }
            )
        }

        // Create Group Dialog
        if (showCreateGroupDialog) {
            CreateGroupDialog(
                contacts = uiState.contacts,
                onDismiss = { showCreateGroupDialog = false },
                onCreate = { groupName, selectedMembers ->
                    showCreateGroupDialog = false
                    viewModel.showToast("Created group '$groupName' with ${selectedMembers.size} members 🎉")
                    val conv = uiState.conversations.firstOrNull()
                    if (conv != null) {
                        viewModel.navigateTo(Screen.Chat(conv.id, conv.contactId))
                    }
                }
            )
        }
    }
}

@Composable
fun StoryAvatarItem(
    story: StoryItem,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(4.dp),
        modifier = modifier
            .width(62.dp)
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null
            ) { onClick() }
    ) {
        Box(
            modifier = Modifier.size(54.dp),
            contentAlignment = Alignment.Center
        ) {
            // Story Ring Border
            val borderBrush = if (story.isSelf) {
                Brush.sweepGradient(listOf(PrimaryBlue, AccentPink, PrimaryBlue))
            } else if (story.isOnline) {
                Brush.sweepGradient(listOf(Color(0xFF4ADE80), PrimaryBlue, Color(0xFF4ADE80)))
            } else {
                Brush.sweepGradient(listOf(TextTertiary.copy(alpha = 0.5f), TextTertiary.copy(alpha = 0.5f)))
            }

            Box(
                modifier = Modifier
                    .size(54.dp)
                    .clip(CircleShape)
                    .border(2.2.dp, borderBrush, CircleShape)
                    .padding(3.dp),
                contentAlignment = Alignment.Center
            ) {
                AvatarView(
                    name = story.contactName,
                    avatarResName = story.avatarResName,
                    avatarColorHex = story.avatarColorHex,
                    size = 46.dp,
                    isOnline = false,
                    showOnlineIndicator = false
                )
            }

            // Self Add Badge
            if (story.isSelf) {
                Box(
                    modifier = Modifier
                        .align(Alignment.BottomEnd)
                        .size(18.dp)
                        .clip(CircleShape)
                        .background(PrimaryBlue)
                        .border(1.5.dp, MaterialTheme.colorScheme.surface, CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = "Add Story",
                        tint = Color.White,
                        modifier = Modifier.size(12.dp)
                    )
                }
            } else if (story.isOnline) {
                Box(
                    modifier = Modifier
                        .align(Alignment.BottomEnd)
                        .size(13.dp)
                        .clip(CircleShape)
                        .background(AccentGreen)
                        .border(1.5.dp, MaterialTheme.colorScheme.surface, CircleShape)
                )
            }
        }

        Text(
            text = story.contactName,
            style = MaterialTheme.typography.bodySmall.copy(
                fontSize = 11.sp,
                fontWeight = FontWeight.Medium
            ),
            color = MaterialTheme.colorScheme.onSurface,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
    }
}

@Composable
fun ChatFilterChip(
    label: String,
    count: Int,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    val bgColor = if (isSelected) PrimaryBlue else MaterialTheme.colorScheme.surface
    val contentColor = if (isSelected) Color.White else TextSecondary

    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(20.dp))
            .background(bgColor)
            .clickable { onClick() }
            .padding(horizontal = 14.dp, vertical = 6.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(5.dp)
        ) {
            Text(
                text = label,
                fontSize = 12.5.sp,
                fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Medium,
                color = contentColor
            )

            if (count > 0 && !isSelected) {
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(10.dp))
                        .background(PrimaryBlueContainer)
                        .padding(horizontal = 6.dp, vertical = 1.dp)
                ) {
                    Text(
                        text = "$count",
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        color = PrimaryBlue
                    )
                }
            }
        }
    }
}

@Composable
fun ConversationRowItem(
    conversation: Conversation,
    onClick: () -> Unit,
    onAvatarClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val formattedTime = remember(conversation.lastMessageTime) {
        val sdf = SimpleDateFormat("hh:mm a", Locale.getDefault())
        sdf.format(Date(conversation.lastMessageTime))
    }

    val isGroup = conversation.contactName.contains("Group", ignoreCase = true) ||
            conversation.contactName.contains("Squad", ignoreCase = true) ||
            conversation.contactName.contains("Club", ignoreCase = true)

    Box(
        modifier = modifier
            .fillMaxWidth()
            .shadow(
                elevation = 3.dp,
                shape = RoundedCornerShape(20.dp),
                spotColor = Color(0x105078C8),
                ambientColor = Color(0x06000000)
            )
            .clip(RoundedCornerShape(20.dp))
            .background(MaterialTheme.colorScheme.surface)
            .clickable { onClick() }
            .padding(horizontal = 14.dp, vertical = 12.dp)
            .testTag("conversation_item_${conversation.contactName.lowercase().take(6)}")
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth()
        ) {
            // Avatar
            AvatarView(
                name = conversation.contactName,
                avatarResName = conversation.contactAvatarResName,
                avatarColorHex = conversation.contactAvatarColorHex,
                size = 50.dp,
                isOnline = conversation.isOnline,
                showOnlineIndicator = true,
                modifier = Modifier
                    .clickable(
                        interactionSource = remember { MutableInteractionSource() },
                        indication = null
                    ) { onAvatarClick() }
            )

            Spacer(modifier = Modifier.width(12.dp))

            // Middle Column: Title & Last Message
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(3.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Text(
                        text = conversation.contactName,
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = if (conversation.unreadCount > 0) FontWeight.Bold else FontWeight.SemiBold,
                            fontSize = 15.5.sp
                        ),
                        color = MaterialTheme.colorScheme.onSurface,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        modifier = Modifier.weight(1f, fill = false)
                    )

                    if (isGroup) {
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(6.dp))
                                .background(PrimaryBlueContainer)
                                .padding(horizontal = 5.dp, vertical = 1.dp)
                        ) {
                            Text(
                                text = "Group",
                                color = PrimaryBlue,
                                fontSize = 9.5.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }

                    if (conversation.isPinned) {
                        Icon(
                            imageVector = Icons.Default.PushPin,
                            contentDescription = "Pinned",
                            tint = PrimaryBlue,
                            modifier = Modifier.size(13.dp)
                        )
                    }
                }

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    if (conversation.lastMessageText.startsWith("✓")) {
                        Icon(
                            imageVector = Icons.Default.DoneAll,
                            contentDescription = null,
                            tint = PrimaryBlue,
                            modifier = Modifier.size(14.dp)
                        )
                    }

                    Text(
                        text = conversation.lastMessageText,
                        style = MaterialTheme.typography.bodyMedium.copy(
                            fontSize = 13.5.sp,
                            fontWeight = if (conversation.unreadCount > 0) FontWeight.Medium else FontWeight.Normal
                        ),
                        color = if (conversation.unreadCount > 0) MaterialTheme.colorScheme.onSurface else TextTertiary,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }

            Spacer(modifier = Modifier.width(8.dp))

            // Right Column: Time & Unread Badge
            Column(
                horizontalAlignment = Alignment.End,
                verticalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                Text(
                    text = formattedTime,
                    style = MaterialTheme.typography.bodySmall.copy(
                        fontSize = 11.sp,
                        fontWeight = if (conversation.unreadCount > 0) FontWeight.SemiBold else FontWeight.Normal
                    ),
                    color = if (conversation.unreadCount > 0) PrimaryBlue else TextTertiary
                )

                if (conversation.unreadCount > 0) {
                    Box(
                        modifier = Modifier
                            .clip(CircleShape)
                            .background(AccentPink)
                            .padding(horizontal = 7.dp, vertical = 2.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "${conversation.unreadCount}",
                            color = Color.White,
                            fontSize = 10.5.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun StoryViewerDialog(
    story: StoryItem,
    onDismiss: () -> Unit,
    onReply: () -> Unit
) {
    Dialog(onDismissRequest = onDismiss) {
        Card(
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
                .shadow(20.dp, RoundedCornerShape(24.dp))
        ) {
            Column(
                modifier = Modifier.padding(20.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Header
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
                            name = story.contactName,
                            avatarResName = story.avatarResName,
                            avatarColorHex = story.avatarColorHex,
                            size = 42.dp,
                            isOnline = story.isOnline,
                            showOnlineIndicator = true
                        )
                        Column {
                            Text(
                                text = story.contactName,
                                fontWeight = FontWeight.Bold,
                                fontSize = 15.sp,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Text(
                                text = story.timeAgo,
                                fontSize = 11.sp,
                                color = TextTertiary
                            )
                        }
                    }

                    IconButton(onClick = onDismiss) {
                        Icon(imageVector = Icons.Default.Close, contentDescription = "Close")
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Story Content Card
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(180.dp)
                        .clip(RoundedCornerShape(18.dp))
                        .background(
                            Brush.linearGradient(
                                listOf(PrimaryBlue, Color(0xFFA88BFF), AccentPink)
                            )
                        )
                        .padding(20.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = story.caption,
                        color = Color.White,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Medium,
                        lineHeight = 24.sp
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Quick Reply Action
                Button(
                    onClick = onReply,
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(containerColor = PrimaryBlue),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Icon(imageVector = Icons.Outlined.ChatBubbleOutline, contentDescription = null, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Reply to ${story.contactName}")
                }
            }
        }
    }
}

@Composable
fun PostStoryDialog(
    onDismiss: () -> Unit,
    onPost: (String) -> Unit
) {
    var storyText by remember { mutableStateOf("") }

    Dialog(onDismissRequest = onDismiss) {
        Card(
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            modifier = Modifier.fillMaxWidth().padding(16.dp)
        ) {
            Column(
                modifier = Modifier.padding(20.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                Text(
                    text = "Share a Moment",
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp
                )

                OutlinedTextField(
                    value = storyText,
                    onValueChange = { storyText = it },
                    placeholder = { Text("What's happening? 📸✨") },
                    modifier = Modifier.fillMaxWidth(),
                    minLines = 3,
                    shape = RoundedCornerShape(16.dp)
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    TextButton(onClick = onDismiss) {
                        Text("Cancel")
                    }
                    Button(
                        onClick = {
                            if (storyText.isNotBlank()) onPost(storyText.trim())
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = PrimaryBlue),
                        shape = RoundedCornerShape(14.dp)
                    ) {
                        Text("Share")
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NewChatBottomSheet(
    contacts: List<Contact>,
    onDismiss: () -> Unit,
    onSelectContact: (Contact) -> Unit,
    onCreateGroup: () -> Unit
) {
    ModalBottomSheet(
        onDismissRequest = onDismiss,
        containerColor = MaterialTheme.colorScheme.surface,
        shape = RoundedCornerShape(topStart = 28.dp, topEnd = 28.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp, vertical = 8.dp)
                .navigationBarsPadding()
        ) {
            Text(
                text = "New Message",
                style = MaterialTheme.typography.titleLarge.copy(
                    fontWeight = FontWeight.Bold,
                    fontSize = 20.sp
                ),
                modifier = Modifier.padding(bottom = 12.dp)
            )

            // Create Group Item
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(16.dp))
                    .background(PrimaryBlueContainer)
                    .clickable { onCreateGroup() }
                    .padding(14.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .clip(CircleShape)
                        .background(PrimaryBlue),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.GroupAdd,
                        contentDescription = "New Group",
                        tint = Color.White,
                        modifier = Modifier.size(22.dp)
                    )
                }

                Column {
                    Text(
                        text = "Create New Group",
                        fontWeight = FontWeight.Bold,
                        fontSize = 15.sp,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = "Chat with multiple friends at once",
                        fontSize = 12.sp,
                        color = TextSecondary
                    )
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            Text(
                text = "Direct Message",
                style = MaterialTheme.typography.titleSmall.copy(
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 13.sp
                ),
                color = TextSecondary,
                modifier = Modifier.padding(vertical = 4.dp)
            )

            LazyColumn(
                modifier = Modifier.heightIn(max = 320.dp),
                verticalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                items(contacts, key = { it.id }) { contact ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(14.dp))
                            .clickable { onSelectContact(contact) }
                            .padding(vertical = 8.dp, horizontal = 4.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        AvatarView(
                            name = contact.name,
                            avatarResName = contact.avatarResName,
                            avatarColorHex = contact.avatarColorHex,
                            size = 42.dp,
                            isOnline = contact.isOnline,
                            showOnlineIndicator = true
                        )

                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = contact.name,
                                fontWeight = FontWeight.SemiBold,
                                fontSize = 15.sp
                            )
                            Text(
                                text = contact.statusMessage,
                                fontSize = 12.sp,
                                color = TextTertiary,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                        }

                        Icon(
                            imageVector = Icons.Default.Chat,
                            contentDescription = "Chat",
                            tint = PrimaryBlue,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun CreateGroupDialog(
    contacts: List<Contact>,
    onDismiss: () -> Unit,
    onCreate: (String, List<String>) -> Unit
) {
    var groupName by remember { mutableStateOf("") }
    val selectedContactIds = remember { mutableStateListOf<String>() }

    Dialog(onDismissRequest = onDismiss) {
        Card(
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            modifier = Modifier.fillMaxWidth().padding(16.dp)
        ) {
            Column(
                modifier = Modifier.padding(20.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Text(
                    text = "New Group Chat",
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp
                )

                OutlinedTextField(
                    value = groupName,
                    onValueChange = { groupName = it },
                    placeholder = { Text("Group Name (e.g. Gamers Squad 🎮)") },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(14.dp)
                )

                Text(
                    text = "Select Members (${selectedContactIds.size})",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = TextSecondary
                )

                LazyColumn(
                    modifier = Modifier.heightIn(max = 200.dp),
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    items(contacts) { contact ->
                        val isSelected = selectedContactIds.contains(contact.id)
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(12.dp))
                                .clickable {
                                    if (isSelected) selectedContactIds.remove(contact.id)
                                    else selectedContactIds.add(contact.id)
                                }
                                .padding(vertical = 6.dp, horizontal = 4.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            Checkbox(
                                checked = isSelected,
                                onCheckedChange = { checked ->
                                    if (checked) selectedContactIds.add(contact.id)
                                    else selectedContactIds.remove(contact.id)
                                }
                            )

                            AvatarView(
                                name = contact.name,
                                avatarResName = contact.avatarResName,
                                avatarColorHex = contact.avatarColorHex,
                                size = 36.dp
                            )

                            Text(
                                text = contact.name,
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Medium
                            )
                        }
                    }
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    TextButton(onClick = onDismiss) { Text("Cancel") }
                    Button(
                        onClick = {
                            if (groupName.isNotBlank()) {
                                onCreate(groupName.trim(), selectedContactIds.toList())
                            }
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = PrimaryBlue),
                        shape = RoundedCornerShape(14.dp)
                    ) {
                        Text("Create Group")
                    }
                }
            }
        }
    }
}
