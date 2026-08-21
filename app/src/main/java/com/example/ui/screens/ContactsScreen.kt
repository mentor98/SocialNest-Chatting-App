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
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Menu
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
import androidx.compose.ui.window.Dialog
import com.example.model.Contact
import com.example.ui.components.AlphabetIndex
import com.example.ui.components.AvatarView
import com.example.ui.components.BottomNavigationDock
import com.example.ui.components.SearchBar
import com.example.ui.theme.*
import com.example.viewmodel.ChatViewModel
import com.example.viewmodel.NavTab
import com.example.viewmodel.Screen
import kotlinx.coroutines.launch

@Composable
fun ContactsScreen(
    viewModel: ChatViewModel,
    modifier: Modifier = Modifier
) {
    val uiState by viewModel.uiState.collectAsState()
    val listState = rememberLazyListState()
    val coroutineScope = rememberCoroutineScope()

    var showAddContactDialog by remember { mutableStateOf(false) }
    var selectedAlphabetLetter by remember { mutableStateOf<String?>(null) }

    // Group contacts by initial letter (M, N, A, B, etc.)
    val groupedContacts = remember(uiState.searchResults) {
        uiState.searchResults
            .groupBy { it.initialLetter.uppercase() }
            .toSortedMap()
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
            // Top Navigation Bar
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 12.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                // Menu / Drawer button
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .clip(CircleShape)
                        .clickable { viewModel.navigateTo(Screen.UserProfile) }
                        .testTag("contacts_menu_button"),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Menu,
                        contentDescription = "Menu",
                        tint = MaterialTheme.colorScheme.onSurface,
                        modifier = Modifier.size(24.dp)
                    )
                }

                // Title "Contact"
                Text(
                    text = "Contact",
                    style = MaterialTheme.typography.titleLarge.copy(
                        fontWeight = FontWeight.Medium,
                        fontSize = 22.sp,
                        letterSpacing = (-0.2).sp
                    ),
                    color = MaterialTheme.colorScheme.onSurface
                )

                // Add Contact (+) Button
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .clip(CircleShape)
                        .background(PrimaryBlue)
                        .clickable { showAddContactDialog = true }
                        .testTag("add_contact_button"),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = "Add Contact",
                        tint = Color.White,
                        modifier = Modifier.size(22.dp)
                    )
                }
            }

            // Search Bar
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 6.dp)
            ) {
                SearchBar(
                    query = uiState.searchQuery,
                    onQueryChange = { viewModel.updateSearchQuery(it) },
                    placeholder = "Search for contacts",
                    onMicClick = {
                        viewModel.showToast("Listening for voice search...")
                    }
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Main Contacts List with Alphabetical Index
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
            ) {
                if (groupedContacts.isEmpty()) {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "No contacts found",
                            style = MaterialTheme.typography.bodyMedium,
                            color = TextTertiary
                        )
                    }
                } else {
                    LazyColumn(
                        state = listState,
                        contentPadding = PaddingValues(start = 20.dp, end = 36.dp, top = 6.dp, bottom = 80.dp),
                        modifier = Modifier
                            .fillMaxSize()
                            .testTag("contacts_lazy_list")
                    ) {
                        groupedContacts.forEach { (letter, contacts) ->
                            // Section header
                            item(key = "header_$letter") {
                                Text(
                                    text = letter,
                                    style = MaterialTheme.typography.titleMedium.copy(
                                        fontWeight = FontWeight.SemiBold,
                                        fontSize = 17.sp
                                    ),
                                    color = TextSecondary,
                                    modifier = Modifier.padding(vertical = 8.dp, horizontal = 4.dp)
                                )
                            }

                            // Contact items in section
                            items(
                                items = contacts,
                                key = { it.id }
                            ) { contact ->
                                ContactItemRow(
                                    contact = contact,
                                    onClick = {
                                        viewModel.openChatWithContact(contact)
                                    },
                                    onAvatarClick = {
                                        viewModel.navigateTo(Screen.ContactProfile(contact.id))
                                    }
                                )
                            }
                        }
                    }
                }

                // Alphabetical Quick Scroll Index on right edge
                AlphabetIndex(
                    selectedLetter = selectedAlphabetLetter,
                    onLetterSelected = { letter ->
                        selectedAlphabetLetter = letter
                        val sectionKeys = groupedContacts.keys.toList()
                        val targetIndex = sectionKeys.indexOfFirst { it.equals(letter, ignoreCase = true) }
                        if (targetIndex >= 0) {
                            coroutineScope.launch {
                                // Calculate approximate item index
                                var itemPos = 0
                                for (i in 0 until targetIndex) {
                                    itemPos += 1 + (groupedContacts[sectionKeys[i]]?.size ?: 0)
                                }
                                listState.animateScrollToItem(itemPos)
                            }
                        }
                    },
                    modifier = Modifier
                        .align(Alignment.CenterEnd)
                        .padding(end = 4.dp)
                )
            }
        }

        // Floating Bottom Navigation Bar
        BottomNavigationDock(
            activeTab = uiState.activeNavTab,
            onTabSelected = { tab ->
                viewModel.setActiveNavTab(tab)
            },
            unreadNotificationsCount = uiState.notifications.count { !it.isRead },
            modifier = Modifier.align(Alignment.BottomCenter)
        )

        // Add Contact Dialog
        if (showAddContactDialog) {
            AddContactDialog(
                onDismiss = { showAddContactDialog = false },
                onAdd = { name, status, phone ->
                    viewModel.addNewContact(name, status, phone)
                    showAddContactDialog = false
                }
            )
        }
    }
}

@Composable
fun ContactItemRow(
    contact: Contact,
    onClick: () -> Unit,
    onAvatarClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp)
            .shadow(
                elevation = 3.dp,
                shape = RoundedCornerShape(18.dp),
                spotColor = PrimaryBlue.copy(alpha = 0.08f),
                ambientColor = PrimaryBlue.copy(alpha = 0.04f)
            )
            .clip(RoundedCornerShape(18.dp))
            .background(MaterialTheme.colorScheme.surface)
            .clickable { onClick() }
            .padding(horizontal = 14.dp, vertical = 12.dp)
            .testTag("contact_item_${contact.name.lowercase()}")
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth()
        ) {
            // Avatar (clicking opens profile)
            AvatarView(
                name = contact.name,
                avatarResName = contact.avatarResName,
                avatarColorHex = contact.avatarColorHex,
                size = 48.dp,
                isOnline = contact.isOnline,
                showOnlineIndicator = true,
                modifier = Modifier
                    .clickable(
                        interactionSource = remember { MutableInteractionSource() },
                        indication = null
                    ) { onAvatarClick() }
            )

            Spacer(modifier = Modifier.width(14.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = contact.name,
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.Medium,
                        fontSize = 16.sp
                    ),
                    color = MaterialTheme.colorScheme.onSurface
                )

                Spacer(modifier = Modifier.height(2.dp))

                Text(
                    text = contact.statusMessage,
                    style = MaterialTheme.typography.bodyMedium.copy(
                        fontSize = 12.5.sp
                    ),
                    color = TextSecondary,
                    maxLines = 1
                )
            }
        }
    }
}

@Composable
private fun AddContactDialog(
    onDismiss: () -> Unit,
    onAdd: (String, String, String) -> Unit
) {
    var name by remember { mutableStateOf("") }
    var status by remember { mutableStateOf("") }
    var phone by remember { mutableStateOf("") }

    Dialog(onDismissRequest = onDismiss) {
        Card(
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
                .shadow(12.dp, RoundedCornerShape(24.dp))
        ) {
            Column(
                modifier = Modifier.padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "Add New Contact",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.onSurface
                )

                Spacer(modifier = Modifier.height(18.dp))

                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("Contact Name") },
                    singleLine = true,
                    shape = RoundedCornerShape(16.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("add_contact_name_input")
                )

                Spacer(modifier = Modifier.height(12.dp))

                OutlinedTextField(
                    value = status,
                    onValueChange = { status = it },
                    label = { Text("Status / Quote") },
                    singleLine = true,
                    shape = RoundedCornerShape(16.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("add_contact_status_input")
                )

                Spacer(modifier = Modifier.height(12.dp))

                OutlinedTextField(
                    value = phone,
                    onValueChange = { phone = it },
                    label = { Text("Phone Number") },
                    singleLine = true,
                    shape = RoundedCornerShape(16.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("add_contact_phone_input")
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
                        onClick = {
                            if (name.isNotBlank()) {
                                onAdd(name, status, phone)
                            }
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = PrimaryBlue),
                        shape = RoundedCornerShape(18.dp),
                        modifier = Modifier.testTag("save_contact_button")
                    ) {
                        Text("Save", color = Color.White)
                    }
                }
            }
        }
    }
}
