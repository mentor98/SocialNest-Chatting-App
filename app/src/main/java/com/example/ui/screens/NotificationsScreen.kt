package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.NotificationItem
import com.example.model.NotificationType
import com.example.ui.components.AvatarView
import com.example.ui.components.BottomNavigationDock
import com.example.ui.theme.*
import com.example.viewmodel.ChatViewModel
import com.example.viewmodel.NavTab
import com.example.viewmodel.Screen

@Composable
fun NotificationsScreen(
    viewModel: ChatViewModel,
    modifier: Modifier = Modifier
) {
    val uiState by viewModel.uiState.collectAsState()

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
            // Header
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 12.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                IconButton(
                    onClick = { viewModel.navigateBack() },
                    modifier = Modifier.size(40.dp)
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Back",
                        tint = MaterialTheme.colorScheme.onSurface
                    )
                }

                Text(
                    text = "Notifications",
                    style = MaterialTheme.typography.titleLarge.copy(
                        fontWeight = FontWeight.Medium,
                        fontSize = 20.sp
                    ),
                    color = MaterialTheme.colorScheme.onSurface
                )

                TextButton(
                    onClick = { viewModel.markAllNotificationsAsRead() },
                    modifier = Modifier.testTag("mark_all_read_button")
                ) {
                    Text(
                        text = "Mark all read",
                        style = MaterialTheme.typography.bodySmall,
                        color = PrimaryBlue,
                        fontWeight = FontWeight.Medium
                    )
                }
            }

            // Notifications List
            if (uiState.notifications.isEmpty()) {
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth(),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "No notifications yet",
                        style = MaterialTheme.typography.bodyMedium,
                        color = TextTertiary
                    )
                }
            } else {
                LazyColumn(
                    contentPadding = PaddingValues(start = 16.dp, end = 16.dp, top = 8.dp, bottom = 90.dp),
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth()
                        .testTag("notifications_list")
                ) {
                    items(
                        items = uiState.notifications,
                        key = { it.id }
                    ) { item ->
                        NotificationCard(
                            item = item,
                            onClick = {
                                viewModel.markNotificationAsRead(item.id)
                                if (item.contactId != null) {
                                    val contact = uiState.contacts.find { it.id == item.contactId }
                                    if (contact != null) {
                                        viewModel.openChatWithContact(contact)
                                    }
                                }
                            },
                            onActionClick = {
                                viewModel.markNotificationAsRead(item.id)
                                viewModel.showToast("Action completed for ${item.title}")
                            }
                        )
                    }
                }
            }
        }

        // Bottom Nav Dock
        BottomNavigationDock(
            activeTab = uiState.activeNavTab,
            onTabSelected = { tab ->
                viewModel.setActiveNavTab(tab)
            },
            unreadNotificationsCount = uiState.notifications.count { !it.isRead },
            modifier = Modifier.align(Alignment.BottomCenter)
        )
    }
}

@Composable
private fun NotificationCard(
    item: NotificationItem,
    onClick: () -> Unit,
    onActionClick: () -> Unit
) {
    val indicatorColor = when (item.type) {
        NotificationType.MESSAGE -> PrimaryBlue
        NotificationType.MISSED_CALL -> AccentPink
        NotificationType.FRIEND_REQUEST -> AccentPurple
        NotificationType.SYSTEM -> AccentGreen
    }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp)
            .shadow(
                elevation = if (!item.isRead) 4.dp else 2.dp,
                shape = RoundedCornerShape(20.dp),
                spotColor = indicatorColor.copy(alpha = 0.12f)
            )
            .clip(RoundedCornerShape(20.dp))
            .background(MaterialTheme.colorScheme.surface)
            .clickable { onClick() }
            .padding(14.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth()
        ) {
            // Icon or Avatar
            if (item.contactId != null) {
                AvatarView(
                    name = item.title,
                    avatarResName = item.avatarResName,
                    avatarColorHex = item.avatarColorHex,
                    size = 46.dp,
                    showOnlineIndicator = false
                )
            } else {
                Box(
                    modifier = Modifier
                        .size(46.dp)
                        .clip(CircleShape)
                        .background(indicatorColor.copy(alpha = 0.15f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = when (item.type) {
                            NotificationType.MISSED_CALL -> Icons.Default.PhoneMissed
                            NotificationType.FRIEND_REQUEST -> Icons.Default.PersonAdd
                            NotificationType.SYSTEM -> Icons.Default.Info
                            else -> Icons.Default.Notifications
                        },
                        contentDescription = null,
                        tint = indicatorColor,
                        modifier = Modifier.size(22.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = item.title,
                        style = MaterialTheme.typography.titleSmall.copy(
                            fontWeight = if (!item.isRead) FontWeight.Bold else FontWeight.Medium,
                            fontSize = 14.5.sp
                        ),
                        color = MaterialTheme.colorScheme.onSurface
                    )

                    Text(
                        text = item.timeAgo,
                        style = MaterialTheme.typography.bodySmall,
                        color = TextTertiary,
                        fontSize = 11.sp
                    )
                }

                Spacer(modifier = Modifier.height(2.dp))

                Text(
                    text = item.description,
                    style = MaterialTheme.typography.bodyMedium,
                    color = TextSecondary,
                    fontSize = 12.5.sp,
                    maxLines = 2
                )
            }

            if (!item.isRead) {
                Spacer(modifier = Modifier.width(8.dp))
                Box(
                    modifier = Modifier
                        .size(8.dp)
                        .clip(CircleShape)
                        .background(indicatorColor)
                )
            }
        }
    }
}
