package com.example.ui.screens

import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Call
import androidx.compose.material.icons.filled.Videocam
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.Contact
import com.example.model.Message
import com.example.model.MessageType
import com.example.ui.components.*
import com.example.ui.theme.*
import com.example.viewmodel.ChatViewModel
import com.example.viewmodel.NavTab
import com.example.viewmodel.Screen
import kotlinx.coroutines.launch

@Composable
fun ChatScreen(
    viewModel: ChatViewModel,
    modifier: Modifier = Modifier
) {
    val uiState by viewModel.uiState.collectAsState()
    val listState = rememberLazyListState()
    val coroutineScope = rememberCoroutineScope()

    var showAttachmentSheet by remember { mutableStateOf(false) }
    var activeReactionMessage by remember { mutableStateOf<Message?>(null) }

    val contact = uiState.selectedContact ?: Contact(
        id = "contact_martin",
        name = "Martin",
        username = "martin_dev",
        avatarResName = "avatar_martin",
        avatarColorHex = 0xFF568CF5,
        statusMessage = "Excellent are efforts",
        isOnline = true,
        initialLetter = "M"
    )

    // Auto scroll to bottom when messages change
    LaunchedEffect(uiState.currentMessages.size) {
        if (uiState.currentMessages.isNotEmpty()) {
            listState.animateScrollToItem(uiState.currentMessages.size - 1)
        }
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        Row(modifier = Modifier.fillMaxSize()) {
            // Vertical Navigation Rail on the Left (from center reference screen)
            LeftNavigationRail(
                activeTab = uiState.activeNavTab,
                onTabSelected = { tab ->
                    viewModel.setActiveNavTab(tab)
                },
                unreadNotificationsCount = uiState.notifications.count { !it.isRead },
                modifier = Modifier
                    .fillMaxHeight()
                    .testTag("chat_left_nav_rail")
            )

            // Main Chat Area
            Column(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxHeight()
                    .statusBarsPadding()
            ) {
                // Header (Top bar)
                ChatHeader(
                    contact = contact,
                    onBackClick = { viewModel.navigateBack() },
                    onProfileClick = { viewModel.navigateTo(Screen.ContactProfile(contact.id)) },
                    onVoiceCallClick = { viewModel.startCall(contact, isVideo = false) },
                    onVideoCallClick = { viewModel.startCall(contact, isVideo = true) }
                )

                // Conversation Messages List
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth()
                ) {
                    LazyColumn(
                        state = listState,
                        contentPadding = PaddingValues(horizontal = 14.dp, vertical = 10.dp),
                        modifier = Modifier
                            .fillMaxSize()
                            .testTag("chat_messages_list")
                    ) {
                        items(
                            items = uiState.currentMessages,
                            key = { it.id }
                        ) { message ->
                            MessageBubble(
                                message = message,
                                onClick = {
                                    activeReactionMessage = message
                                },
                                onLongClick = {
                                    activeReactionMessage = message
                                },
                                onReactionClick = { emoji ->
                                    viewModel.addReaction(message, emoji)
                                }
                            )
                        }

                        // Typing indicator bubble if simulated
                        if (uiState.isSimulatedTyping) {
                            item {
                                TypingIndicatorBubble()
                            }
                        }
                    }

                    // Floating Reaction Picker Popup
                    if (activeReactionMessage != null) {
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .clickable(
                                    interactionSource = remember { MutableInteractionSource() },
                                    indication = null
                                ) { activeReactionMessage = null }
                                .padding(16.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Card(
                                shape = RoundedCornerShape(20.dp),
                                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                                modifier = Modifier.shadow(16.dp, RoundedCornerShape(20.dp))
                            ) {
                                Column(
                                    modifier = Modifier.padding(14.dp),
                                    horizontalAlignment = Alignment.CenterHorizontally
                                ) {
                                    QuickReactionPicker(
                                        onReactionSelected = { emoji ->
                                            activeReactionMessage?.let { viewModel.addReaction(it, emoji) }
                                            activeReactionMessage = null
                                        }
                                    )

                                    Spacer(modifier = Modifier.height(10.dp))

                                    Row(
                                        horizontalArrangement = Arrangement.spacedBy(16.dp)
                                    ) {
                                        TextButton(onClick = {
                                            activeReactionMessage?.let { viewModel.setReplyTo(it) }
                                            activeReactionMessage = null
                                        }) {
                                            Text("Reply", color = PrimaryBlue)
                                        }

                                        TextButton(onClick = {
                                            activeReactionMessage?.let { viewModel.deleteMessage(it.id) }
                                            activeReactionMessage = null
                                        }) {
                                            Text("Delete", color = AccentPink)
                                        }
                                    }
                                }
                            }
                        }
                    }
                }

                // Voice Recording Bar or Standard Message Composer
                if (uiState.isVoiceRecording) {
                    VoiceRecorderBar(
                        durationSeconds = uiState.recordingDuration,
                        onCancel = { viewModel.cancelVoiceRecording() },
                        onFinish = { viewModel.finishVoiceRecording() },
                        modifier = Modifier.padding(12.dp)
                    )
                } else {
                    MessageComposer(
                        replyToMessage = uiState.replyToMessage,
                        onClearReply = { viewModel.setReplyTo(null) },
                        onSendMessage = { text ->
                            viewModel.sendMessage(text)
                        },
                        onAttachmentClick = { showAttachmentSheet = true },
                        onMicClick = { viewModel.startVoiceRecording() }
                    )
                }
            }
        }

        // Attachment Bottom Sheet
        if (showAttachmentSheet) {
            AttachmentBottomSheet(
                onDismiss = { showAttachmentSheet = false },
                onOptionSelected = { type, url, name ->
                    when (type) {
                        MessageType.IMAGE -> viewModel.sendMessage(text = "Sent a photo", type = MessageType.IMAGE, attachmentUrl = url, attachmentName = name)
                        MessageType.FILE -> viewModel.sendMessage(text = "Sent a document", type = MessageType.FILE, attachmentName = name)
                        MessageType.VOICE -> viewModel.sendMessage(text = "Voice memo", type = MessageType.VOICE, voiceDurationSeconds = 14)
                        else -> viewModel.sendMessage(text = name, type = MessageType.TEXT)
                    }
                }
            )
        }
    }
}

@Composable
private fun ChatHeader(
    contact: Contact,
    onBackClick: () -> Unit,
    onProfileClick: () -> Unit,
    onVoiceCallClick: () -> Unit,
    onVideoCallClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        color = MaterialTheme.colorScheme.surface,
        shadowElevation = 6.dp,
        modifier = modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            // Left side: Back button + Avatar + Name & Online status
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.weight(1f)
            ) {
                IconButton(
                    onClick = onBackClick,
                    modifier = Modifier
                        .size(36.dp)
                        .testTag("chat_back_button")
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Back",
                        tint = MaterialTheme.colorScheme.onSurface,
                        modifier = Modifier.size(22.dp)
                    )
                }

                Spacer(modifier = Modifier.width(6.dp))

                // Avatar (Clickable to open profile)
                AvatarView(
                    name = contact.name,
                    avatarResName = contact.avatarResName,
                    avatarColorHex = contact.avatarColorHex,
                    size = 40.dp,
                    isOnline = contact.isOnline,
                    showOnlineIndicator = true,
                    modifier = Modifier
                        .clickable(
                            interactionSource = remember { MutableInteractionSource() },
                            indication = null
                        ) { onProfileClick() }
                        .testTag("chat_header_avatar")
                )

                Spacer(modifier = Modifier.width(12.dp))

                Column(
                    modifier = Modifier
                        .clickable(
                            interactionSource = remember { MutableInteractionSource() },
                            indication = null
                        ) { onProfileClick() }
                ) {
                    Text(
                        text = contact.name,
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.SemiBold,
                            fontSize = 16.sp
                        ),
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = if (contact.isOnline) "Online" else contact.lastSeenText,
                        style = MaterialTheme.typography.bodySmall.copy(
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Normal
                        ),
                        color = if (contact.isOnline) AccentGreen else TextTertiary
                    )
                }
            }

            // Right side: Audio Call & Video Call action buttons (rounded-2xl)
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .clip(RoundedCornerShape(14.dp))
                        .background(PrimaryBlueContainer)
                        .clickable { onVoiceCallClick() }
                        .testTag("chat_voice_call_button"),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Call,
                        contentDescription = "Voice Call",
                        tint = PrimaryBlue,
                        modifier = Modifier.size(19.dp)
                    )
                }

                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .clip(RoundedCornerShape(14.dp))
                        .background(PrimaryBlueContainer)
                        .clickable { onVideoCallClick() }
                        .testTag("chat_video_call_button"),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Videocam,
                        contentDescription = "Video Call",
                        tint = PrimaryBlue,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }
        }
    }
}

@Composable
private fun TypingIndicatorBubble(contactName: String = "Martin") {
    Row(
        modifier = Modifier
            .padding(vertical = 6.dp)
            .fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Row(
            modifier = Modifier
                .clip(RoundedCornerShape(16.dp))
                .background(Color.White.copy(alpha = 0.7f))
                .padding(horizontal = 10.dp, vertical = 6.dp),
            horizontalArrangement = Arrangement.spacedBy(3.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            listOf(0, 1, 2).forEach { index ->
                Box(
                    modifier = Modifier
                        .size(6.dp)
                        .clip(CircleShape)
                        .background(TextTertiary)
                )
            }
        }

        Text(
            text = "$contactName is typing...",
            fontSize = 11.sp,
            color = TextTertiary,
            style = MaterialTheme.typography.bodySmall.copy(
                fontStyle = androidx.compose.ui.text.font.FontStyle.Italic
            )
        )
    }
}
