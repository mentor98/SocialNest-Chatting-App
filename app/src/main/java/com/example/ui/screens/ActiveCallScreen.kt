package com.example.ui.screens

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.components.AvatarView
import com.example.ui.theme.*
import com.example.viewmodel.ChatViewModel

@Composable
fun ActiveCallScreen(
    viewModel: ChatViewModel,
    modifier: Modifier = Modifier
) {
    val uiState by viewModel.uiState.collectAsState()
    val callState = uiState.activeCallState
    val contact = callState.contact ?: uiState.selectedContact

    val infiniteTransition = rememberInfiniteTransition(label = "ripple")
    val rippleScale by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = 1.35f,
        animationSpec = infiniteRepeatable(
            animation = tween(1400, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "ripple_scale"
    )
    val rippleAlpha by infiniteTransition.animateFloat(
        initialValue = 0.5f,
        targetValue = 0f,
        animationSpec = infiniteRepeatable(
            animation = tween(1400, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "ripple_alpha"
    )

    val minutes = callState.durationSeconds / 60
    val seconds = callState.durationSeconds % 60
    val durationFormatted = String.format("%02d:%02d", minutes, seconds)

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(
                        PrimaryBlueDark,
                        Color(0xFF1E293B),
                        Color(0xFF0F172A)
                    )
                )
            )
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
                .navigationBarsPadding()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            // Header Info
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.padding(top = 28.dp)
            ) {
                Text(
                    text = if (callState.isVideo) "Video Call" else "Voice Call",
                    style = MaterialTheme.typography.titleMedium,
                    color = Color.White.copy(alpha = 0.7f)
                )

                Spacer(modifier = Modifier.height(10.dp))

                Text(
                    text = contact?.name ?: "Contact",
                    style = MaterialTheme.typography.titleLarge.copy(
                        fontSize = 28.sp,
                        fontWeight = FontWeight.Bold
                    ),
                    color = Color.White
                )

                Spacer(modifier = Modifier.height(6.dp))

                Text(
                    text = durationFormatted,
                    style = MaterialTheme.typography.bodyLarge.copy(
                        fontSize = 18.sp,
                        fontWeight = FontWeight.SemiBold
                    ),
                    color = AccentGreen
                )
            }

            // Center Call Avatar / Video View
            Box(
                modifier = Modifier.size(240.dp),
                contentAlignment = Alignment.Center
            ) {
                // Pulsing ring
                Box(
                    modifier = Modifier
                        .size(190.dp)
                        .scale(rippleScale)
                        .clip(CircleShape)
                        .background(PrimaryBlue.copy(alpha = rippleAlpha))
                )

                // Contact Avatar
                AvatarView(
                    name = contact?.name ?: "User",
                    avatarResName = contact?.avatarResName ?: "avatar_martin",
                    avatarColorHex = contact?.avatarColorHex ?: 0xFF568CF5,
                    size = 140.dp,
                    isOnline = true,
                    showOnlineIndicator = false,
                    modifier = Modifier.shadow(16.dp, CircleShape)
                )
            }

            // Call Controls Card
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(24.dp)
            ) {
                Row(
                    modifier = Modifier
                        .shadow(12.dp, RoundedCornerShape(32.dp))
                        .clip(RoundedCornerShape(32.dp))
                        .background(Color.White.copy(alpha = 0.12f))
                        .padding(horizontal = 16.dp, vertical = 12.dp),
                    horizontalArrangement = Arrangement.spacedBy(20.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    CallControlButton(
                        icon = if (callState.isMuted) Icons.Default.MicOff else Icons.Default.Mic,
                        isActive = callState.isMuted,
                        label = "Mute",
                        onClick = { viewModel.toggleMute() }
                    )

                    CallControlButton(
                        icon = if (callState.isSpeakerOn) Icons.Default.VolumeUp else Icons.Default.VolumeOff,
                        isActive = callState.isSpeakerOn,
                        label = "Speaker",
                        onClick = { viewModel.toggleSpeaker() }
                    )

                    if (callState.isVideo) {
                        CallControlButton(
                            icon = if (callState.isVideoPaused) Icons.Default.VideocamOff else Icons.Default.Videocam,
                            isActive = callState.isVideoPaused,
                            label = "Video",
                            onClick = { viewModel.toggleVideoPause() }
                        )
                    }
                }

                // End Call Button
                Box(
                    modifier = Modifier
                        .size(68.dp)
                        .shadow(14.dp, CircleShape, spotColor = AccentPink.copy(alpha = 0.7f))
                        .clip(CircleShape)
                        .background(AccentPink)
                        .clickable { viewModel.endCall() }
                        .testTag("end_call_button"),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.CallEnd,
                        contentDescription = "End Call",
                        tint = Color.White,
                        modifier = Modifier.size(32.dp)
                    )
                }

                Spacer(modifier = Modifier.height(10.dp))
            }
        }
    }
}

@Composable
private fun CallControlButton(
    icon: ImageVector,
    isActive: Boolean,
    label: String,
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .size(52.dp)
            .clip(CircleShape)
            .background(if (isActive) PrimaryBlue else Color.White.copy(alpha = 0.15f))
            .clickable { onClick() },
        contentAlignment = Alignment.Center
    ) {
        Icon(
            imageVector = icon,
            contentDescription = label,
            tint = Color.White,
            modifier = Modifier.size(24.dp)
        )
    }
}
