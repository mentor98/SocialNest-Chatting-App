package com.example.data

import androidx.room.*
import com.example.model.*
import kotlinx.coroutines.flow.Flow

@Dao
interface ChatDao {
    // User
    @Query("SELECT * FROM users WHERE id = 'user_me' LIMIT 1")
    fun getCurrentUser(): Flow<User?>

    @Query("SELECT * FROM users WHERE id = 'user_me' LIMIT 1")
    suspend fun getCurrentUserSync(): User?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertUser(user: User)

    @Update
    suspend fun updateUser(user: User)

    // Contacts
    @Query("SELECT * FROM contacts ORDER BY name ASC")
    fun getAllContacts(): Flow<List<Contact>>

    @Query("SELECT * FROM contacts WHERE id = :id LIMIT 1")
    fun getContactById(id: String): Flow<Contact?>

    @Query("SELECT * FROM contacts WHERE id = :id LIMIT 1")
    suspend fun getContactByIdSync(id: String): Contact?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertContacts(contacts: List<Contact>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertContact(contact: Contact)

    @Update
    suspend fun updateContact(contact: Contact)

    @Delete
    suspend fun deleteContact(contact: Contact)

    // Conversations
    @Query("SELECT * FROM conversations ORDER BY lastMessageTime DESC")
    fun getAllConversations(): Flow<List<Conversation>>

    @Query("SELECT * FROM conversations WHERE id = :id LIMIT 1")
    fun getConversationById(id: String): Flow<Conversation?>

    @Query("SELECT * FROM conversations WHERE contactId = :contactId LIMIT 1")
    suspend fun getConversationByContactId(contactId: String): Conversation?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertConversation(conversation: Conversation)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertConversations(conversations: List<Conversation>)

    @Update
    suspend fun updateConversation(conversation: Conversation)

    @Query("DELETE FROM conversations WHERE id = :id")
    suspend fun deleteConversationById(id: String)

    // Messages
    @Query("SELECT * FROM messages WHERE conversationId = :conversationId ORDER BY timestamp ASC")
    fun getMessagesForConversation(conversationId: String): Flow<List<Message>>

    @Query("SELECT * FROM messages WHERE text LIKE '%' || :query || '%' ORDER BY timestamp DESC")
    fun searchMessages(query: String): Flow<List<Message>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMessage(message: Message)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMessages(messages: List<Message>)

    @Update
    suspend fun updateMessage(message: Message)

    @Query("DELETE FROM messages WHERE id = :id")
    suspend fun deleteMessageById(id: String)

    // Notifications
    @Query("SELECT * FROM notifications ORDER BY timestamp DESC")
    fun getAllNotifications(): Flow<List<NotificationItem>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertNotifications(notifications: List<NotificationItem>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertNotification(notification: NotificationItem)

    @Query("UPDATE notifications SET isRead = 1 WHERE id = :id")
    suspend fun markNotificationAsRead(id: String)

    @Query("UPDATE notifications SET isRead = 1")
    suspend fun markAllNotificationsAsRead()

    // Calls
    @Query("SELECT * FROM call_records ORDER BY timestamp DESC")
    fun getAllCallRecords(): Flow<List<CallRecord>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCallRecord(callRecord: CallRecord)
}
