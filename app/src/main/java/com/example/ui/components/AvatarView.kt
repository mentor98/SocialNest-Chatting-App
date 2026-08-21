package com.example.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.AccentGreen
import com.example.ui.theme.PrimaryBlue

@Composable
fun AvatarView(
    name: String,
    avatarResName: String = "",
    avatarColorHex: Long = 0xFF568CF5,
    size: Dp = 48.dp,
    isOnline: Boolean = false,
    showOnlineIndicator: Boolean = true,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val resId = if (avatarResName.isNotBlank()) {
        context.resources.getIdentifier(avatarResName, "drawable", context.packageName)
    } else 0

    Box(
        modifier = modifier.size(size),
        contentAlignment = Alignment.Center
    ) {
        if (resId != 0) {
            Image(
                painter = painterResource(id = resId),
                contentDescription = name,
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .fillMaxSize()
                    .clip(CircleShape)
            )
        } else {
            // High-end pastel initial circle
            val initial = if (name.isNotBlank()) name.first().uppercase() else "?"
            val bgColor = Color(avatarColorHex)
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .clip(CircleShape)
                    .background(bgColor),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = initial,
                    color = Color.White,
                    fontSize = (size.value * 0.42f).sp,
                    fontWeight = FontWeight.SemiBold
                )
            }
        }

        if (showOnlineIndicator && isOnline) {
            val indicatorSize = (size * 0.28f).coerceAtLeast(10.dp).coerceAtMost(16.dp)
            Box(
                modifier = Modifier
                    .size(indicatorSize)
                    .align(Alignment.BottomEnd)
                    .clip(CircleShape)
                    .background(AccentGreen)
                    .border(2.dp, MaterialTheme.colorScheme.surface, CircleShape)
            )
        }
    }
}
