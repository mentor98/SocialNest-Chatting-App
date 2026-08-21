package com.example.ui.components

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.Send
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.AccentPink
import com.example.ui.theme.PrimaryBlue

@Composable
fun VoiceRecorderBar(
    durationSeconds: Int,
    onCancel: () -> Unit,
    onFinish: () -> Unit,
    modifier: Modifier = Modifier
) {
    val infiniteTransition = rememberInfiniteTransition(label = "pulse")
    val pulseScale by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = 1.25f,
        animationSpec = infiniteRepeatable(
            animation = tween(600, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "scale"
    )

    Row(
        modifier = modifier
            .fillMaxWidth()
            .shadow(
                elevation = 10.dp,
                shape = RoundedCornerShape(26.dp),
                spotColor = AccentPink.copy(alpha = 0.2f)
            )
            .clip(RoundedCornerShape(26.dp))
            .background(MaterialTheme.colorScheme.surface)
            .padding(horizontal = 16.dp, vertical = 10.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        // Cancel Button
        Box(
            modifier = Modifier
                .size(38.dp)
                .clip(CircleShape)
                .background(Color(0xFFFFEEEE))
                .clickable { onCancel() },
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Default.Close,
                contentDescription = "Cancel Recording",
                tint = AccentPink,
                modifier = Modifier.size(20.dp)
            )
        }

        // Live recording indicator & timer
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(12.dp)
                    .scale(pulseScale)
                    .clip(CircleShape)
                    .background(AccentPink)
            )

            Text(
                text = "Recording 0:${durationSeconds.toString().padStart(2, '0')}",
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.SemiBold,
                color = AccentPink,
                fontSize = 14.sp
            )

            // Animated waveform placeholder bars
            Row(
                horizontalArrangement = Arrangement.spacedBy(3.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                val heights = listOf(14.dp, 20.dp, 8.dp, 24.dp, 16.dp, 10.dp)
                heights.forEach { h ->
                    Box(
                        modifier = Modifier
                            .width(3.dp)
                            .height(h)
                            .clip(RoundedCornerShape(2.dp))
                            .background(PrimaryBlue.copy(alpha = 0.6f))
                    )
                }
            }
        }

        // Send Voice Message Button
        Box(
            modifier = Modifier
                .size(42.dp)
                .clip(CircleShape)
                .background(PrimaryBlue)
                .clickable { onFinish() },
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Default.Send,
                contentDescription = "Send Voice",
                tint = Color.White,
                modifier = Modifier.size(20.dp)
            )
        }
    }
}
