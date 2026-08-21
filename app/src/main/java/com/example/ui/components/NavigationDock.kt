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
import com.example.ui.theme.AccentPink
import com.example.ui.theme.PrimaryBlue
import com.example.ui.theme.PrimaryBlueDark
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
            .background(PrimaryBlue)
            .padding(vertical = 24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Brand Logo container
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(RoundedCornerShape(14.dp))
                    .background(Color(0xFF6A9BFF))
                    .clickable(
                        interactionSource = remember { MutableInteractionSource() },
                        indication = null
                    ) { onTabSelected(NavTab.CONVERSATIONS) },
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "◈",
                    color = Color.White,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold
                )
            }

            Spacer(modifier = Modifier.height(4.dp))

            // Conversations / Chats Tab (Primary)
            RailNavItem(
                icon = Icons.Outlined.ChatBubbleOutline,
                selectedIcon = Icons.Filled.ChatBubble,
                isSelected = activeTab == NavTab.CONVERSATIONS,
                badgeCount = unreadNotificationsCount,
                onClick = { onTabSelected(NavTab.CONVERSATIONS) },
                testTag = "nav_conversations"
            )

            // Calls Tab
            RailNavItem(
                icon = Icons.Outlined.Call,
                selectedIcon = Icons.Filled.Call,
                isSelected = activeTab == NavTab.CALLS,
                onClick = { onTabSelected(NavTab.CALLS) },
                testTag = "nav_calls"
            )

            // Contacts Tab
            RailNavItem(
                icon = Icons.Outlined.PeopleOutline,
                selectedIcon = Icons.Filled.People,
                isSelected = activeTab == NavTab.CONTACTS,
                onClick = { onTabSelected(NavTab.CONTACTS) },
                testTag = "nav_contacts"
            )

            // Notifications Tab
            RailNavItem(
                icon = Icons.Outlined.Notifications,
                selectedIcon = Icons.Filled.Notifications,
                isSelected = activeTab == NavTab.NOTIFICATIONS,
                onClick = { onTabSelected(NavTab.NOTIFICATIONS) },
                testTag = "nav_notifications"
            )
        }

        // Profile Avatar at bottom of Rail
        Box(
            modifier = Modifier
                .size(40.dp)
                .clip(CircleShape)
                .background(Color.White.copy(alpha = 0.3f))
                .border(2.dp, Color.White.copy(alpha = 0.5f), CircleShape)
                .clickable(
                    interactionSource = remember { MutableInteractionSource() },
                    indication = null
                ) { onTabSelected(NavTab.SETTINGS) }
                .testTag("nav_settings"),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Filled.Person,
                contentDescription = "My Profile",
                tint = Color.White,
                modifier = Modifier.size(22.dp)
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
            .padding(horizontal = 20.dp, vertical = 10.dp)
            .navigationBarsPadding(),
        contentAlignment = Alignment.Center
    ) {
        Row(
            modifier = Modifier
                .shadow(
                    elevation = 16.dp,
                    shape = RoundedCornerShape(32.dp),
                    spotColor = PrimaryBlue.copy(alpha = 0.22f),
                    ambientColor = PrimaryBlue.copy(alpha = 0.12f)
                )
                .clip(RoundedCornerShape(32.dp))
                .background(MaterialTheme.colorScheme.surface)
                .padding(horizontal = 12.dp, vertical = 8.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            BottomDockItem(
                icon = Icons.Outlined.ChatBubbleOutline,
                selectedIcon = Icons.Filled.ChatBubble,
                label = "Chats",
                isSelected = activeTab == NavTab.CONVERSATIONS,
                badgeCount = unreadNotificationsCount,
                onClick = { onTabSelected(NavTab.CONVERSATIONS) },
                testTag = "bottom_nav_conversations"
            )

            BottomDockItem(
                icon = Icons.Outlined.Call,
                selectedIcon = Icons.Filled.Call,
                label = "Calls",
                isSelected = activeTab == NavTab.CALLS,
                onClick = { onTabSelected(NavTab.CALLS) },
                testTag = "bottom_nav_calls"
            )

            BottomDockItem(
                icon = Icons.Outlined.PeopleOutline,
                selectedIcon = Icons.Filled.People,
                label = "Contacts",
                isSelected = activeTab == NavTab.CONTACTS,
                onClick = { onTabSelected(NavTab.CONTACTS) },
                testTag = "bottom_nav_contacts"
            )

            BottomDockItem(
                icon = Icons.Outlined.Notifications,
                selectedIcon = Icons.Filled.Notifications,
                label = "Alerts",
                isSelected = activeTab == NavTab.NOTIFICATIONS,
                onClick = { onTabSelected(NavTab.NOTIFICATIONS) },
                testTag = "bottom_nav_notifications"
            )

            BottomDockItem(
                icon = Icons.Outlined.PersonOutline,
                selectedIcon = Icons.Filled.Person,
                label = "Profile",
                isSelected = activeTab == NavTab.SETTINGS,
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
        targetValue = if (isSelected) PrimaryBlue else Color.White,
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
                        containerColor = AccentPink,
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
    onClick: () -> Unit,
    testTag: String
) {
    val bgColor by animateColorAsState(
        targetValue = if (isSelected) PrimaryBlue else Color.Transparent,
        animationSpec = tween(220)
    )
    val contentColor by animateColorAsState(
        targetValue = if (isSelected) Color.White else PrimaryBlueDark.copy(alpha = 0.6f),
        animationSpec = tween(220)
    )

    Box(
        modifier = Modifier
            .size(44.dp)
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
                        containerColor = AccentPink,
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
