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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.components.AvatarView
import com.example.ui.components.BottomNavigationDock
import com.example.ui.theme.*
import com.example.viewmodel.ChatViewModel

@Composable
fun UserProfileScreen(
    viewModel: ChatViewModel,
    modifier: Modifier = Modifier
) {
    val uiState by viewModel.uiState.collectAsState()
    val user = uiState.currentUser

    var name by remember(user?.name) { mutableStateOf(user?.name ?: "Alex Rivera") }
    var bio by remember(user?.bio) { mutableStateOf(user?.bio ?: "Designing modern interfaces and connecting with friends.") }
    var status by remember(user?.status) { mutableStateOf(user?.status ?: "Available for chat") }
    var phone by remember(user?.phone) { mutableStateOf(user?.phone ?: "+1 (555) 382-9910") }
    var email by remember(user?.email) { mutableStateOf(user?.email ?: "alex.rivera@example.com") }

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
                .padding(bottom = 90.dp)
        ) {
            // Top Bar
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
                    text = "My Profile & Settings",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.onSurface
                )

                TextButton(
                    onClick = {
                        viewModel.updateCurrentUser(name, bio, status, phone, email)
                    },
                    modifier = Modifier.testTag("save_profile_button")
                ) {
                    Text("Save", color = PrimaryBlue, fontWeight = FontWeight.Bold)
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Avatar & Name Card
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Box(contentAlignment = Alignment.BottomEnd) {
                    AvatarView(
                        name = name,
                        avatarResName = "ic_chat_logo",
                        avatarColorHex = 0xFF568CF5,
                        size = 88.dp,
                        isOnline = true
                    )
                    Box(
                        modifier = Modifier
                            .size(28.dp)
                            .clip(CircleShape)
                            .background(PrimaryBlue)
                            .clickable { viewModel.showToast("Change avatar") },
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.CameraAlt,
                            contentDescription = "Edit photo",
                            tint = Color.White,
                            modifier = Modifier.size(16.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                Text(
                    text = name,
                    style = MaterialTheme.typography.titleLarge.copy(
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 20.sp
                    ),
                    color = MaterialTheme.colorScheme.onSurface
                )

                Text(
                    text = "@${user?.username ?: "alex_rivera"}",
                    style = MaterialTheme.typography.bodySmall,
                    color = TextSecondary
                )
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Edit Profile Form Card
            Card(
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp)
                    .shadow(6.dp, RoundedCornerShape(24.dp), spotColor = PrimaryBlue.copy(alpha = 0.1f))
            ) {
                Column(modifier = Modifier.padding(18.dp)) {
                    Text(
                        text = "Personal Information",
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.SemiBold,
                        color = PrimaryBlue
                    )

                    Spacer(modifier = Modifier.height(14.dp))

                    OutlinedTextField(
                        value = name,
                        onValueChange = { name = it },
                        label = { Text("Display Name") },
                        singleLine = true,
                        shape = RoundedCornerShape(16.dp),
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    OutlinedTextField(
                        value = status,
                        onValueChange = { status = it },
                        label = { Text("Status") },
                        singleLine = true,
                        shape = RoundedCornerShape(16.dp),
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    OutlinedTextField(
                        value = bio,
                        onValueChange = { bio = it },
                        label = { Text("Bio") },
                        maxLines = 3,
                        shape = RoundedCornerShape(16.dp),
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    OutlinedTextField(
                        value = phone,
                        onValueChange = { phone = it },
                        label = { Text("Phone") },
                        singleLine = true,
                        shape = RoundedCornerShape(16.dp),
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    OutlinedTextField(
                        value = email,
                        onValueChange = { email = it },
                        label = { Text("Email") },
                        singleLine = true,
                        shape = RoundedCornerShape(16.dp),
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // App Preferences Card
            Card(
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp)
                    .shadow(6.dp, RoundedCornerShape(24.dp), spotColor = PrimaryBlue.copy(alpha = 0.1f))
            ) {
                Column(modifier = Modifier.padding(18.dp)) {
                    Text(
                        text = "Preferences",
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.SemiBold,
                        color = PrimaryBlue
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    // Dark Theme Toggle
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 8.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = if (uiState.isDarkMode) Icons.Default.DarkMode else Icons.Default.LightMode,
                                contentDescription = null,
                                tint = PrimaryBlue
                            )
                            Spacer(modifier = Modifier.width(12.dp))
                            Text(
                                text = "Dark Mode Theme",
                                style = MaterialTheme.typography.bodyLarge,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        }

                        Switch(
                            checked = uiState.isDarkMode,
                            onCheckedChange = { viewModel.toggleDarkMode() },
                            colors = SwitchDefaults.colors(
                                checkedThumbColor = Color.White,
                                checkedTrackColor = PrimaryBlue
                            ),
                            modifier = Modifier.testTag("dark_mode_switch")
                        )
                    }

                    HorizontalDivider(modifier = Modifier.padding(vertical = 4.dp), color = IncomingBubbleBorder)

                    // Logout Button
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { viewModel.logout() }
                            .padding(vertical = 12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.Logout,
                            contentDescription = "Logout",
                            tint = AccentPink
                        )
                        Spacer(modifier = Modifier.width(12.dp))
                        Text(
                            text = "Log Out",
                            style = MaterialTheme.typography.bodyLarge,
                            color = AccentPink,
                            fontWeight = FontWeight.Medium
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
