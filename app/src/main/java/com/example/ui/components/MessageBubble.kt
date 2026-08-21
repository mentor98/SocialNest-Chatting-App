package com.example.ui.components

import androidx.compose.animation.core.*
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Done
import androidx.compose.material.icons.filled.DoneAll
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.outlined.InsertDriveFile
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.Message
import com.example.model.MessageStatus
import com.example.model.MessageType
import com.example.ui.theme.*
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun MessageBubble(
    message: Message,
    onLongClick: () -> Unit = {},
    onClick: () -> Unit = {},
    onReactionClick: (String) -> Unit = {},
    modifier: Modifier = Modifier
) {
    val isOutgoing = message.isOutgoing
    val timeFormat = remember { SimpleDateFormat("h:mm a", Locale.getDefault()) }
    val formattedTime = remember(message.timestamp) { timeFormat.format(Date(message.timestamp)) }

    // Waveform amplitudes for voice notes
    val waveformValues = remember(message.voiceWaveform) {
        if (message.voiceWaveform.isNotBlank()) {
            message.voiceWaveform.split(",").mapNotNull { it.trim().toFloatOrNull() }
        } else {
            listOf(0.3f, 0.6f, 0.9f, 0.4f, 0.7f, 0.8f, 0.5f, 0.3f, 0.8f, 0.6f, 0.4f, 0.7f)
        }
    }

    var isVoicePlaying by remember { mutableStateOf(false) }

    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        horizontalAlignment = if (isOutgoing) Alignment.End else Alignment.Start
    ) {
        val bubbleShape = if (isOutgoing) {
            RoundedCornerShape(topStart = 20.dp, topEnd = 4.dp, bottomStart = 20.dp, bottomEnd = 20.dp)
        } else {
            RoundedCornerShape(topStart = 4.dp, topEnd = 20.dp, bottomStart = 20.dp, bottomEnd = 20.dp)
        }

        Box(
            modifier = Modifier
                .widthIn(max = 290.dp)
                .shadow(
                    elevation = if (isOutgoing) 6.dp else 4.dp,
                    shape = bubbleShape,
                    spotColor = if (isOutgoing) PrimaryBlue.copy(alpha = 0.22f) else Color(0x12568CF5),
                    ambientColor = Color(0x0A000000)
                )
                .clip(bubbleShape)
                .background(
                    if (isOutgoing) OutgoingBubble else MaterialTheme.colorScheme.surface
                )
                .combinedClickable(
                    onClick = onClick,
                    onLongClick = onLongClick
                )
                .padding(horizontal = 16.dp, vertical = 12.dp)
        ) {
            Column {
                // Reply-to preview if any
                if (!message.replyToText.isNullOrBlank()) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 6.dp)
                            .clip(RoundedCornerShape(8.dp))
                            .background(
                                if (isOutgoing) Color.White.copy(alpha = 0.2f)
                                else BackgroundSecondary
                            )
                            .padding(horizontal = 8.dp, vertical = 4.dp)
                    ) {
                        Text(
                            text = message.replyToText,
                            fontSize = 11.sp,
                            maxLines = 1,
                            color = if (isOutgoing) Color.White.copy(alpha = 0.9f) else TextSecondary
                        )
                    }
                }

                // Main message content based on type
                when (message.type) {
                    MessageType.TEXT -> {
                        Text(
                            text = message.text,
                            style = MaterialTheme.typography.bodyLarge.copy(
                                color = if (isOutgoing) Color.White else MaterialTheme.colorScheme.onSurface,
                                fontSize = 14.sp,
                                lineHeight = 21.sp,
                                fontWeight = if (isOutgoing) FontWeight.Normal else FontWeight.Normal
                            )
                        )
                    }
                    MessageType.VOICE -> {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            modifier = Modifier.padding(vertical = 2.dp)
                        ) {
                            // Play / Pause Circle
                            Box(
                                modifier = Modifier
                                    .size(36.dp)
                                    .clip(CircleShape)
                                    .background(
                                        if (isOutgoing) Color.White.copy(alpha = 0.25f)
                                        else PrimaryBlueSoft
                                    ),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = if (isVoicePlaying) Icons.Default.Pause else Icons.Default.PlayArrow,
                                    contentDescription = "Play",
                                    tint = if (isOutgoing) Color.White else PrimaryBlue,
                                    modifier = Modifier
                                        .size(20.dp)
                                        .combinedClickable(
                                            onClick = { isVoicePlaying = !isVoicePlaying }
                                        )
                                )
                            }

                            // Waveform bars
                            Row(
                                modifier = Modifier
                                    .weight(1f)
                                    .height(26.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(3.dp)
                            ) {
                                waveformValues.forEachIndexed { index, amp ->
                                    val barHeight = (amp * 22).coerceIn(4f, 22f).dp
                                    val barColor = if (isOutgoing) {
                                        if (isVoicePlaying && index < 6) Color.White else Color.White.copy(alpha = 0.55f)
                                    } else {
                                        if (isVoicePlaying && index < 6) PrimaryBlue else PrimaryBlueLight
                                    }
                                    Box(
                                        modifier = Modifier
                                            .width(3.dp)
                                            .height(barHeight)
                                            .clip(RoundedCornerShape(2.dp))
                                            .background(barColor)
                                    )
                                }
                            }

                            // Duration
                            val durationSec = if (message.voiceDurationSeconds > 0) message.voiceDurationSeconds else 12
                            Text(
                                text = "0:${durationSec.toString().padStart(2, '0')}",
                                fontSize = 11.sp,
                                color = if (isOutgoing) Color.White.copy(alpha = 0.85f) else TextSecondary,
                                fontWeight = FontWeight.Medium
                            )
                        }
                    }
                    MessageType.FILE -> {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(38.dp)
                                    .clip(RoundedCornerShape(10.dp))
                                    .background(
                                        if (isOutgoing) Color.White.copy(alpha = 0.2f)
                                        else PrimaryBlueSoft
                                    ),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Outlined.InsertDriveFile,
                                    contentDescription = "File",
                                    tint = if (isOutgoing) Color.White else PrimaryBlue,
                                    modifier = Modifier.size(22.dp)
                                )
                            }
                            Column {
                                Text(
                                    text = if (message.attachmentName.isNotBlank()) message.attachmentName else "Document.pdf",
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Medium,
                                    color = if (isOutgoing) Color.White else MaterialTheme.colorScheme.onSurface
                                )
                                Text(
                                    text = if (message.attachmentSize.isNotBlank()) message.attachmentSize else "2.4 MB",
                                    fontSize = 10.sp,
                                    color = if (isOutgoing) Color.White.copy(alpha = 0.75f) else TextTertiary
                                )
                            }
                        }
                    }
                    else -> {
                        Text(
                            text = message.text,
                            style = MaterialTheme.typography.bodyLarge.copy(
                                color = if (isOutgoing) Color.White else MaterialTheme.colorScheme.onSurface,
                                fontSize = 14.sp
                            )
                        )
                    }
                }
            }
        }

        // Timestamp & status below bubble
        Row(
            modifier = Modifier
                .padding(horizontal = 4.dp, vertical = 3.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Text(
                text = formattedTime,
                fontSize = 10.sp,
                color = TextTertiary
            )

            if (isOutgoing) {
                when (message.status) {
                    MessageStatus.SENDING -> {
                        Box(
                            modifier = Modifier
                                .size(6.dp)
                                .clip(CircleShape)
                                .background(TextTertiary)
                        )
                    }
                    MessageStatus.SENT -> {
                        Icon(
                            imageVector = Icons.Default.Done,
                            contentDescription = "Sent",
                            tint = TextTertiary,
                            modifier = Modifier.size(11.dp)
                        )
                    }
                    MessageStatus.DELIVERED, MessageStatus.READ -> {
                        Icon(
                            imageVector = Icons.Default.DoneAll,
                            contentDescription = "Read",
                            tint = PrimaryBlue,
                            modifier = Modifier.size(13.dp)
                        )
                    }
                }
            }
        }

        // Display reactions if attached
        if (message.reactions.isNotBlank()) {
            val reactionList = message.reactions.split(",").filter { it.isNotBlank() }
            Row(
                modifier = Modifier
                    .offset(y = (-6).dp)
                    .padding(horizontal = 6.dp)
                    .shadow(4.dp, RoundedCornerShape(12.dp))
                    .clip(RoundedCornerShape(12.dp))
                    .background(MaterialTheme.colorScheme.surface)
                    .padding(horizontal = 6.dp, vertical = 2.dp),
                horizontalArrangement = Arrangement.spacedBy(3.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                reactionList.forEach { reaction ->
                    Text(
                        text = reaction,
                        fontSize = 12.sp,
                        modifier = Modifier.combinedClickable(
                            onClick = { onReactionClick(reaction) }
                        )
                    )
                }
            }
        }
    }
}
