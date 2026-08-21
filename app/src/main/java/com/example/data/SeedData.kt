package com.example.data

import com.example.model.*

object SeedData {

    fun getDefaultUser(): User = User(
        id = "user_me",
        name = "Alex Rivera",
        username = "alex_rivera",
        avatarResName = "ic_chat_logo",
        bio = "Product designer & mobile enthusiast. Loving clean UI/UX.",
        status = "Available for chat",
        phone = "+1 (555) 382-9910",
        email = "alex.rivera@example.com",
        isOnline = true,
        isVerified = true,
        visitsCount = 142,
        messagesCount = 389,
        callsCount = 94,
        notificationsAllowed = true
    )

    fun getDefaultContacts(): List<Contact> = listOf(
        Contact(
            id = "contact_martin",
            name = "Martin",
            username = "martin_dev",
            avatarResName = "avatar_martin",
            avatarColorHex = 0xFF568CF5,
            statusMessage = "Excellent are efforts",
            isOnline = true,
            lastSeenText = "Online",
            initialLetter = "M",
            phone = "+1 (555) 234-5678",
            email = "martin.d@example.com",
            visitsCount = 69,
            messagesCount = 18,
            callsCount = 87,
            notificationsAllowed = true,
            remarks = "Switch Gamer & Senior Dev",
            dynamicMoments = "Just unboxed the OLED Switch! Zelda gameplay is insane 🎮✨",
            isFavorite = true
        ),
        Contact(
            id = "contact_max",
            name = "Max",
            username = "max_runner",
            avatarResName = "",
            avatarColorHex = 0xFFA88BFF,
            statusMessage = "Hanging on to your dreams",
            isOnline = false,
            lastSeenText = "2 hours ago",
            initialLetter = "M",
            phone = "+1 (555) 345-6789",
            email = "max.runner@example.com",
            visitsCount = 34,
            messagesCount = 42,
            callsCount = 15,
            notificationsAllowed = true,
            remarks = "Running Club",
            dynamicMoments = "Morning 10k run finished. Perfect weather! 🏃‍♂️⛅"
        ),
        Contact(
            id = "contact_merry",
            name = "Merry",
            username = "merry_art",
            avatarResName = "avatar_merry",
            avatarColorHex = 0xFFFF86A8,
            statusMessage = "Nothing for nothing",
            isOnline = true,
            lastSeenText = "Online",
            initialLetter = "M",
            phone = "+1 (555) 456-7890",
            email = "merry.designer@example.com",
            visitsCount = 85,
            messagesCount = 120,
            callsCount = 43,
            notificationsAllowed = true,
            remarks = "UI Art Director",
            dynamicMoments = "New palette exploration with soft lavender tones 🎨💜",
            isFavorite = true
        ),
        Contact(
            id = "contact_mailbox",
            name = "Mailbox",
            username = "mailbox_system",
            avatarResName = "",
            avatarColorHex = 0xFF568CF5,
            statusMessage = "System notifications & cloud sync",
            isOnline = true,
            lastSeenText = "Online",
            initialLetter = "M",
            phone = "+1 (800) 555-0199",
            email = "support@chat-app.internal",
            visitsCount = 12,
            messagesCount = 6,
            callsCount = 0,
            notificationsAllowed = true,
            remarks = "Official Bot"
        ),
        Contact(
            id = "contact_nalla",
            name = "Nalla",
            username = "nalla_sky",
            avatarResName = "",
            avatarColorHex = 0xFF4ADE80,
            statusMessage = "Stay positive and brave",
            isOnline = true,
            lastSeenText = "Online",
            initialLetter = "N",
            phone = "+1 (555) 567-8901",
            email = "nalla.sky@example.com",
            visitsCount = 28,
            messagesCount = 54,
            callsCount = 19,
            notificationsAllowed = true
        ),
        Contact(
            id = "contact_norland",
            name = "Norland",
            username = "norland_arch",
            avatarResName = "",
            avatarColorHex = 0xFFFBBF24,
            statusMessage = "Design is thinking made visual",
            isOnline = false,
            lastSeenText = "Yesterday",
            initialLetter = "N",
            phone = "+1 (555) 678-9012",
            email = "norland.arch@example.com",
            visitsCount = 47,
            messagesCount = 88,
            callsCount = 31,
            notificationsAllowed = true
        ),
        Contact(
            id = "contact_nick",
            name = "Nick",
            username = "nick_code",
            avatarResName = "",
            avatarColorHex = 0xFF60A5FA,
            statusMessage = "Coding with coffee",
            isOnline = true,
            lastSeenText = "Online",
            initialLetter = "N",
            phone = "+1 (555) 789-0123",
            email = "nick.dev@example.com",
            visitsCount = 51,
            messagesCount = 76,
            callsCount = 22,
            notificationsAllowed = true
        ),
        Contact(
            id = "contact_nancy",
            name = "Nancy",
            username = "nancy_claire",
            avatarResName = "",
            avatarColorHex = 0xFFF472B6,
            statusMessage = "Living in the moment",
            isOnline = false,
            lastSeenText = "3 hours ago",
            initialLetter = "N",
            phone = "+1 (555) 890-1234",
            email = "nancy.claire@example.com",
            visitsCount = 19,
            messagesCount = 31,
            callsCount = 8,
            notificationsAllowed = true
        ),
        Contact(
            id = "contact_arthur",
            name = "Arthur",
            username = "arthur_lens",
            avatarResName = "",
            avatarColorHex = 0xFF818CF8,
            statusMessage = "In love with photography",
            isOnline = false,
            lastSeenText = "5 hours ago",
            initialLetter = "A",
            phone = "+1 (555) 123-4567",
            email = "arthur.lens@example.com",
            visitsCount = 15,
            messagesCount = 23,
            callsCount = 5,
            notificationsAllowed = true
        ),
        Contact(
            id = "contact_bella",
            name = "Bella",
            username = "bella_craft",
            avatarResName = "",
            avatarColorHex = 0xFFFB7185,
            statusMessage = "Building the future step by step",
            isOnline = true,
            lastSeenText = "Online",
            initialLetter = "B",
            phone = "+1 (555) 234-9876",
            email = "bella.craft@example.com",
            visitsCount = 62,
            messagesCount = 95,
            callsCount = 29,
            notificationsAllowed = true
        ),
        Contact(
            id = "contact_chloe",
            name = "Chloe",
            username = "chloe_style",
            avatarResName = "",
            avatarColorHex = 0xFF34D399,
            statusMessage = "Creating something new every single day",
            isOnline = false,
            lastSeenText = "1 day ago",
            initialLetter = "C",
            phone = "+1 (555) 345-0987",
            email = "chloe.style@example.com",
            visitsCount = 41,
            messagesCount = 63,
            callsCount = 17,
            notificationsAllowed = true
        ),
        Contact(
            id = "contact_david",
            name = "David",
            username = "david_minimal",
            avatarResName = "",
            avatarColorHex = 0xFF38BDF8,
            statusMessage = "Simplicity is the ultimate sophistication",
            isOnline = true,
            lastSeenText = "Online",
            initialLetter = "D",
            phone = "+1 (555) 456-1230",
            email = "david.m@example.com",
            visitsCount = 58,
            messagesCount = 81,
            callsCount = 33,
            notificationsAllowed = true
        ),
        Contact(
            id = "contact_sophia",
            name = "Sophia",
            username = "sophia_sun",
            avatarResName = "",
            avatarColorHex = 0xFFF59E0B,
            statusMessage = "Dream big, work hard, stay humble",
            isOnline = true,
            lastSeenText = "Online",
            initialLetter = "S",
            phone = "+1 (555) 901-2345",
            email = "sophia.sun@example.com",
            visitsCount = 73,
            messagesCount = 112,
            callsCount = 46,
            notificationsAllowed = true
        ),
        Contact(
            id = "contact_zoe",
            name = "Zoe",
            username = "zoe_vibes",
            avatarResName = "",
            avatarColorHex = 0xFFEC4899,
            statusMessage = "Sunshine mixed with a little hurricane",
            isOnline = false,
            lastSeenText = "4 hours ago",
            initialLetter = "Z",
            phone = "+1 (555) 012-3456",
            email = "zoe.vibes@example.com",
            visitsCount = 29,
            messagesCount = 45,
            callsCount = 12,
            notificationsAllowed = true
        )
    )

    fun getDefaultConversations(): List<Conversation> {
        val now = System.currentTimeMillis()
        return listOf(
            Conversation(
                id = "conv_martin",
                contactId = "contact_martin",
                contactName = "Martin",
                contactAvatarResName = "avatar_martin",
                contactAvatarColorHex = 0xFF568CF5,
                isOnline = true,
                lastMessageText = "No, no, if I do that, my gf will kill me~ I choose life, instead Switch 🎮",
                lastMessageTime = now - 1000 * 60 * 2, // 2 mins ago
                unreadCount = 0,
                isPinned = true
            ),
            Conversation(
                id = "conv_merry",
                contactId = "contact_merry",
                contactName = "Merry",
                contactAvatarResName = "avatar_merry",
                contactAvatarColorHex = 0xFFFF86A8,
                isOnline = true,
                lastMessageText = "Check out the new lavender prototype! 🎨✨",
                lastMessageTime = now - 1000 * 60 * 15, // 15 mins ago
                unreadCount = 2,
                isPinned = true
            ),
            Conversation(
                id = "conv_group_dev",
                contactId = "contact_group_dev",
                contactName = "Dev & Gaming Squad 🎮",
                contactAvatarResName = "",
                contactAvatarColorHex = 0xFFA88BFF,
                isOnline = true,
                lastMessageText = "Martin: Who is up for Mario Kart 8 tonight? 🏁",
                lastMessageTime = now - 1000 * 60 * 35, // 35 mins ago
                unreadCount = 3,
                isPinned = false
            ),
            Conversation(
                id = "conv_bella",
                contactId = "contact_bella",
                contactName = "Bella",
                contactAvatarResName = "",
                contactAvatarColorHex = 0xFFFB7185,
                isOnline = true,
                lastMessageText = "Sent an attachment: UI_Wireframes.pdf 📎",
                lastMessageTime = now - 1000 * 60 * 75,
                unreadCount = 1,
                isPinned = false
            ),
            Conversation(
                id = "conv_max",
                contactId = "contact_max",
                contactName = "Max",
                contactAvatarResName = "",
                contactAvatarColorHex = 0xFFA88BFF,
                isOnline = false,
                lastMessageText = "Are we still doing the morning run tomorrow? 🏃‍♂️",
                lastMessageTime = now - 1000 * 60 * 120, // 2 hrs ago
                unreadCount = 0
            ),
            Conversation(
                id = "conv_nalla",
                contactId = "contact_nalla",
                contactName = "Nalla",
                contactAvatarResName = "",
                contactAvatarColorHex = 0xFF4ADE80,
                isOnline = true,
                lastMessageText = "🎙️ Voice message (0:14)",
                lastMessageTime = now - 1000 * 60 * 240, // 4 hours ago
                unreadCount = 1
            ),
            Conversation(
                id = "conv_sophia",
                contactId = "contact_sophia",
                contactName = "Sophia",
                contactAvatarResName = "",
                contactAvatarColorHex = 0xFFF59E0B,
                isOnline = true,
                lastMessageText = "Thanks for the design review Alex! ❤️",
                lastMessageTime = now - 1000 * 60 * 360,
                unreadCount = 0
            ),
            Conversation(
                id = "conv_david",
                contactId = "contact_david",
                contactName = "David",
                contactAvatarResName = "",
                contactAvatarColorHex = 0xFF38BDF8,
                isOnline = true,
                lastMessageText = "See you at the coffee shop ☕",
                lastMessageTime = now - 1000 * 60 * 600,
                unreadCount = 0
            ),
            Conversation(
                id = "conv_norland",
                contactId = "contact_norland",
                contactName = "Norland",
                contactAvatarResName = "",
                contactAvatarColorHex = 0xFFFBBF24,
                isOnline = false,
                lastMessageText = "The architecture layout is finalized 👍",
                lastMessageTime = now - 1000 * 60 * 60 * 24, // 1 day ago
                unreadCount = 0
            )
        )
    }

    fun getDefaultMessages(): List<Message> {
        val now = System.currentTimeMillis()
        val m = 60 * 1000L
        return listOf(
            // Martin Conversation (Matches exact prompt script)
            Message(
                id = "msg_m_1",
                conversationId = "conv_martin",
                senderId = "contact_martin",
                text = "Good morning, guy",
                timestamp = now - 12 * m,
                status = MessageStatus.READ
            ),
            Message(
                id = "msg_m_2",
                conversationId = "conv_martin",
                senderId = "contact_martin",
                text = "How about your new Switch?",
                timestamp = now - 10 * m,
                status = MessageStatus.READ
            ),
            Message(
                id = "msg_m_3",
                conversationId = "conv_martin",
                senderId = "user_me",
                text = "Awesome!",
                timestamp = now - 8 * m,
                status = MessageStatus.READ
            ),
            Message(
                id = "msg_m_4",
                conversationId = "conv_martin",
                senderId = "contact_martin",
                text = "Could u come here to play with me?",
                timestamp = now - 5 * m,
                status = MessageStatus.READ
            ),
            Message(
                id = "msg_m_5",
                conversationId = "conv_martin",
                senderId = "user_me",
                text = "No,no, if I do that, my gf will kill me~",
                timestamp = now - 3 * m,
                status = MessageStatus.READ
            ),
            Message(
                id = "msg_m_6",
                conversationId = "conv_martin",
                senderId = "user_me",
                text = "I choose life, instead Switch",
                timestamp = now - 2 * m,
                status = MessageStatus.READ,
                reactions = "😂,❤️"
            ),

            // Merry Conversation
            Message(
                id = "msg_me_1",
                conversationId = "conv_merry",
                senderId = "user_me",
                text = "Hey Merry! How is the new design system coming along?",
                timestamp = now - 60 * m,
                status = MessageStatus.READ
            ),
            Message(
                id = "msg_me_2",
                conversationId = "conv_merry",
                senderId = "contact_merry",
                text = "It's looking super fresh! Using that soft #E9EEFF lavender with vibrant blue accents.",
                timestamp = now - 45 * m,
                status = MessageStatus.READ
            ),
            Message(
                id = "msg_me_3",
                conversationId = "conv_merry",
                senderId = "contact_merry",
                text = "Check out the new lavender prototype!",
                timestamp = now - 25 * m,
                status = MessageStatus.DELIVERED
            ),

            // Max Conversation
            Message(
                id = "msg_mx_1",
                conversationId = "conv_max",
                senderId = "contact_max",
                text = "Hey! 10k morning jog was great.",
                timestamp = now - 180 * m,
                status = MessageStatus.READ
            ),
            Message(
                id = "msg_mx_2",
                conversationId = "conv_max",
                senderId = "contact_max",
                text = "Are we still doing the morning run tomorrow?",
                timestamp = now - 90 * m,
                status = MessageStatus.READ
            )
        )
    }

    fun getDefaultNotifications(): List<NotificationItem> = listOf(
        NotificationItem(
            id = "notif_1",
            title = "New Message from Merry",
            description = "Check out the new lavender prototype!",
            timeAgo = "25m ago",
            type = NotificationType.MESSAGE,
            isRead = false,
            contactId = "contact_merry",
            avatarResName = "avatar_merry",
            avatarColorHex = 0xFFFF86A8
        ),
        NotificationItem(
            id = "notif_2",
            title = "Missed Call from Martin",
            description = "Voice call · 2 rings",
            timeAgo = "1h ago",
            type = NotificationType.MISSED_CALL,
            isRead = false,
            contactId = "contact_martin",
            avatarResName = "avatar_martin",
            avatarColorHex = 0xFF568CF5
        ),
        NotificationItem(
            id = "notif_3",
            title = "Friend Request",
            description = "Sophia Sun sent you a connection request",
            timeAgo = "3h ago",
            type = NotificationType.FRIEND_REQUEST,
            isRead = true,
            contactId = "contact_sophia",
            avatarResName = "",
            avatarColorHex = 0xFFF59E0B
        ),
        NotificationItem(
            id = "notif_4",
            title = "System Update",
            description = "Real-time sync and voice waveforms active",
            timeAgo = "Yesterday",
            type = NotificationType.SYSTEM,
            isRead = true,
            avatarResName = "",
            avatarColorHex = 0xFFA88BFF
        )
    )

    fun getDefaultCallRecords(): List<CallRecord> {
        val now = System.currentTimeMillis()
        return listOf(
            CallRecord(
                id = "call_1",
                contactId = "contact_martin",
                contactName = "Martin",
                contactAvatarResName = "avatar_martin",
                contactAvatarColorHex = 0xFF568CF5,
                isVideo = true,
                isIncoming = false,
                isMissed = false,
                durationSeconds = 245,
                timestamp = now - 1000 * 60 * 60 * 2
            ),
            CallRecord(
                id = "call_2",
                contactId = "contact_merry",
                contactName = "Merry",
                contactAvatarResName = "avatar_merry",
                contactAvatarColorHex = 0xFFFF86A8,
                isVideo = false,
                isIncoming = true,
                isMissed = false,
                durationSeconds = 612,
                timestamp = now - 1000 * 60 * 60 * 5
            ),
            CallRecord(
                id = "call_3",
                contactId = "contact_martin",
                contactName = "Martin",
                contactAvatarResName = "avatar_martin",
                contactAvatarColorHex = 0xFF568CF5,
                isVideo = false,
                isIncoming = true,
                isMissed = true,
                durationSeconds = 0,
                timestamp = now - 1000 * 60 * 60 * 26
            )
        )
    }
}
