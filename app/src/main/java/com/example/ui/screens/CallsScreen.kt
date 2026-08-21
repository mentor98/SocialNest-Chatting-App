package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.CallMade
import androidx.compose.material.icons.automirrored.filled.CallMissed
import androidx.compose.material.icons.automirrored.filled.CallReceived
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.Call
import androidx.compose.material.icons.outlined.Videocam
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
import com.example.model.CallRecord
import com.example.model.Contact
import com.example.ui.components.AvatarView
import com.example.ui.components.BottomNavigationDock
import com.example.ui.theme.*
import com.example.viewmodel.ChatViewModel
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun CallsScreen(
    viewModel: ChatViewModel,
    modifier: Modifier = Modifier
) {
    val uiState by viewModel.uiState.collectAsState()
    var selectedFilter by remember { mutableStateOf("All") }
    var showNewCallSheet by remember { mutableStateOf(false) }

    val calls = uiState.callRecords
    val filteredCalls = remember(calls, selectedFilter) {
        if (selectedFilter == "Missed") {
            calls.filter { it.isMissed }
        } else {
            calls
        }
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
            // Top Bar
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 10.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "Calls",
                    style = MaterialTheme.typography.titleLarge.copy(
                        fontWeight = FontWeight.Bold,
                        fontSize = 22.sp,
                        letterSpacing = (-0.3).sp
                    ),
                    color = MaterialTheme.colorScheme.onSurface
                )

                // Filter Pill: All / Missed
                Row(
                    modifier = Modifier
                        .clip(RoundedCornerShape(20.dp))
                        .background(MaterialTheme.colorScheme.surface)
                        .padding(3.dp),
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(16.dp))
                            .background(if (selectedFilter == "All") PrimaryBlue else Color.Transparent)
                            .clickable { selectedFilter = "All" }
                            .padding(horizontal = 12.dp, vertical = 5.dp)
                    ) {
                        Text(
                            text = "All",
                            color = if (selectedFilter == "All") Color.White else TextSecondary,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                    }

                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(16.dp))
                            .background(if (selectedFilter == "Missed") AccentPink else Color.Transparent)
                            .clickable { selectedFilter = "Missed" }
                            .padding(horizontal = 12.dp, vertical = 5.dp)
                    ) {
                        Text(
                            text = "Missed",
                            color = if (selectedFilter == "Missed") Color.White else TextSecondary,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                }

                // New Call Button
                Box(
                    modifier = Modifier
                        .size(38.dp)
                        .clip(CircleShape)
                        .background(PrimaryBlue)
                        .clickable { showNewCallSheet = true }
                        .testTag("calls_new_call_button"),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Call,
                        contentDescription = "New Call",
                        tint = Color.White,
                        modifier = Modifier.size(19.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(6.dp))

            // Calls List
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
            ) {
                if (filteredCalls.isEmpty()) {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Outlined.Call,
                                contentDescription = null,
                                tint = TextTertiary,
                                modifier = Modifier.size(48.dp)
                            )
                            Text(
                                text = if (selectedFilter == "Missed") "No missed calls" else "No call history",
                                color = TextTertiary,
                                fontSize = 15.sp
                            )
                        }
                    }
                } else {
                    LazyColumn(
                        contentPadding = PaddingValues(start = 16.dp, end = 16.dp, top = 4.dp, bottom = 85.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp),
                        modifier = Modifier.fillMaxSize()
                    ) {
                        items(filteredCalls, key = { it.id }) { call ->
                            CallRowItem(
                                call = call,
                                onVoiceCallClick = {
                                    val contact = uiState.contacts.firstOrNull { it.name.equals(call.contactName, ignoreCase = true) }
                                        ?: Contact(id = "contact_temp", name = call.contactName, username = call.contactName.lowercase(), statusMessage = "", initialLetter = call.contactName.take(1))
                                    viewModel.startCall(contact, isVideo = false)
                                },
                                onVideoCallClick = {
                                    val contact = uiState.contacts.firstOrNull { it.name.equals(call.contactName, ignoreCase = true) }
                                        ?: Contact(id = "contact_temp", name = call.contactName, username = call.contactName.lowercase(), statusMessage = "", initialLetter = call.contactName.take(1))
                                    viewModel.startCall(contact, isVideo = true)
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
            unreadNotificationsCount = uiState.conversations.sumOf { it.unreadCount },
            modifier = Modifier.align(Alignment.BottomCenter)
        )

        // New Call Picker Bottom Sheet
        if (showNewCallSheet) {
            NewCallBottomSheet(
                contacts = uiState.contacts,
                onDismiss = { showNewCallSheet = false },
                onVoiceCall = { contact ->
                    showNewCallSheet = false
                    viewModel.startCall(contact, isVideo = false)
                },
                onVideoCall = { contact ->
                    showNewCallSheet = false
                    viewModel.startCall(contact, isVideo = true)
                }
            )
        }
    }
}

@Composable
fun CallRowItem(
    call: CallRecord,
    onVoiceCallClick: () -> Unit,
    onVideoCallClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val formattedTime = remember(call.timestamp) {
        val sdf = SimpleDateFormat("MMM dd, hh:mm a", Locale.getDefault())
        sdf.format(Date(call.timestamp))
    }

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
            .padding(horizontal = 14.dp, vertical = 12.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth()
        ) {
            AvatarView(
                name = call.contactName,
                avatarResName = call.contactAvatarResName,
                avatarColorHex = 0xFF568CF5,
                size = 46.dp
            )

            Spacer(modifier = Modifier.width(12.dp))

            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(3.dp)
            ) {
                Text(
                    text = call.contactName,
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 15.5.sp
                    ),
                    color = if (call.isMissed) AccentPink else MaterialTheme.colorScheme.onSurface
                )

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    val (icon, color, label) = when {
                        call.isMissed -> Triple(Icons.AutoMirrored.Filled.CallMissed, AccentPink, "Missed")
                        call.isIncoming -> Triple(Icons.AutoMirrored.Filled.CallReceived, AccentGreen, "Incoming")
                        else -> Triple(Icons.AutoMirrored.Filled.CallMade, PrimaryBlue, "Outgoing")
                    }

                    Icon(
                        imageVector = icon,
                        contentDescription = label,
                        tint = color,
                        modifier = Modifier.size(13.dp)
                    )

                    Text(
                        text = "$formattedTime • ${if (call.durationSeconds > 0) "${call.durationSeconds / 60}m ${call.durationSeconds % 60}s" else "Missed"}",
                        fontSize = 12.sp,
                        color = TextTertiary
                    )
                }
            }

            // Quick Call Actions
            Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                Box(
                    modifier = Modifier
                        .size(36.dp)
                        .clip(CircleShape)
                        .background(PrimaryBlueContainer)
                        .clickable { onVoiceCallClick() },
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Call,
                        contentDescription = "Voice Call",
                        tint = PrimaryBlue,
                        modifier = Modifier.size(18.dp)
                    )
                }

                Box(
                    modifier = Modifier
                        .size(36.dp)
                        .clip(CircleShape)
                        .background(PrimaryBlueContainer)
                        .clickable { onVideoCallClick() },
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Videocam,
                        contentDescription = "Video Call",
                        tint = PrimaryBlue,
                        modifier = Modifier.size(18.dp)
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NewCallBottomSheet(
    contacts: List<Contact>,
    onDismiss: () -> Unit,
    onVoiceCall: (Contact) -> Unit,
    onVideoCall: (Contact) -> Unit
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
                text = "Start a Call",
                style = MaterialTheme.typography.titleLarge.copy(
                    fontWeight = FontWeight.Bold,
                    fontSize = 20.sp
                ),
                modifier = Modifier.padding(bottom = 12.dp)
            )

            LazyColumn(
                modifier = Modifier.heightIn(max = 340.dp),
                verticalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                items(contacts, key = { it.id }) { contact ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(14.dp))
                            .padding(vertical = 8.dp, horizontal = 4.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        AvatarView(
                            name = contact.name,
                            avatarResName = contact.avatarResName,
                            avatarColorHex = contact.avatarColorHex,
                            size = 40.dp,
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
                                text = if (contact.isOnline) "Online" else contact.lastSeenText,
                                fontSize = 12.sp,
                                color = if (contact.isOnline) AccentGreen else TextTertiary
                            )
                        }

                        Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                            IconButton(onClick = { onVoiceCall(contact) }) {
                                Icon(
                                    imageVector = Icons.Default.Call,
                                    contentDescription = "Voice Call",
                                    tint = PrimaryBlue
                                )
                            }
                            IconButton(onClick = { onVideoCall(contact) }) {
                                Icon(
                                    imageVector = Icons.Default.Videocam,
                                    contentDescription = "Video Call",
                                    tint = PrimaryBlue
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
