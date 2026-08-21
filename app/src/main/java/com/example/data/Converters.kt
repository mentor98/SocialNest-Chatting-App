package com.example.data

import androidx.room.TypeConverter
import com.example.model.MessageStatus
import com.example.model.MessageType
import com.example.model.NotificationType

class Converters {
    @TypeConverter
    fun fromMessageType(value: MessageType): String = value.name

    @TypeConverter
    fun toMessageType(value: String): MessageType = runCatching { MessageType.valueOf(value) }.getOrDefault(MessageType.TEXT)

    @TypeConverter
    fun fromMessageStatus(value: MessageStatus): String = value.name

    @TypeConverter
    fun toMessageStatus(value: String): MessageStatus = runCatching { MessageStatus.valueOf(value) }.getOrDefault(MessageStatus.SENT)

    @TypeConverter
    fun fromNotificationType(value: NotificationType): String = value.name

    @TypeConverter
    fun toNotificationType(value: String): NotificationType = runCatching { NotificationType.valueOf(value) }.getOrDefault(NotificationType.MESSAGE)
}
