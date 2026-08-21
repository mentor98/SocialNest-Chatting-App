package com.example.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "call_records")
data class CallRecord(
    @PrimaryKey val id: String,
    val contactId: String,
    val contactName: String,
    val contactAvatarResName: String = "",
    val contactAvatarColorHex: Long = 0xFF568CF5,
    val isVideo: Boolean = false,
    val isIncoming: Boolean = true,
    val isMissed: Boolean = false,
    val durationSeconds: Int = 0,
    val timestamp: Long = System.currentTimeMillis()
)
