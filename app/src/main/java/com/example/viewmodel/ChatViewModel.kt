package com.example.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.ChatDatabase
import com.example.data.ChatRepository
import com.example.model.*
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

sealed class Screen {
    object Splash : Screen()
    object Auth : Screen()
    object Conversations : Screen()
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
    CONVERSATIONS,
    CALLS,
    CONTACTS,
    NOTIFICATIONS,
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
    val currentScreen: Screen = Screen.Conversations,
    val previousScreen: Screen? = null,
    val activeNavTab: NavTab = NavTab.CONVERSATIONS,
    val currentUser: User? = null,
    val contacts: List<Contact> = emptyList(),
    val conversations: List<Conversation> = emptyList(),
    val currentMessages: List<Message> = emptyList(),
    val selectedContact: Contact? = null,
    val selectedConversation: Conversation? = null,
    val notifications: List<NotificationItem> = emptyList(),
    val callRecords: List<CallRecord> = emptyList(),
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

    private val _uiState = MutableStateFlow(UiState())
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
            is Screen.Search -> _uiState.value.previousScreen ?: Screen.Conversations
            is Screen.Notifications -> Screen.Conversations
            is Screen.UserProfile -> Screen.Conversations
            is Screen.Calls -> Screen.Conversations
            is Screen.Contacts -> Screen.Conversations
            is Screen.NewConversation -> Screen.Conversations
            else -> Screen.Conversations
        }
        _uiState.update { it.copy(currentScreen = target) }
    }

    fun setActiveNavTab(tab: NavTab) {
        _uiState.update { it.copy(activeNavTab = tab) }
        when (tab) {
            NavTab.CONVERSATIONS -> navigateTo(Screen.Conversations)
            NavTab.CALLS -> navigateTo(Screen.Calls)
            NavTab.CONTACTS -> navigateTo(Screen.Contacts)
            NavTab.NOTIFICATIONS -> navigateTo(Screen.Notifications)
            NavTab.SETTINGS -> navigateTo(Screen.UserProfile)
        }
    }

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

        // Generate synthetic waveform
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
        navigateTo(Screen.Contacts)
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
        val colors = listOf(0xFF568CF5, 0xFFFF86A8, 0xFFA88BFF, 0xFF4ADE80, 0xFFFBBF24, 0xFF38BDF8)
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
