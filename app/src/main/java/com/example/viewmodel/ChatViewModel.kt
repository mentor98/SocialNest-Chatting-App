package com.example.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.ChatDatabase
import com.example.data.ChatRepository
import com.example.data.SeedData
import com.example.model.*
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

sealed class Screen {
    object Splash : Screen()
    object Auth : Screen()
    object Feed : Screen()
    object Conversations : Screen()
    object Updates : Screen()
    object Contacts : Screen()
    object Calls : Screen()
    data class Chat(val conversationId: String, val contactId: String) : Screen()
    data class ContactProfile(val contactId: String) : Screen()
    object UserProfile : Screen()
    object Search : Screen()
    object Notifications : Screen()
    object NewConversation : Screen()
    data class ActiveCall(val contactId: String, val isVideo: Boolean) : Screen()
}

enum class NavTab {
    FEED,
    CONVERSATIONS,
    UPDATES,
    CALLS,
    SETTINGS
}

data class ActiveCallState(
    val contact: Contact? = null,
    val isVideo: Boolean = false,
    val durationSeconds: Int = 0,
    val isMuted: Boolean = false,
    val isSpeakerOn: Boolean = true,
    val isVideoPaused: Boolean = false
)

data class UiState(
    val currentScreen: Screen = Screen.Feed,
    val previousScreen: Screen? = null,
    val activeNavTab: NavTab = NavTab.FEED,
    val currentUser: User? = null,
    val contacts: List<Contact> = emptyList(),
    val conversations: List<Conversation> = emptyList(),
    val currentMessages: List<Message> = emptyList(),
    val selectedContact: Contact? = null,
    val selectedConversation: Conversation? = null,
    val notifications: List<NotificationItem> = emptyList(),
    val callRecords: List<CallRecord> = emptyList(),
    val feedPosts: List<FeedPost> = emptyList(),
    val statusUpdates: List<UserStatusUpdate> = emptyList(),
    val channels: List<WhatsAppChannel> = emptyList(),
    val activeStoryViewer: UserStatusUpdate? = null,
    val activeCommentsPost: FeedPost? = null,
    val activeReactionPostId: String? = null,
    val searchQuery: String = "",
    val searchResults: List<Contact> = emptyList(),
    val messageSearchResults: List<Message> = emptyList(),
    val isVoiceRecording: Boolean = false,
    val recordingDuration: Int = 0,
    val replyToMessage: Message? = null,
    val activeCallState: ActiveCallState = ActiveCallState(),
    val isDarkMode: Boolean = false,
    val isAuthenticated: Boolean = true,
    val toastMessage: String? = null,
    val isSimulatedTyping: Boolean = false
)

class ChatViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: ChatRepository
    private var callTimerJob: Job? = null
    private var voiceRecordJob: Job? = null

    private val _uiState = MutableStateFlow(
        UiState(
            feedPosts = SeedData.getDefaultPosts(),
            statusUpdates = SeedData.getDefaultStatusUpdates(),
            channels = SeedData.getDefaultChannels()
        )
    )
    val uiState: StateFlow<UiState> = _uiState.asStateFlow()

    init {
        val db = ChatDatabase.getDatabase(application)
        repository = ChatRepository(db.chatDao(), viewModelScope)

        observeData()
    }

    private fun observeData() {
        viewModelScope.launch {
            repository.currentUser.collect { user ->
                _uiState.update { it.copy(currentUser = user) }
            }
        }

        viewModelScope.launch {
            repository.allContacts.collect { contactsList ->
                _uiState.update { state ->
                    val query = state.searchQuery
                    val filtered = if (query.isBlank()) contactsList else contactsList.filter {
                        it.name.contains(query, ignoreCase = true) || it.statusMessage.contains(query, ignoreCase = true)
                    }
                    state.copy(contacts = contactsList, searchResults = filtered)
                }
            }
        }

        viewModelScope.launch {
            repository.allConversations.collect { convList ->
                _uiState.update { it.copy(conversations = convList) }
            }
        }

        viewModelScope.launch {
            repository.allNotifications.collect { notifList ->
                _uiState.update { it.copy(notifications = notifList) }
            }
        }

        viewModelScope.launch {
            repository.allCallRecords.collect { calls ->
                _uiState.update { it.copy(callRecords = calls) }
            }
        }
    }

    fun navigateTo(screen: Screen) {
        val current = _uiState.value.currentScreen
        _uiState.update {
            it.copy(
                previousScreen = current,
                currentScreen = screen
            )
        }

        if (screen is Screen.Chat) {
            loadChat(screen.conversationId, screen.contactId)
        } else if (screen is Screen.ContactProfile) {
            loadContactProfile(screen.contactId)
        }
    }

    fun navigateBack() {
        val current = _uiState.value.currentScreen
        val target = when (current) {
            is Screen.Chat -> _uiState.value.previousScreen ?: Screen.Conversations
            is Screen.ContactProfile -> _uiState.value.previousScreen ?: Screen.Conversations
            is Screen.ActiveCall -> Screen.Chat(
                _uiState.value.selectedConversation?.id ?: "conv_martin",
                _uiState.value.selectedContact?.id ?: "contact_martin"
            )
            is Screen.Search -> _uiState.value.previousScreen ?: Screen.Feed
            is Screen.Notifications -> Screen.Feed
            is Screen.UserProfile -> Screen.Feed
            is Screen.Calls -> Screen.Feed
            is Screen.Contacts -> Screen.Feed
            is Screen.Updates -> Screen.Feed
            is Screen.Conversations -> Screen.Feed
            else -> Screen.Feed
        }
        _uiState.update { it.copy(currentScreen = target) }
    }

    fun setActiveNavTab(tab: NavTab) {
        _uiState.update { it.copy(activeNavTab = tab) }
        when (tab) {
            NavTab.FEED -> navigateTo(Screen.Feed)
            NavTab.CONVERSATIONS -> navigateTo(Screen.Conversations)
            NavTab.UPDATES -> navigateTo(Screen.Updates)
            NavTab.CALLS -> navigateTo(Screen.Calls)
            NavTab.SETTINGS -> navigateTo(Screen.UserProfile)
        }
    }

    // --- Facebook Feed Actions ---
    fun togglePostReaction(postId: String, reaction: ReactionType) {
        _uiState.update { state ->
            val updatedPosts = state.feedPosts.map { post ->
                if (post.id == postId) {
                    if (post.userReaction == reaction) {
                        // Undo reaction
                        post.copy(
                            userReaction = null,
                            likesCount = (post.likesCount - 1).coerceAtLeast(0)
                        )
                    } else {
                        val diff = if (post.userReaction == null) 1 else 0
                        post.copy(
                            userReaction = reaction,
                            likesCount = post.likesCount + diff
                        )
                    }
                } else post
            }
            state.copy(feedPosts = updatedPosts, activeReactionPostId = null)
        }
    }

    fun openReactionPicker(postId: String?) {
        _uiState.update { it.copy(activeReactionPostId = postId) }
    }

    fun openPostComments(post: FeedPost?) {
        _uiState.update { it.copy(activeCommentsPost = post) }
    }

    fun addPostComment(postId: String, commentText: String) {
        if (commentText.isBlank()) return
        val user = _uiState.value.currentUser
        val newComment = PostComment(
            id = "c_${System.currentTimeMillis()}",
            authorName = user?.name ?: "Alex Rivera",
            authorAvatarResName = user?.avatarResName ?: "ic_chat_logo",
            authorAvatarColorHex = 0xFF1877F2,
            text = commentText.trim(),
            timeAgo = "Just now",
            likesCount = 0,
            isLiked = false
        )

        _uiState.update { state ->
            val updatedPosts = state.feedPosts.map { post ->
                if (post.id == postId) {
                    val newComments = listOf(newComment) + post.comments
                    post.copy(
                        comments = newComments,
                        commentsCount = post.commentsCount + 1
                    )
                } else post
            }
            val activePost = updatedPosts.firstOrNull { it.id == postId }
            state.copy(feedPosts = updatedPosts, activeCommentsPost = activePost)
        }
        showToast("Comment posted!")
    }

    fun likePostComment(postId: String, commentId: String) {
        _uiState.update { state ->
            val updatedPosts = state.feedPosts.map { post ->
                if (post.id == postId) {
                    val updatedComments = post.comments.map { comment ->
                        if (comment.id == commentId) {
                            val liked = !comment.isLiked
                            comment.copy(
                                isLiked = liked,
                                likesCount = if (liked) comment.likesCount + 1 else (comment.likesCount - 1).coerceAtLeast(0)
                            )
                        } else comment
                    }
                    post.copy(comments = updatedComments)
                } else post
            }
            val activePost = updatedPosts.firstOrNull { it.id == postId }
            state.copy(feedPosts = updatedPosts, activeCommentsPost = activePost)
        }
    }

    fun createNewPost(text: String, gradientIndex: Int = -1, location: String? = null) {
        if (text.isBlank()) return
        val user = _uiState.value.currentUser
        val newPost = FeedPost(
            id = "post_${System.currentTimeMillis()}",
            authorId = user?.id ?: "user_me",
            authorName = user?.name ?: "Alex Rivera",
            authorAvatarResName = user?.avatarResName ?: "ic_chat_logo",
            authorAvatarColorHex = 0xFF1877F2,
            isVerified = true,
            timeAgo = "Just now • 🌐",
            contentText = text.trim(),
            postGradientIndex = gradientIndex,
            likesCount = 1,
            commentsCount = 0,
            sharesCount = 0,
            userReaction = ReactionType.LIKE,
            topReactions = listOf(ReactionType.LIKE),
            location = location
        )

        _uiState.update { state ->
            state.copy(feedPosts = listOf(newPost) + state.feedPosts)
        }
        showToast("Post published to Feed!")
    }

    // --- WhatsApp Status Updates & Stories ---
    fun openStoryViewer(status: UserStatusUpdate?) {
        _uiState.update { state ->
            if (status != null) {
                // Mark as viewed
                val updatedList = state.statusUpdates.map {
                    if (it.id == status.id) it.copy(isViewed = true) else it
                }
                state.copy(activeStoryViewer = status, statusUpdates = updatedList)
            } else {
                state.copy(activeStoryViewer = null)
            }
        }
    }

    fun addStatusStory(text: String, gradientIndex: Int = 0) {
        if (text.isBlank()) return
        val newStory = StatusStory(
            id = "story_${System.currentTimeMillis()}",
            text = text.trim(),
            mediaGradientIndex = gradientIndex,
            timeAgo = "Just now",
            viewsCount = 1
        )

        _uiState.update { state ->
            val updated = state.statusUpdates.map { update ->
                if (update.isSelf) {
                    update.copy(
                        stories = listOf(newStory) + update.stories,
                        lastUpdatedText = "Just now"
                    )
                } else update
            }
            state.copy(statusUpdates = updated)
        }
        showToast("Status updated!")
    }

    fun replyToStatus(contactName: String, replyText: String) {
        if (replyText.isBlank()) return
        val contact = _uiState.value.contacts.firstOrNull { it.name.equals(contactName, ignoreCase = true) }
            ?: _uiState.value.contacts.firstOrNull()
        if (contact != null) {
            openChatWithContact(contact)
            sendMessage("Replied to status: $replyText")
            _uiState.update { it.copy(activeStoryViewer = null) }
            showToast("Reply sent to ${contact.name}")
        }
    }

    // --- WhatsApp Channels Actions ---
    fun toggleChannelFollow(channelId: String) {
        _uiState.update { state ->
            val updated = state.channels.map { channel ->
                if (channel.id == channelId) {
                    val following = !channel.isFollowing
                    showToast(if (following) "Following ${channel.name}" else "Unfollowed ${channel.name}")
                    channel.copy(isFollowing = following)
                } else channel
            }
            state.copy(channels = updated)
        }
    }

    // --- Messaging Actions ---
    fun loadChat(conversationId: String, contactId: String) {
        viewModelScope.launch {
            repository.getContactById(contactId).collect { contact ->
                _uiState.update { it.copy(selectedContact = contact) }
            }
        }
        viewModelScope.launch {
            repository.getConversationById(conversationId).collect { conv ->
                _uiState.update { it.copy(selectedConversation = conv) }
            }
        }
        viewModelScope.launch {
            repository.getMessagesForConversation(conversationId).collect { messages ->
                _uiState.update { it.copy(currentMessages = messages) }
            }
        }
    }

    fun loadContactProfile(contactId: String) {
        viewModelScope.launch {
            repository.getContactById(contactId).collect { contact ->
                _uiState.update { it.copy(selectedContact = contact) }
            }
        }
    }

    fun openChatWithContact(contact: Contact) {
        viewModelScope.launch {
            val convId = repository.getOrCreateConversation(contact)
            navigateTo(Screen.Chat(convId, contact.id))
        }
    }

    fun sendMessage(
        text: String,
        type: MessageType = MessageType.TEXT,
        attachmentUrl: String = "",
        attachmentName: String = "",
        voiceDurationSeconds: Int = 0,
        voiceWaveform: String = ""
    ) {
        val convId = _uiState.value.selectedConversation?.id ?: "conv_martin"
        val reply = _uiState.value.replyToMessage

        viewModelScope.launch {
            repository.sendMessage(
                conversationId = convId,
                text = text,
                type = type,
                attachmentUrl = attachmentUrl,
                attachmentName = attachmentName,
                voiceDurationSeconds = voiceDurationSeconds,
                voiceWaveform = voiceWaveform,
                replyToMessageId = reply?.id,
                replyToText = reply?.text,
                autoReply = true
            )
            _uiState.update { it.copy(replyToMessage = null) }
        }
    }

    fun setReplyTo(message: Message?) {
        _uiState.update { it.copy(replyToMessage = message) }
    }

    fun addReaction(message: Message, emoji: String) {
        viewModelScope.launch {
            repository.addReaction(message, emoji)
        }
    }

    fun deleteMessage(messageId: String) {
        viewModelScope.launch {
            repository.deleteMessage(messageId)
            showToast("Message deleted")
        }
    }

    fun updateSearchQuery(query: String) {
        _uiState.update { state ->
            val filtered = if (query.isBlank()) state.contacts else state.contacts.filter {
                it.name.contains(query, ignoreCase = true) || it.statusMessage.contains(query, ignoreCase = true)
            }
            state.copy(searchQuery = query, searchResults = filtered)
        }

        if (query.isNotBlank()) {
            viewModelScope.launch {
                repository.searchMessages(query).collect { results ->
                    _uiState.update { it.copy(messageSearchResults = results) }
                }
            }
        } else {
            _uiState.update { it.copy(messageSearchResults = emptyList()) }
        }
    }

    fun toggleContactNotification(contact: Contact, allowed: Boolean) {
        val updated = contact.copy(notificationsAllowed = allowed)
        viewModelScope.launch {
            repository.updateContact(updated)
            _uiState.update { it.copy(selectedContact = updated) }
            showToast(if (allowed) "Notifications enabled for ${contact.name}" else "Notifications muted")
        }
    }

    fun updateContactRemarks(contact: Contact, remarks: String) {
        val updated = contact.copy(remarks = remarks)
        viewModelScope.launch {
            repository.updateContact(updated)
            _uiState.update { it.copy(selectedContact = updated) }
            showToast("Remarks updated")
        }
    }

    fun updateCurrentUser(name: String, bio: String, status: String, phone: String, email: String) {
        val current = _uiState.value.currentUser ?: return
        val updated = current.copy(
            name = name,
            bio = bio,
            status = status,
            phone = phone,
            email = email
        )
        viewModelScope.launch {
            repository.updateUser(updated)
            showToast("Profile updated successfully")
        }
    }

    fun startVoiceRecording() {
        _uiState.update { it.copy(isVoiceRecording = true, recordingDuration = 0) }
        voiceRecordJob?.cancel()
        voiceRecordJob = viewModelScope.launch {
            while (_uiState.value.isVoiceRecording) {
                delay(1000)
                _uiState.update { it.copy(recordingDuration = it.recordingDuration + 1) }
            }
        }
    }

    fun cancelVoiceRecording() {
        voiceRecordJob?.cancel()
        _uiState.update { it.copy(isVoiceRecording = false, recordingDuration = 0) }
    }

    fun finishVoiceRecording() {
        val duration = _uiState.value.recordingDuration.coerceAtLeast(1)
        voiceRecordJob?.cancel()
        _uiState.update { it.copy(isVoiceRecording = false, recordingDuration = 0) }

        val waveform = (1..16).map { (20..95).random() / 100f }.joinToString(",")
        sendMessage(
            text = "Voice message ($duration s)",
            type = MessageType.VOICE,
            voiceDurationSeconds = duration,
            voiceWaveform = waveform
        )
    }

    fun startCall(contact: Contact, isVideo: Boolean) {
        _uiState.update {
            it.copy(
                activeCallState = ActiveCallState(
                    contact = contact,
                    isVideo = isVideo,
                    durationSeconds = 0,
                    isMuted = false,
                    isSpeakerOn = true
                )
            )
        }
        navigateTo(Screen.ActiveCall(contact.id, isVideo))

        callTimerJob?.cancel()
        callTimerJob = viewModelScope.launch {
            while (true) {
                delay(1000)
                _uiState.update {
                    it.copy(
                        activeCallState = it.activeCallState.copy(
                            durationSeconds = it.activeCallState.durationSeconds + 1
                        )
                    )
                }
            }
        }
    }

    fun toggleMute() {
        _uiState.update {
            it.copy(
                activeCallState = it.activeCallState.copy(
                    isMuted = !it.activeCallState.isMuted
                )
            )
        }
    }

    fun toggleSpeaker() {
        _uiState.update {
            it.copy(
                activeCallState = it.activeCallState.copy(
                    isSpeakerOn = !it.activeCallState.isSpeakerOn
                )
            )
        }
    }

    fun toggleVideoPause() {
        _uiState.update {
            it.copy(
                activeCallState = it.activeCallState.copy(
                    isVideoPaused = !it.activeCallState.isVideoPaused
                )
            )
        }
    }

    fun endCall() {
        val state = _uiState.value.activeCallState
        callTimerJob?.cancel()
        if (state.contact != null) {
            viewModelScope.launch {
                repository.logCall(
                    contactId = state.contact.id,
                    contactName = state.contact.name,
                    contactAvatarRes = state.contact.avatarResName,
                    isVideo = state.isVideo,
                    durationSeconds = state.durationSeconds
                )
            }
        }
        navigateBack()
    }

    fun toggleDarkMode() {
        _uiState.update { it.copy(isDarkMode = !it.isDarkMode) }
    }

    fun markNotificationAsRead(id: String) {
        viewModelScope.launch {
            repository.markNotificationAsRead(id)
        }
    }

    fun markAllNotificationsAsRead() {
        viewModelScope.launch {
            repository.markAllNotificationsAsRead()
            showToast("All notifications marked as read")
        }
    }

    fun loginDemoUser() {
        _uiState.update { it.copy(isAuthenticated = true) }
        navigateTo(Screen.Feed)
        showToast("Logged in as Alex Rivera")
    }

    fun logout() {
        _uiState.update { it.copy(isAuthenticated = false) }
        navigateTo(Screen.Auth)
    }

    fun showToast(message: String) {
        _uiState.update { it.copy(toastMessage = message) }
    }

    fun clearToast() {
        _uiState.update { it.copy(toastMessage = null) }
    }

    fun addNewContact(name: String, status: String, phone: String) {
        if (name.isBlank()) return
        val letter = name.trim().first().uppercase()
        val colors = listOf(0xFF1877F2, 0xFFFF86A8, 0xFFA88BFF, 0xFF25D366, 0xFFFBBF24, 0xFF38BDF8)
        val newContact = Contact(
            id = "contact_" + System.currentTimeMillis(),
            name = name.trim(),
            username = name.lowercase().replace(" ", "_"),
            avatarColorHex = colors.random(),
            statusMessage = if (status.isBlank()) "Hey there! I am using Chat" else status.trim(),
            isOnline = true,
            initialLetter = letter,
            phone = if (phone.isBlank()) "+1 (555) 000-0000" else phone.trim()
        )
        viewModelScope.launch {
            repository.insertContact(newContact)
            showToast("Contact $name added!")
        }
    }
}
