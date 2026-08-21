package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.Message
import com.example.ui.components.AvatarView
import com.example.ui.components.SearchBar
import com.example.ui.theme.*
import com.example.viewmodel.ChatViewModel
import com.example.viewmodel.Screen
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun SearchScreen(
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
            // Header with search
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically
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

                Spacer(modifier = Modifier.width(6.dp))

                SearchBar(
                    query = uiState.searchQuery,
                    onQueryChange = { viewModel.updateSearchQuery(it) },
                    placeholder = "Search contacts or messages...",
                    modifier = Modifier.weight(1f)
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Results List
            LazyColumn(
                contentPadding = PaddingValues(horizontal = 16.dp, vertical = 6.dp),
                modifier = Modifier.fillMaxSize()
            ) {
                if (uiState.searchResults.isNotEmpty()) {
                    item {
                        Text(
                            text = "Contacts (${uiState.searchResults.size})",
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.SemiBold,
                            color = TextSecondary,
                            modifier = Modifier.padding(vertical = 8.dp)
                        )
                    }

                    items(uiState.searchResults) { contact ->
                        ContactItemRow(
                            contact = contact,
                            onClick = { viewModel.openChatWithContact(contact) },
                            onAvatarClick = { viewModel.navigateTo(Screen.ContactProfile(contact.id)) }
                        )
                    }
                }

                if (uiState.messageSearchResults.isNotEmpty()) {
                    item {
                        Text(
                            text = "Messages (${uiState.messageSearchResults.size})",
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.SemiBold,
                            color = TextSecondary,
                            modifier = Modifier.padding(top = 16.dp, bottom = 8.dp)
                        )
                    }

                    items(uiState.messageSearchResults) { msg ->
                        MessageSearchResultCard(
                            message = msg,
                            onClick = {
                                val contact = uiState.contacts.find { it.id == msg.senderId }
                                    ?: uiState.contacts.first()
                                viewModel.openChatWithContact(contact)
                            }
                        )
                    }
                }

                if (uiState.searchQuery.isNotBlank() && uiState.searchResults.isEmpty() && uiState.messageSearchResults.isEmpty()) {
                    item {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 40.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "No results found for \"${uiState.searchQuery}\"",
                                style = MaterialTheme.typography.bodyMedium,
                                color = TextTertiary
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun MessageSearchResultCard(
    message: Message,
    onClick: () -> Unit
) {
    val timeFormat = SimpleDateFormat("MMM d, h:mm a", Locale.getDefault())
    val formatted = timeFormat.format(Date(message.timestamp))

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp)
            .shadow(2.dp, RoundedCornerShape(16.dp))
            .clip(RoundedCornerShape(16.dp))
            .background(MaterialTheme.colorScheme.surface)
            .clickable { onClick() }
            .padding(14.dp)
    ) {
        Column {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = if (message.isOutgoing) "You" else "Contact",
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.SemiBold,
                    color = PrimaryBlue
                )
                Text(
                    text = formatted,
                    style = MaterialTheme.typography.bodySmall,
                    color = TextTertiary,
                    fontSize = 11.sp
                )
            }
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = message.text,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurface,
                fontSize = 13.5.sp
            )
        }
    }
}
