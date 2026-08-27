package com.example.ui.screens

import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.UserStatusUpdate
import com.example.model.WhatsAppChannel
import com.example.ui.components.AvatarView
import com.example.ui.components.BottomNavigationDock
import com.example.ui.theme.*
import com.example.viewmodel.ChatViewModel
import com.example.viewmodel.NavTab
import com.example.viewmodel.Screen

@Composable
fun UpdatesScreen(
    viewModel: ChatViewModel,
    modifier: Modifier = Modifier
) {
    val uiState by viewModel.uiState.collectAsState()
    var showAddStatusSheet by remember { mutableStateOf(false) }

    val selfStatus = uiState.statusUpdates.firstOrNull { it.isSelf }
    val recentUpdates = uiState.statusUpdates.filter { !it.isSelf && !it.isViewed }
    val viewedUpdates = uiState.statusUpdates.filter { !it.isSelf && it.isViewed }

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
            // Top App Bar
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 12.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "Updates",
                    style = MaterialTheme.typography.titleLarge.copy(
                        fontWeight = FontWeight.Bold,
                        fontSize = 24.sp
                    ),
                    color = MaterialTheme.colorScheme.onSurface
                )

                Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    IconButton(
                        onClick = { viewModel.navigateTo(Screen.Search) },
                        modifier = Modifier.size(36.dp)
                    ) {
                        Icon(imageVector = Icons.Default.Search, contentDescription = "Search", tint = TextPrimary)
                    }
                    IconButton(
                        onClick = { showAddStatusSheet = true },
                        modifier = Modifier.size(36.dp)
                    ) {
                        Icon(imageVector = Icons.Default.CameraAlt, contentDescription = "Camera", tint = TextPrimary)
                    }
                }
            }

            // Updates & Channels Content
            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                contentPadding = PaddingValues(bottom = 90.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // Status Section Title
                item {
                    Text(
                        text = "Status",
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                        modifier = Modifier.padding(horizontal = 20.dp, vertical = 4.dp),
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }

                // "My Status" Row
                item {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable {
                                if (selfStatus != null && selfStatus.stories.isNotEmpty()) {
                                    viewModel.openStoryViewer(selfStatus)
                                } else {
                                    showAddStatusSheet = true
                                }
                            }
                            .padding(horizontal = 20.dp, vertical = 8.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(14.dp)
                    ) {
                        Box(contentAlignment = Alignment.BottomEnd) {
                            AvatarView(
                                name = selfStatus?.contactName ?: "Alex Rivera",
                                avatarResName = selfStatus?.avatarResName ?: "ic_chat_logo",
                                avatarColorHex = 0xFF1877F2,
                                size = 52.dp
                            )
                            Box(
                                modifier = Modifier
                                    .size(20.dp)
                                    .clip(CircleShape)
                                    .background(WhatsAppGreen)
                                    .border(1.5.dp, MaterialTheme.colorScheme.surface, CircleShape),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Add,
                                    contentDescription = "Add Status",
                                    tint = Color.White,
                                    modifier = Modifier.size(14.dp)
                                )
                            }
                        }

                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "My Status",
                                fontWeight = FontWeight.Bold,
                                fontSize = 16.sp,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Text(
                                text = if (selfStatus != null && selfStatus.stories.isNotEmpty())
                                    "${selfStatus.stories.size} status update • ${selfStatus.lastUpdatedText}"
                                else "Tap to add status update",
                                fontSize = 13.5.sp,
                                color = TextSecondary
                            )
                        }

                        IconButton(onClick = { showAddStatusSheet = true }) {
                            Icon(
                                imageVector = Icons.Default.Edit,
                                contentDescription = "Edit Status",
                                tint = WhatsAppDarkGreen
                            )
                        }
                    }
                }

                // Recent Updates Subheader
                if (recentUpdates.isNotEmpty()) {
                    item {
                        Text(
                            text = "Recent updates",
                            fontSize = 13.5.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = TextSecondary,
                            modifier = Modifier.padding(horizontal = 20.dp, vertical = 4.dp)
                        )
                    }

                    items(recentUpdates, key = { it.id }) { update ->
                        StatusRowItem(
                            status = update,
                            onClick = { viewModel.openStoryViewer(update) }
                        )
                    }
                }

                // Viewed Updates Subheader
                if (viewedUpdates.isNotEmpty()) {
                    item {
                        Text(
                            text = "Viewed updates",
                            fontSize = 13.5.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = TextTertiary,
                            modifier = Modifier.padding(horizontal = 20.dp, vertical = 4.dp)
                        )
                    }

                    items(viewedUpdates, key = { it.id }) { update ->
                        StatusRowItem(
                            status = update,
                            onClick = { viewModel.openStoryViewer(update) }
                        )
                    }
                }

                // Divider
                item {
                    HorizontalDivider(
                        modifier = Modifier.padding(horizontal = 20.dp, vertical = 8.dp),
                        color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f)
                    )
                }

                // Channels / Communities Section Title
                item {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 20.dp, vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column {
                            Text(
                                text = "Channels",
                                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Text(
                                text = "Stay updated on topics you care about",
                                fontSize = 12.5.sp,
                                color = TextSecondary
                            )
                        }

                        TextButton(onClick = {}) {
                            Text("Explore", color = WhatsAppDarkGreen, fontWeight = FontWeight.Bold)
                        }
                    }
                }

                // Channels List
                items(uiState.channels, key = { it.id }) { channel ->
                    ChannelRowItem(
                        channel = channel,
                        onFollowClick = { viewModel.toggleChannelFollow(channel.id) }
                    )
                }
            }
        }

        // Floating Action Buttons (WhatsApp style Pencil & Camera)
        Column(
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(end = 20.dp, bottom = 90.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Pencil Button (Text Status)
            SmallFloatingActionButton(
                onClick = { showAddStatusSheet = true },
                containerColor = MaterialTheme.colorScheme.surfaceVariant,
                contentColor = MaterialTheme.colorScheme.onSurfaceVariant,
                shape = CircleShape,
                modifier = Modifier.shadow(4.dp, CircleShape)
            ) {
                Icon(imageVector = Icons.Default.Edit, contentDescription = "Text Status", modifier = Modifier.size(20.dp))
            }

            // Camera / Main Status FAB
            FloatingActionButton(
                onClick = { showAddStatusSheet = true },
                containerColor = WhatsAppGreen,
                contentColor = Color.White,
                shape = CircleShape,
                modifier = Modifier.shadow(6.dp, CircleShape)
            ) {
                Icon(imageVector = Icons.Default.CameraAlt, contentDescription = "Photo Status", modifier = Modifier.size(24.dp))
            }
        }

        // Bottom Navigation Bar
        BottomNavigationDock(
            activeTab = uiState.activeNavTab,
            onTabSelected = { tab -> viewModel.setActiveNavTab(tab) },
            unreadNotificationsCount = uiState.conversations.sumOf { it.unreadCount },
            modifier = Modifier.align(Alignment.BottomCenter)
        )

        // Status Creator Sheet
        if (showAddStatusSheet) {
            AddStatusBottomSheet(
                onDismiss = { showAddStatusSheet = false },
                onPublish = { text, gradientIndex ->
                    showAddStatusSheet = false
                    viewModel.addStatusStory(text, gradientIndex)
                }
            )
        }

        // Status Viewer
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
fun StatusRowItem(
    status: UserStatusUpdate,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .padding(horizontal = 20.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        // Status Avatar Ring
        Box(
            modifier = Modifier
                .size(52.dp)
                .border(
                    2.5.dp,
                    if (status.isViewed) TextTertiary.copy(alpha = 0.5f) else WhatsAppGreen,
                    CircleShape
                )
                .padding(3.dp)
        ) {
            AvatarView(
                name = status.contactName,
                avatarResName = status.avatarResName,
                avatarColorHex = status.avatarColorHex,
                size = 44.dp
            )
        }

        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = status.contactName,
                fontWeight = FontWeight.SemiBold,
                fontSize = 15.5.sp,
                color = MaterialTheme.colorScheme.onSurface
            )
            Text(
                text = status.lastUpdatedText,
                fontSize = 13.sp,
                color = TextSecondary
            )
        }
    }
}

@Composable
fun ChannelRowItem(
    channel: WhatsAppChannel,
    onFollowClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp, vertical = 10.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        AvatarView(
            name = channel.name,
            avatarResName = "",
            avatarColorHex = channel.avatarColorHex,
            size = 48.dp
        )

        Column(modifier = Modifier.weight(1f)) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Text(
                    text = channel.name,
                    fontWeight = FontWeight.Bold,
                    fontSize = 15.sp,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                if (channel.verified) {
                    Icon(
                        imageVector = Icons.Default.CheckCircle,
                        contentDescription = "Verified",
                        tint = WhatsAppGreen,
                        modifier = Modifier.size(15.dp)
                    )
                }
            }

            Text(
                text = channel.lastUpdateText,
                fontSize = 13.sp,
                color = TextSecondary,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )

            Text(
                text = "${channel.followersCount} • ${channel.lastUpdateTime}",
                fontSize = 11.5.sp,
                color = TextTertiary
            )
        }

        // Follow Button
        Button(
            onClick = onFollowClick,
            shape = RoundedCornerShape(20.dp),
            colors = if (channel.isFollowing) ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.surfaceVariant, contentColor = MaterialTheme.colorScheme.onSurfaceVariant)
            else ButtonDefaults.buttonColors(containerColor = WhatsAppLightGreen, contentColor = WhatsAppDarkGreen),
            contentPadding = PaddingValues(horizontal = 14.dp, vertical = 6.dp)
        ) {
            Text(
                text = if (channel.isFollowing) "Following" else "Follow",
                fontWeight = FontWeight.Bold,
                fontSize = 13.sp
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddStatusBottomSheet(
    onDismiss: () -> Unit,
    onPublish: (String, Int) -> Unit
) {
    var statusText by remember { mutableStateOf("") }
    var selectedGradientIndex by remember { mutableStateOf(0) }

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
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "Add WhatsApp Status",
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp
                )
                Button(
                    onClick = { onPublish(statusText, selectedGradientIndex) },
                    enabled = statusText.isNotBlank(),
                    colors = ButtonDefaults.buttonColors(containerColor = WhatsAppGreen),
                    shape = RoundedCornerShape(20.dp)
                ) {
                    Text("Share to Status", color = Color.White, fontWeight = FontWeight.Bold)
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Story Preview Card with Gradient
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(180.dp)
                    .clip(RoundedCornerShape(16.dp))
                    .background(postGradients[selectedGradientIndex])
                    .padding(20.dp),
                contentAlignment = Alignment.Center
            ) {
                TextField(
                    value = statusText,
                    onValueChange = { statusText = it },
                    placeholder = { Text("Type a status...", color = Color.White.copy(alpha = 0.8f), fontSize = 20.sp, fontWeight = FontWeight.Bold) },
                    colors = TextFieldDefaults.colors(
                        focusedContainerColor = Color.Transparent,
                        unfocusedContainerColor = Color.Transparent,
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White,
                        focusedIndicatorColor = Color.Transparent,
                        unfocusedIndicatorColor = Color.Transparent
                    ),
                    textStyle = MaterialTheme.typography.bodyLarge.copy(
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                )
            }

            // Gradient Background Palette Selector
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 12.dp),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                postGradients.forEachIndexed { index, grad ->
                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .clip(CircleShape)
                            .background(grad)
                            .border(if (selectedGradientIndex == index) 2.5.dp else 0.dp, WhatsAppGreen, CircleShape)
                            .clickable { selectedGradientIndex = index }
                    )
                }
            }
        }
    }
}
