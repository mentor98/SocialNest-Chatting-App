package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.Send
import androidx.compose.material.icons.outlined.EmojiEmotions
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.Message
import com.example.ui.theme.*

@Composable
fun MessageComposer(
    replyToMessage: Message? = null,
    onClearReply: () -> Unit = {},
    onSendMessage: (String) -> Unit,
    onAttachmentClick: () -> Unit = {},
    onMicClick: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    var text by remember { mutableStateOf("") }
    var showEmojiQuickBar by remember { mutableStateOf(false) }

    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 14.dp, vertical = 8.dp)
            .navigationBarsPadding()
    ) {
        // Reply Preview Banner if replying to a message
        AnimatedVisibility(
            visible = replyToMessage != null,
            enter = fadeIn(),
            exit = fadeOut()
        ) {
            if (replyToMessage != null) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 6.dp)
                        .shadow(4.dp, RoundedCornerShape(14.dp))
                        .clip(RoundedCornerShape(14.dp))
                        .background(MaterialTheme.colorScheme.surface)
                        .padding(horizontal = 12.dp, vertical = 6.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "Replying to ${if (replyToMessage.isOutgoing) "yourself" else "message"}",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = PrimaryBlue
                        )
                        Text(
                            text = replyToMessage.text,
                            fontSize = 12.sp,
                            maxLines = 1,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = "Cancel reply",
                        tint = TextTertiary,
                        modifier = Modifier
                            .size(18.dp)
                            .clickable { onClearReply() }
                    )
                }
            }
        }

        // Quick Emoji Selector Bar if triggered
        AnimatedVisibility(
            visible = showEmojiQuickBar,
            enter = fadeIn(),
            exit = fadeOut()
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 8.dp)
                    .shadow(6.dp, RoundedCornerShape(20.dp))
                    .clip(RoundedCornerShape(20.dp))
                    .background(MaterialTheme.colorScheme.surface)
                    .padding(horizontal = 14.dp, vertical = 6.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                listOf("😊", "😂", "❤️", "👍", "🔥", "🎉", "🎮", "✨").forEach { emoji ->
                    Text(
                        text = emoji,
                        fontSize = 22.sp,
                        modifier = Modifier
                            .clickable {
                                text += emoji
                            }
                            .padding(4.dp)
                    )
                }
            }
        }

        // Main Composer Pill + Send Button
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            // Floating Rounded Input Card (matching rounded-[28px] p-2 border border-slate-100 shadow-[0_-8px_30px_rgba(80,120,200,0.05)])
            Box(
                modifier = Modifier
                    .weight(1f)
                    .shadow(
                        elevation = 6.dp,
                        shape = RoundedCornerShape(28.dp),
                        spotColor = Color(0x105078C8),
                        ambientColor = Color(0x08000000)
                    )
                    .clip(RoundedCornerShape(28.dp))
                    .background(MaterialTheme.colorScheme.surface)
                    .padding(horizontal = 10.dp, vertical = 6.dp),
                contentAlignment = Alignment.CenterStart
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    // Attachment (📎 / +) Button
                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .clip(CircleShape)
                            .clickable { onAttachmentClick() }
                            .testTag("composer_attachment_button"),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Add,
                            contentDescription = "Attachment",
                            tint = TextTertiary,
                            modifier = Modifier.size(22.dp)
                        )
                    }

                    Spacer(modifier = Modifier.width(6.dp))

                    // Text Field (text-[14px] text-slate-700 placeholder:text-slate-300)
                    Box(
                        modifier = Modifier.weight(1f),
                        contentAlignment = Alignment.CenterStart
                    ) {
                        if (text.isEmpty()) {
                            Text(
                                text = "Write a message...",
                                style = MaterialTheme.typography.bodyMedium,
                                color = TextTertiary,
                                fontSize = 14.sp
                            )
                        }

                        BasicTextField(
                            value = text,
                            onValueChange = { text = it },
                            textStyle = MaterialTheme.typography.bodyMedium.copy(
                                color = MaterialTheme.colorScheme.onSurface,
                                fontSize = 14.sp
                            ),
                            cursorBrush = SolidColor(PrimaryBlue),
                            maxLines = 4,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 6.dp)
                                .testTag("message_input_field")
                        )
                    }

                    Spacer(modifier = Modifier.width(4.dp))

                    // Emoji Button (active: #FF86A8)
                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .clip(CircleShape)
                            .clickable { showEmojiQuickBar = !showEmojiQuickBar }
                            .testTag("composer_emoji_button"),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Outlined.EmojiEmotions,
                            contentDescription = "Emojis",
                            tint = if (showEmojiQuickBar) AccentPink else TextTertiary,
                            modifier = Modifier.size(22.dp)
                        )
                    }

                    // Microphone Button
                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .clip(CircleShape)
                            .clickable { onMicClick() }
                            .testTag("composer_mic_button"),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Mic,
                            contentDescription = "Voice Record",
                            tint = TextTertiary,
                            modifier = Modifier.size(22.dp)
                        )
                    }

                    Spacer(modifier = Modifier.width(2.dp))

                    // Send Button inside composer dock or alongside
                    Box(
                        modifier = Modifier
                            .size(38.dp)
                            .shadow(
                                elevation = 4.dp,
                                shape = CircleShape,
                                spotColor = PrimaryBlue.copy(alpha = 0.4f)
                            )
                            .clip(CircleShape)
                            .background(PrimaryBlue)
                            .clickable(
                                interactionSource = remember { MutableInteractionSource() },
                                indication = null
                            ) {
                                if (text.isNotBlank()) {
                                    onSendMessage(text.trim())
                                    text = ""
                                    showEmojiQuickBar = false
                                }
                            }
                            .testTag("composer_send_button"),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Send,
                            contentDescription = "Send",
                            tint = Color.White,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }
            }
        }
    }
}
