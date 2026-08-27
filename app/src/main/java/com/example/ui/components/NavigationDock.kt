package com.example.ui.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.*
import com.example.viewmodel.NavTab

@Composable
fun LeftNavigationRail(
    activeTab: NavTab,
    onTabSelected: (NavTab) -> Unit,
    unreadNotificationsCount: Int = 1,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .width(64.dp)
            .fillMaxHeight()
            .background(FacebookBlue)
            .padding(vertical = 20.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            // Brand Logo container (SocialNest)
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(RoundedCornerShape(14.dp))
                    .background(Color(0xFF25D366))
                    .clickable(
                        interactionSource = remember { MutableInteractionSource() },
                        indication = null
                    ) { onTabSelected(NavTab.FEED) },
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "SN",
                    color = Color.White,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Black
                )
            }

            Spacer(modifier = Modifier.height(4.dp))

            // Feed (Facebook News Feed)
            RailNavItem(
                icon = Icons.Outlined.DynamicFeed,
                selectedIcon = Icons.Filled.DynamicFeed,
                isSelected = activeTab == NavTab.FEED,
                onClick = { onTabSelected(NavTab.FEED) },
                testTag = "nav_feed"
            )

            // WhatsApp Chats
            RailNavItem(
                icon = Icons.Outlined.ChatBubbleOutline,
                selectedIcon = Icons.Filled.ChatBubble,
                isSelected = activeTab == NavTab.CONVERSATIONS,
                badgeCount = unreadNotificationsCount,
                onClick = { onTabSelected(NavTab.CONVERSATIONS) },
                testTag = "nav_conversations"
            )

            // WhatsApp Status Updates
            RailNavItem(
                icon = Icons.Outlined.DonutLarge,
                selectedIcon = Icons.Filled.DonutLarge,
                isSelected = activeTab == NavTab.UPDATES,
                onClick = { onTabSelected(NavTab.UPDATES) },
                testTag = "nav_updates"
            )

            // WhatsApp Calls
            RailNavItem(
                icon = Icons.Outlined.Call,
                selectedIcon = Icons.Filled.Call,
                isSelected = activeTab == NavTab.CALLS,
                onClick = { onTabSelected(NavTab.CALLS) },
                testTag = "nav_calls"
            )
        }

        // Profile / Menu Avatar at bottom of Rail
        Box(
            modifier = Modifier
                .size(40.dp)
                .clip(CircleShape)
                .background(if (activeTab == NavTab.SETTINGS) Color.White else Color.White.copy(alpha = 0.3f))
                .border(2.dp, Color.White.copy(alpha = 0.6f), CircleShape)
                .clickable(
                    interactionSource = remember { MutableInteractionSource() },
                    indication = null
                ) { onTabSelected(NavTab.SETTINGS) }
                .testTag("nav_settings"),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Filled.Menu,
                contentDescription = "Menu & Profile",
                tint = if (activeTab == NavTab.SETTINGS) FacebookBlue else Color.White,
                modifier = Modifier.size(20.dp)
            )
        }
    }
}

@Composable
fun BottomNavigationDock(
    activeTab: NavTab,
    onTabSelected: (NavTab) -> Unit,
    unreadNotificationsCount: Int = 1,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 10.dp)
            .navigationBarsPadding(),
        contentAlignment = Alignment.Center
    ) {
        Row(
            modifier = Modifier
                .shadow(
                    elevation = 16.dp,
                    shape = RoundedCornerShape(32.dp),
                    spotColor = FacebookBlue.copy(alpha = 0.22f),
                    ambientColor = FacebookBlue.copy(alpha = 0.12f)
                )
                .clip(RoundedCornerShape(32.dp))
                .background(MaterialTheme.colorScheme.surface)
                .padding(horizontal = 8.dp, vertical = 6.dp),
            horizontalArrangement = Arrangement.spacedBy(6.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // 1. Facebook Feed
            BottomDockItem(
                icon = Icons.Outlined.DynamicFeed,
                selectedIcon = Icons.Filled.DynamicFeed,
                label = "Feed",
                isSelected = activeTab == NavTab.FEED,
                activeColor = FacebookBlue,
                onClick = { onTabSelected(NavTab.FEED) },
                testTag = "bottom_nav_feed"
            )

            // 2. WhatsApp Chats
            BottomDockItem(
                icon = Icons.Outlined.ChatBubbleOutline,
                selectedIcon = Icons.Filled.ChatBubble,
                label = "Chats",
                isSelected = activeTab == NavTab.CONVERSATIONS,
                badgeCount = unreadNotificationsCount,
                activeColor = WhatsAppGreen,
                onClick = { onTabSelected(NavTab.CONVERSATIONS) },
                testTag = "bottom_nav_conversations"
            )

            // 3. WhatsApp Updates (Status & Channels)
            BottomDockItem(
                icon = Icons.Outlined.DonutLarge,
                selectedIcon = Icons.Filled.DonutLarge,
                label = "Updates",
                isSelected = activeTab == NavTab.UPDATES,
                activeColor = WhatsAppGreen,
                onClick = { onTabSelected(NavTab.UPDATES) },
                testTag = "bottom_nav_updates"
            )

            // 4. WhatsApp Calls
            BottomDockItem(
                icon = Icons.Outlined.Call,
                selectedIcon = Icons.Filled.Call,
                label = "Calls",
                isSelected = activeTab == NavTab.CALLS,
                activeColor = WhatsAppGreen,
                onClick = { onTabSelected(NavTab.CALLS) },
                testTag = "bottom_nav_calls"
            )

            // 5. Facebook Menu / Profile
            BottomDockItem(
                icon = Icons.Outlined.Menu,
                selectedIcon = Icons.Filled.Menu,
                label = "Menu",
                isSelected = activeTab == NavTab.SETTINGS,
                activeColor = FacebookBlue,
                onClick = { onTabSelected(NavTab.SETTINGS) },
                testTag = "bottom_nav_profile"
            )
        }
    }
}

@Composable
private fun RailNavItem(
    icon: ImageVector,
    selectedIcon: ImageVector,
    isSelected: Boolean,
    badgeCount: Int = 0,
    onClick: () -> Unit,
    testTag: String
) {
    val bgColor by animateColorAsState(
        targetValue = if (isSelected) Color.White else Color.White.copy(alpha = 0.20f),
        animationSpec = tween(220)
    )
    val contentColor by animateColorAsState(
        targetValue = if (isSelected) FacebookBlue else Color.White,
        animationSpec = tween(220)
    )

    Box(
        modifier = Modifier
            .size(40.dp)
            .clip(RoundedCornerShape(14.dp))
            .background(bgColor)
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null
            ) { onClick() }
            .testTag(testTag),
        contentAlignment = Alignment.Center
    ) {
        if (badgeCount > 0) {
            BadgedBox(
                badge = {
                    Badge(
                        containerColor = WhatsAppGreen,
                        contentColor = Color.White,
                        modifier = Modifier.size(8.dp)
                    )
                }
            ) {
                Icon(
                    imageVector = if (isSelected) selectedIcon else icon,
                    contentDescription = null,
                    tint = contentColor,
                    modifier = Modifier.size(22.dp)
                )
            }
        } else {
            Icon(
                imageVector = if (isSelected) selectedIcon else icon,
                contentDescription = null,
                tint = contentColor,
                modifier = Modifier.size(22.dp)
            )
        }
    }
}

@Composable
private fun BottomDockItem(
    icon: ImageVector,
    selectedIcon: ImageVector,
    label: String,
    isSelected: Boolean,
    badgeCount: Int = 0,
    activeColor: Color = FacebookBlue,
    onClick: () -> Unit,
    testTag: String
) {
    val bgColor by animateColorAsState(
        targetValue = if (isSelected) activeColor else Color.Transparent,
        animationSpec = tween(220)
    )
    val contentColor by animateColorAsState(
        targetValue = if (isSelected) Color.White else TextSecondary,
        animationSpec = tween(220)
    )

    Box(
        modifier = Modifier
            .size(46.dp)
            .clip(CircleShape)
            .background(bgColor)
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null
            ) { onClick() }
            .testTag(testTag),
        contentAlignment = Alignment.Center
    ) {
        if (badgeCount > 0) {
            BadgedBox(
                badge = {
                    Badge(
                        containerColor = WhatsAppGreen,
                        contentColor = Color.White,
                        modifier = Modifier.size(7.dp)
                    )
                }
            ) {
                Icon(
                    imageVector = if (isSelected) selectedIcon else icon,
                    contentDescription = label,
                    tint = contentColor,
                    modifier = Modifier.size(22.dp)
                )
            }
        } else {
            Icon(
                imageVector = if (isSelected) selectedIcon else icon,
                contentDescription = label,
                tint = contentColor,
                modifier = Modifier.size(22.dp)
            )
        }
    }
}
