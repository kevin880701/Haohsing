package com.clockworkorange.domain.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.clockworkorange.domain.BuildConfig
import timber.log.Timber
import java.util.concurrent.Executors

@Database(entities = [ReadMessageId::class], version = 1, exportSchema = false)
abstract class AppDatabase: RoomDatabase() {

    abstract fun readMessageIdDao(): ReadMessageIdDao

    companion object{
        fun create(context: Context): AppDatabase{

            val builder = Room.databaseBuilder(context, AppDatabase::class.java, "haohsing")
                .fallbackToDestructiveMigration()

            if (BuildConfig.DEBUG){
                builder.setQueryCallback(
                    { sqlQuery, bindArgs -> Timber.d("SQL Query: $sqlQuery SQL Args: $bindArgs") },
                    Executors.newSingleThreadExecutor()
                )
            }
            return builder.build()
        }

    }
}