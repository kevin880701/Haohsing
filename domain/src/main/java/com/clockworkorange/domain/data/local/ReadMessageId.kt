package com.clockworkorange.domain.data.local

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "read_message_id")
data class ReadMessageId(

    @PrimaryKey
    @ColumnInfo(name = "message_id")
    val messageId: Int
)