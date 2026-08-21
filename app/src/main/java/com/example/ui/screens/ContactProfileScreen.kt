package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.model.Contact
import com.example.ui.components.AvatarView
import com.example.ui.theme.*
import com.example.viewmodel.ChatViewModel
import com.example.viewmodel.Screen

@Composable
fun ContactProfileScreen(
    viewModel: ChatViewModel,
    modifier: Modifier = Modifier
) {
    val uiState by viewModel.uiState.collectAsState()
    val contact = uiState.selectedContact ?: Contact(
        id = "contact_martin",
        name = "Martin",
        username = "martin_dev",
        avatarResName = "avatar_martin",
        avatarColorHex = 0xFF568CF5,
        statusMessage = "Excellent are efforts",
        isOnline = true,
        initialLetter = "M",
        visitsCount = 69,
        messagesCount = 18,
        callsCount = 87,
        notificationsAllowed = true,
        remarks = "Switch Gamer & Senior Dev"
    )

    var showEditRemarksDialog by remember { mutableStateOf(false) }
    var showDynamicDialog by remember { mutableStateOf(false) }
    var showMoreDialog by remember { mutableStateOf(false) }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
                .verticalScroll(rememberScrollState())
                .padding(bottom = 24.dp)
        ) {
            // Top Navigation Row
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 12.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                IconButton(
                    onClick = { viewModel.navigateBack() },
                    modifier = Modifier
                        .size(40.dp)
                        .testTag("profile_back_button")
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Back",
                        tint = MaterialTheme.colorScheme.onSurface,
                        modifier = Modifier.size(24.dp)
                    )
                }

                Text(
                    text = "Profile",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Medium,
                    color = MaterialTheme.colorScheme.onSurface
                )

                IconButton(
                    onClick = { showMoreDialog = true },
                    modifier = Modifier.size(40.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.MoreVert,
                        contentDescription = "More Options",
                        tint = MaterialTheme.colorScheme.onSurface,
                        modifier = Modifier.size(24.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Contact Avatar & Name
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Large Avatar
                AvatarView(
                    name = contact.name,
                    avatarResName = contact.avatarResName,
                    avatarColorHex = contact.avatarColorHex,
                    size = 92.dp,
                    isOnline = contact.isOnline,
                    showOnlineIndicator = true,
                    modifier = Modifier.shadow(8.dp, CircleShape)
                )

                Spacer(modifier = Modifier.height(14.dp))

                // User Name with verification badge
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Text(
                        text = contact.name,
                        style = MaterialTheme.typography.titleLarge.copy(
                            fontWeight = FontWeight.SemiBold,
                            fontSize = 22.sp
                        ),
                        color = MaterialTheme.colorScheme.onSurface
                    )

                    Icon(
                        imageVector = Icons.Filled.CheckCircle,
                        contentDescription = "Verified",
                        tint = PrimaryBlue,
                        modifier = Modifier.size(18.dp)
                    )
                }

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = "@${contact.username}",
                    style = MaterialTheme.typography.bodyMedium,
                    color = TextSecondary,
                    fontSize = 13.sp
                )

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = "\"${contact.statusMessage}\"",
                    style = MaterialTheme.typography.bodyMedium,
                    color = TextTertiary,
                    fontSize = 13.sp
                )
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Statistics Row Card (69 Visits, 18 Messages, 87 Calls)
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp)
                    .shadow(
                        elevation = 6.dp,
                        shape = RoundedCornerShape(22.dp),
                        spotColor = PrimaryBlue.copy(alpha = 0.12f),
                        ambientColor = PrimaryBlue.copy(alpha = 0.06f)
                    )
                    .clip(RoundedCornerShape(22.dp))
                    .background(MaterialTheme.colorScheme.surface)
                    .padding(vertical = 18.dp, horizontal = 12.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceAround,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    StatColumn(
                        count = contact.visitsCount.toString(),
                        label = "Visits"
                    )

                    Box(
                        modifier = Modifier
                            .width(1.dp)
                            .height(30.dp)
                            .background(IncomingBubbleBorder)
                    )

                    StatColumn(
                        count = contact.messagesCount.toString(),
                        label = "Messages"
                    )

                    Box(
                        modifier = Modifier
                            .width(1.dp)
                            .height(30.dp)
                            .background(IncomingBubbleBorder)
                    )

                    StatColumn(
                        count = contact.callsCount.toString(),
                        label = "Calls"
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Notification Card with toggle switch
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp)
                    .shadow(
                        elevation = 6.dp,
                        shape = RoundedCornerShape(22.dp),
                        spotColor = PrimaryBlue.copy(alpha = 0.12f),
                        ambientColor = PrimaryBlue.copy(alpha = 0.06f)
                    )
                    .clip(RoundedCornerShape(22.dp))
                    .background(MaterialTheme.colorScheme.surface)
                    .padding(horizontal = 18.dp, vertical = 14.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "Notification",
                            style = MaterialTheme.typography.titleSmall.copy(
                                fontWeight = FontWeight.SemiBold,
                                fontSize = 15.sp
                            ),
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = "Allow to push the notification",
                            style = MaterialTheme.typography.bodySmall,
                            color = TextSecondary,
                            fontSize = 12.sp
                        )
                    }

                    Switch(
                        checked = contact.notificationsAllowed,
                        onCheckedChange = { allowed ->
                            viewModel.toggleContactNotification(contact, allowed)
                        },
                        colors = SwitchDefaults.colors(
                            checkedThumbColor = Color.White,
                            checkedTrackColor = PrimaryBlue,
                            uncheckedThumbColor = Color.White,
                            uncheckedTrackColor = TextTertiary.copy(alpha = 0.4f)
                        ),
                        modifier = Modifier.testTag("profile_notification_switch")
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Menu Items Card (Remarks, Dynamic, More)
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp)
                    .shadow(
                        elevation = 6.dp,
                        shape = RoundedCornerShape(22.dp),
                        spotColor = PrimaryBlue.copy(alpha = 0.12f),
                        ambientColor = PrimaryBlue.copy(alpha = 0.06f)
                    )
                    .clip(RoundedCornerShape(22.dp))
                    .background(MaterialTheme.colorScheme.surface)
                    .padding(horizontal = 16.dp, vertical = 6.dp)
            ) {
                Column {
                    ProfileMenuItemRow(
                        icon = Icons.Outlined.EditNote,
                        iconBg = PrimaryBlueSoft,
                        iconTint = PrimaryBlue,
                        label = "Remarks",
                        value = if (contact.remarks.isNotBlank()) contact.remarks else "Add note",
                        onClick = { showEditRemarksDialog = true }
                    )

                    HorizontalDivider(
                        modifier = Modifier.padding(horizontal = 8.dp),
                        color = IncomingBubbleBorder.copy(alpha = 0.6f)
                    )

                    ProfileMenuItemRow(
                        icon = Icons.Outlined.AutoAwesome,
                        iconBg = AccentPurpleSoft,
                        iconTint = AccentPurple,
                        label = "Dynamic",
                        value = "Latest moments",
                        onClick = { showDynamicDialog = true }
                    )

                    HorizontalDivider(
                        modifier = Modifier.padding(horizontal = 8.dp),
                        color = IncomingBubbleBorder.copy(alpha = 0.6f)
                    )

                    ProfileMenuItemRow(
                        icon = Icons.Outlined.FolderShared,
                        iconBg = AccentPinkSoft,
                        iconTint = AccentPink,
                        label = "Shared Media",
                        value = "24 Photos & Files",
                        onClick = { viewModel.showToast("Opening shared media library") }
                    )

                    HorizontalDivider(
                        modifier = Modifier.padding(horizontal = 8.dp),
                        color = IncomingBubbleBorder.copy(alpha = 0.6f)
                    )

                    ProfileMenuItemRow(
                        icon = Icons.Outlined.MoreHoriz,
                        iconBg = PrimaryBlueSoft,
                        iconTint = PrimaryBlueDark,
                        label = "More",
                        value = "",
                        onClick = { showMoreDialog = true }
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Action Buttons: Send Message & Large Blue "Edit" CTA
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // Secondary Action: Call
                Button(
                    onClick = { viewModel.startCall(contact, isVideo = false) },
                    shape = RoundedCornerShape(20.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = PrimaryBlueSoft),
                    modifier = Modifier
                        .weight(1f)
                        .height(52.dp)
                        .testTag("profile_call_button")
                ) {
                    Icon(
                        imageVector = Icons.Default.Call,
                        contentDescription = "Call",
                        tint = PrimaryBlue,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "Call",
                        color = PrimaryBlue,
                        fontWeight = FontWeight.SemiBold
                    )
                }

                // Primary Large Blue Button: "Edit" / "Message"
                Button(
                    onClick = {
                        viewModel.openChatWithContact(contact)
                    },
                    shape = RoundedCornerShape(20.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = PrimaryBlue),
                    modifier = Modifier
                        .weight(1.4f)
                        .height(52.dp)
                        .shadow(8.dp, RoundedCornerShape(20.dp), spotColor = PrimaryBlue.copy(alpha = 0.4f))
                        .testTag("profile_edit_button")
                ) {
                    Icon(
                        imageVector = Icons.Default.ChatBubble,
                        contentDescription = "Message",
                        tint = Color.White,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Message",
                        color = Color.White,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }
        }

        // Remarks Dialog
        if (showEditRemarksDialog) {
            EditRemarksDialog(
                currentRemarks = contact.remarks,
                onDismiss = { showEditRemarksDialog = false },
                onSave = { newRemarks ->
                    viewModel.updateContactRemarks(contact, newRemarks)
                    showEditRemarksDialog = false
                }
            )
        }

        // Dynamic Moments Dialog
        if (showDynamicDialog) {
            DynamicMomentsDialog(
                contact = contact,
                onDismiss = { showDynamicDialog = false }
            )
        }

        // More Options Dialog
        if (showMoreDialog) {
            MoreOptionsDialog(
                contact = contact,
                onDismiss = { showMoreDialog = false },
                onAction = { action ->
                    viewModel.showToast(action)
                    showMoreDialog = false
                }
            )
        }
    }
}

@Composable
private fun StatColumn(
    count: String,
    label: String
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = count,
            style = MaterialTheme.typography.titleMedium.copy(
                fontWeight = FontWeight.Bold,
                fontSize = 18.sp
            ),
            color = MaterialTheme.colorScheme.onSurface
        )
        Spacer(modifier = Modifier.height(2.dp))
        Text(
            text = label,
            style = MaterialTheme.typography.bodySmall,
            color = TextSecondary,
            fontSize = 12.sp
        )
    }
}

@Composable
private fun ProfileMenuItemRow(
    icon: ImageVector,
    iconBg: Color,
    iconTint: Color,
    label: String,
    value: String = "",
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .padding(vertical = 12.dp, horizontal = 4.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(38.dp)
                    .clip(CircleShape)
                    .background(iconBg),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = label,
                    tint = iconTint,
                    modifier = Modifier.size(20.dp)
                )
            }

            Text(
                text = label,
                style = MaterialTheme.typography.bodyLarge.copy(
                    fontWeight = FontWeight.Medium,
                    fontSize = 14.5.sp
                ),
                color = MaterialTheme.colorScheme.onSurface
            )
        }

        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            if (value.isNotBlank()) {
                Text(
                    text = value,
                    style = MaterialTheme.typography.bodySmall,
                    color = TextTertiary,
                    fontSize = 12.sp
                )
            }

            Icon(
                imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
                contentDescription = "Open",
                tint = TextTertiary,
                modifier = Modifier.size(20.dp)
            )
        }
    }
}

@Composable
private fun EditRemarksDialog(
    currentRemarks: String,
    onDismiss: () -> Unit,
    onSave: (String) -> Unit
) {
    var remarks by remember { mutableStateOf(currentRemarks) }

    Dialog(onDismissRequest = onDismiss) {
        Card(
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Column(
                modifier = Modifier.padding(22.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "Edit Remarks",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.onSurface
                )

                Spacer(modifier = Modifier.height(16.dp))

                OutlinedTextField(
                    value = remarks,
                    onValueChange = { remarks = it },
                    label = { Text("Remarks / Tag") },
                    singleLine = true,
                    shape = RoundedCornerShape(16.dp),
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(20.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End
                ) {
                    TextButton(onClick = onDismiss) {
                        Text("Cancel", color = TextSecondary)
                    }

                    Spacer(modifier = Modifier.width(8.dp))

                    Button(
                        onClick = { onSave(remarks.trim()) },
                        colors = ButtonDefaults.buttonColors(containerColor = PrimaryBlue),
                        shape = RoundedCornerShape(18.dp)
                    ) {
                        Text("Save", color = Color.White)
                    }
                }
            }
        }
    }
}

@Composable
private fun DynamicMomentsDialog(
    contact: Contact,
    onDismiss: () -> Unit
) {
    Dialog(onDismissRequest = onDismiss) {
        Card(
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Column(
                modifier = Modifier.padding(22.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    AvatarView(
                        name = contact.name,
                        avatarResName = contact.avatarResName,
                        avatarColorHex = contact.avatarColorHex,
                        size = 40.dp
                    )
                    Column {
                        Text(
                            text = "${contact.name}'s Moments",
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.SemiBold
                        )
                        Text(
                            text = "Updated today",
                            style = MaterialTheme.typography.bodySmall,
                            color = TextTertiary
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(16.dp))
                        .background(BackgroundSecondary)
                        .padding(14.dp)
                ) {
                    Text(
                        text = contact.dynamicMoments,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurface,
                        lineHeight = 20.sp
                    )
                }

                Spacer(modifier = Modifier.height(18.dp))

                Button(
                    onClick = onDismiss,
                    colors = ButtonDefaults.buttonColors(containerColor = PrimaryBlue),
                    shape = RoundedCornerShape(18.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Close", color = Color.White)
                }
            }
        }
    }
}

@Composable
private fun MoreOptionsDialog(
    contact: Contact,
    onDismiss: () -> Unit,
    onAction: (String) -> Unit
) {
    Dialog(onDismissRequest = onDismiss) {
        Card(
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Column(
                modifier = Modifier.padding(20.dp)
            ) {
                Text(
                    text = "Contact Options",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.onSurface
                )

                Spacer(modifier = Modifier.height(14.dp))

                TextButton(
                    onClick = { onAction("Starred ${contact.name}") },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(Icons.Outlined.Star, contentDescription = null, tint = PrimaryBlue)
                        Spacer(modifier = Modifier.width(12.dp))
                        Text("Add to Favorites", color = MaterialTheme.colorScheme.onSurface)
                    }
                }

                TextButton(
                    onClick = { onAction("Shared Contact ${contact.name}") },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(Icons.Outlined.Share, contentDescription = null, tint = PrimaryBlue)
                        Spacer(modifier = Modifier.width(12.dp))
                        Text("Share Contact Card", color = MaterialTheme.colorScheme.onSurface)
                    }
                }

                TextButton(
                    onClick = { onAction("Blocked ${contact.name}") },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(Icons.Outlined.Block, contentDescription = null, tint = AccentPink)
                        Spacer(modifier = Modifier.width(12.dp))
                        Text("Block Contact", color = AccentPink)
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                Button(
                    onClick = onDismiss,
                    colors = ButtonDefaults.buttonColors(containerColor = BackgroundSecondary),
                    shape = RoundedCornerShape(16.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Cancel", color = TextSecondary)
                }
            }
        }
    }
}
