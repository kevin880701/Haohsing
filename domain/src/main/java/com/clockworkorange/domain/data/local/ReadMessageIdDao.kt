package com.clockworkorange.domain.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

@Dao
interface ReadMessageIdDao {

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(readMessage: ReadMessageId)

    @Query("SELECT message_id FROM read_message_id")
    fun getAllReadIds(): Flow<List<ReadMessageId>>
}