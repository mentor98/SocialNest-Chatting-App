package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.animation.*
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.screens.*
import com.example.ui.theme.ChatTheme
import com.example.ui.theme.PrimaryBlue
import com.example.viewmodel.ChatViewModel
import com.example.viewmodel.Screen
import kotlinx.coroutines.delay

class MainActivity : ComponentActivity() {

    private val viewModel: ChatViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            val uiState by viewModel.uiState.collectAsState()

            ChatTheme(darkTheme = uiState.isDarkMode) {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    ChatApp(viewModel = viewModel)
                }
            }
        }
    }
}

@Composable
fun ChatApp(viewModel: ChatViewModel) {
    val uiState by viewModel.uiState.collectAsState()

    // Handle back button presses
    BackHandler(enabled = uiState.currentScreen !is Screen.Conversations && uiState.currentScreen !is Screen.Auth) {
        viewModel.navigateBack()
    }

    Box(modifier = Modifier.fillMaxSize()) {
        AnimatedContent(
            targetState = uiState.currentScreen,
            transitionSpec = {
                fadeIn(animationSpec = tween(250)) togetherWith fadeOut(animationSpec = tween(200))
            },
            label = "screen_transition"
        ) { screen ->
            when (screen) {
                is Screen.Auth -> AuthScreen(viewModel = viewModel)
                is Screen.Conversations -> ConversationsScreen(viewModel = viewModel)
                is Screen.Contacts -> ContactsScreen(viewModel = viewModel)
                is Screen.Calls -> CallsScreen(viewModel = viewModel)
                is Screen.Chat -> ChatScreen(viewModel = viewModel)
                is Screen.ContactProfile -> ContactProfileScreen(viewModel = viewModel)
                is Screen.UserProfile -> UserProfileScreen(viewModel = viewModel)
                is Screen.Notifications -> NotificationsScreen(viewModel = viewModel)
                is Screen.Search -> SearchScreen(viewModel = viewModel)
                is Screen.ActiveCall -> ActiveCallScreen(viewModel = viewModel)
                else -> ConversationsScreen(viewModel = viewModel)
            }
        }

        // Floating Toast Notification
        uiState.toastMessage?.let { msg ->
            LaunchedEffect(msg) {
                delay(2400)
                viewModel.clearToast()
            }

            Box(
                modifier = Modifier
                    .align(Alignment.TopCenter)
                    .statusBarsPadding()
                    .padding(top = 16.dp, start = 24.dp, end = 24.dp)
            ) {
                Box(
                    modifier = Modifier
                        .shadow(10.dp, RoundedCornerShape(20.dp), spotColor = PrimaryBlue.copy(alpha = 0.2f))
                        .clip(RoundedCornerShape(20.dp))
                        .background(MaterialTheme.colorScheme.surface)
                        .padding(horizontal = 18.dp, vertical = 10.dp)
                ) {
                    Text(
                        text = msg,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurface,
                        fontSize = 13.5.sp
                    )
                }
            }
        }
    }
}
