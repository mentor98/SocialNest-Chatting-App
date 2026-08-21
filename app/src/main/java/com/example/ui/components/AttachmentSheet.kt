package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.MessageType
import com.example.ui.theme.*

data class AttachmentOption(
    val title: String,
    val icon: ImageVector,
    val bgColor: Color,
    val type: MessageType
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AttachmentBottomSheet(
    onDismiss: () -> Unit,
    onOptionSelected: (MessageType, String, String) -> Unit,
    modifier: Modifier = Modifier
) {
    val options = listOf(
        AttachmentOption("Gallery", Icons.Default.Image, PrimaryBlue, MessageType.IMAGE),
        AttachmentOption("Camera", Icons.Default.PhotoCamera, AccentPink, MessageType.IMAGE),
        AttachmentOption("Document", Icons.Default.InsertDriveFile, AccentPurple, MessageType.FILE),
        AttachmentOption("Audio", Icons.Default.Headphones, AccentGreen, MessageType.VOICE),
        AttachmentOption("Location", Icons.Default.LocationOn, AccentOrange, MessageType.TEXT),
        AttachmentOption("Contact", Icons.Default.Person, Color(0xFF38BDF8), MessageType.TEXT)
    )

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        containerColor = MaterialTheme.colorScheme.surface,
        shape = RoundedCornerShape(topStart = 28.dp, topEnd = 28.dp),
        dragHandle = {
            Box(
                modifier = Modifier
                    .padding(top = 12.dp, bottom = 8.dp)
                    .width(40.dp)
                    .height(4.dp)
                    .clip(CircleShape)
                    .background(TextTertiary.copy(alpha = 0.4f))
            )
        }
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp, vertical = 16.dp)
                .navigationBarsPadding(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "Share Content",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.onSurface
            )

            Spacer(modifier = Modifier.height(20.dp))

            // 2 rows of 3 options
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceAround
            ) {
                options.take(3).forEach { opt ->
                    AttachmentItemButton(opt) {
                        when (opt.title) {
                            "Gallery" -> onOptionSelected(MessageType.IMAGE, "avatar_martin", "Photo_Switch_Setup.jpg")
                            "Camera" -> onOptionSelected(MessageType.IMAGE, "ic_chat_logo", "Live_Snapshot.png")
                            "Document" -> onOptionSelected(MessageType.FILE, "", "Game_Project_Specification.pdf")
                            else -> onOptionSelected(opt.type, "", opt.title)
                        }
                        onDismiss()
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceAround
            ) {
                options.drop(3).forEach { opt ->
                    AttachmentItemButton(opt) {
                        when (opt.title) {
                            "Audio" -> onOptionSelected(MessageType.VOICE, "", "Voice_Note_01.aac")
                            "Location" -> onOptionSelected(MessageType.TEXT, "", "📍 Shared Location: Downtown Center")
                            "Contact" -> onOptionSelected(MessageType.TEXT, "", "👤 Shared Contact: Martin (@martin_dev)")
                            else -> onOptionSelected(opt.type, "", opt.title)
                        }
                        onDismiss()
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}

@Composable
private fun AttachmentItemButton(
    option: AttachmentOption,
    onClick: () -> Unit
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .clip(RoundedCornerShape(16.dp))
            .clickable { onClick() }
            .padding(10.dp)
            .testTag("attachment_${option.title.lowercase()}")
    ) {
        Box(
            modifier = Modifier
                .size(54.dp)
                .clip(CircleShape)
                .background(option.bgColor.copy(alpha = 0.15f)),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = option.icon,
                contentDescription = option.title,
                tint = option.bgColor,
                modifier = Modifier.size(26.dp)
            )
        }
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = option.title,
            style = MaterialTheme.typography.bodySmall,
            fontWeight = FontWeight.Medium,
            color = MaterialTheme.colorScheme.onSurface
        )
    }
}
