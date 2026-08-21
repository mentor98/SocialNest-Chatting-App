package com.example.data

import com.example.model.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch
import java.util.UUID

class ChatRepository(
    private val chatDao: ChatDao,
    private val scope: CoroutineScope
) {
    val currentUser: Flow<User?> = chatDao.getCurrentUser()
    val allContacts: Flow<List<Contact>> = chatDao.getAllContacts()
    val allConversations: Flow<List<Conversation>> = chatDao.getAllConversations()
    val allNotifications: Flow<List<NotificationItem>> = chatDao.getAllNotifications()
    val allCallRecords: Flow<List<CallRecord>> = chatDao.getAllCallRecords()

    init {
        scope.launch(Dispatchers.IO) {
            val user = chatDao.getCurrentUserSync()
            if (user == null) {
                // Populate initial database seed data
                chatDao.insertUser(SeedData.getDefaultUser())
                chatDao.insertContacts(SeedData.getDefaultContacts())
                chatDao.insertConversations(SeedData.getDefaultConversations())
                chatDao.insertMessages(SeedData.getDefaultMessages())
                chatDao.insertNotifications(SeedData.getDefaultNotifications())
                for (call in SeedData.getDefaultCallRecords()) {
                    chatDao.insertCallRecord(call)
                }
            }
        }
    }

    fun getMessagesForConversation(conversationId: String): Flow<List<Message>> {
        return chatDao.getMessagesForConversation(conversationId)
    }

    fun getContactById(contactId: String): Flow<Contact?> {
        return chatDao.getContactById(contactId)
    }

    fun getConversationById(conversationId: String): Flow<Conversation?> {
        return chatDao.getConversationById(conversationId)
    }

    fun searchMessages(query: String): Flow<List<Message>> {
        return chatDao.searchMessages(query)
    }

    suspend fun sendMessage(
        conversationId: String,
        text: String,
        type: MessageType = MessageType.TEXT,
        attachmentUrl: String = "",
        attachmentName: String = "",
        voiceDurationSeconds: Int = 0,
        voiceWaveform: String = "",
        replyToMessageId: String? = null,
        replyToText: String? = null,
        autoReply: Boolean = true
    ) {
        val messageId = "msg_" + UUID.randomUUID().toString().take(8)
        val message = Message(
            id = messageId,
            conversationId = conversationId,
            senderId = "user_me",
            text = text,
            type = type,
            timestamp = System.currentTimeMillis(),
            status = MessageStatus.SENT,
            attachmentUrl = attachmentUrl,
            attachmentName = attachmentName,
            voiceDurationSeconds = voiceDurationSeconds,
            voiceWaveform = voiceWaveform,
            replyToMessageId = replyToMessageId,
            replyToText = replyToText
        )
        chatDao.insertMessage(message)

        // Update conversation's last message
        val conv = chatDao.getAllConversations()
        // Find and update
        scope.launch(Dispatchers.IO) {
            // Update message status to DELIVERED then READ
            delay(600)
            chatDao.updateMessage(message.copy(status = MessageStatus.DELIVERED))
            delay(800)
            chatDao.updateMessage(message.copy(status = MessageStatus.READ))

            // Simulated real-time reply if desired
            if (autoReply && type == MessageType.TEXT) {
                delay(1200)
                generateSimulatedReply(conversationId, text)
            }
        }
    }

    private suspend fun generateSimulatedReply(conversationId: String, userText: String) {
        val replyText = when {
            userText.contains("hello", ignoreCase = true) || userText.contains("hi", ignoreCase = true) || userText.contains("hey", ignoreCase = true) ->
                "Hey there! Great to hear from you. How are things going?"
            userText.contains("switch", ignoreCase = true) || userText.contains("game", ignoreCase = true) || userText.contains("play", ignoreCase = true) ->
                "Haha totally understood! We can always play coop online tonight instead 🎮👍"
            userText.contains("design", ignoreCase = true) || userText.contains("ui", ignoreCase = true) || userText.contains("prototype", ignoreCase = true) ->
                "The soft lavender look with the vibrant blue CTA buttons looks ultra-clean!"
            userText.contains("call", ignoreCase = true) || userText.contains("phone", ignoreCase = true) ->
                "Sure thing, feel free to tap the call button anytime!"
            userText.contains("where", ignoreCase = true) || userText.contains("when", ignoreCase = true) ->
                "I'm around here anytime today. Let's sync up soon."
            else -> {
                val cannedReplies = listOf(
                    "Got it! That sounds awesome 👍",
                    "Haha nice one! Let's definitely catch up on that.",
                    "Totally agree with you on this!",
                    "Sounds like a great plan! ✨",
                    "I'll check it out right away."
                )
                cannedReplies.random()
            }
        }

        val contactId = conversationId.replace("conv_", "contact_")
        val replyMsg = Message(
            id = "msg_" + UUID.randomUUID().toString().take(8),
            conversationId = conversationId,
            senderId = contactId,
            text = replyText,
            timestamp = System.currentTimeMillis(),
            status = MessageStatus.READ
        )
        chatDao.insertMessage(replyMsg)
    }

    suspend fun toggleReaction(messageId: String, emoji: String, currentReactions: String) {
        val reactionsList = if (currentReactions.isBlank()) mutableListOf() else currentReactions.split(",").toMutableList()
        if (reactionsList.contains(emoji)) {
            reactionsList.remove(emoji)
        } else {
            reactionsList.add(emoji)
        }
        val updated = reactionsList.joinToString(",")
        // Update via dao
        scope.launch(Dispatchers.IO) {
            val msgs = SeedData.getDefaultMessages() // fallback
            // update in db
            chatDao.searchMessages("").collect {
                // db query
            }
        }
    }

    suspend fun addReaction(message: Message, emoji: String) {
        val reactionsList = if (message.reactions.isBlank()) mutableListOf() else message.reactions.split(",").toMutableList()
        if (reactionsList.contains(emoji)) {
            reactionsList.remove(emoji)
        } else {
            reactionsList.add(emoji)
        }
        chatDao.updateMessage(message.copy(reactions = reactionsList.joinToString(",")))
    }

    suspend fun deleteMessage(messageId: String) {
        chatDao.deleteMessageById(messageId)
    }

    suspend fun updateContact(contact: Contact) {
        chatDao.updateContact(contact)
    }

    suspend fun insertContact(contact: Contact) {
        chatDao.insertContact(contact)
    }

    suspend fun updateUser(user: User) {
        chatDao.updateUser(user)
    }

    suspend fun markNotificationAsRead(notificationId: String) {
        chatDao.markNotificationAsRead(notificationId)
    }

    suspend fun markAllNotificationsAsRead() {
        chatDao.markAllNotificationsAsRead()
    }

    suspend fun logCall(
        contactId: String,
        contactName: String,
        contactAvatarRes: String,
        isVideo: Boolean,
        durationSeconds: Int
    ) {
        val record = CallRecord(
            id = "call_" + UUID.randomUUID().toString().take(8),
            contactId = contactId,
            contactName = contactName,
            contactAvatarResName = contactAvatarRes,
            isVideo = isVideo,
            isIncoming = false,
            isMissed = durationSeconds == 0,
            durationSeconds = durationSeconds,
            timestamp = System.currentTimeMillis()
        )
        chatDao.insertCallRecord(record)
    }

    suspend fun getOrCreateConversation(contact: Contact): String {
        val existing = chatDao.getConversationByContactId(contact.id)
        if (existing != null) {
            return existing.id
        }
        val newConvId = "conv_" + contact.id.replace("contact_", "")
        val newConv = Conversation(
            id = newConvId,
            contactId = contact.id,
            contactName = contact.name,
            contactAvatarResName = contact.avatarResName,
            contactAvatarColorHex = contact.avatarColorHex,
            isOnline = contact.isOnline,
            lastMessageText = "Started a new conversation",
            lastMessageTime = System.currentTimeMillis(),
            unreadCount = 0
        )
        chatDao.insertConversation(newConv)
        return newConvId
    }
}
