package com.example.rssreader.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.rssreader.data.dao.NewsDao
import com.example.rssreader.data.dao.RssSourceDao
import com.example.rssreader.data.model.NewsItem
import com.example.rssreader.data.model.RssSource

@Database(
    entities = [NewsItem::class, RssSource::class],
    version = 1,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun newsDao(): NewsDao
    abstract fun rssSourceDao(): RssSourceDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getInstance(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                INSTANCE ?: Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "rssreader.db"
                ).build().also { INSTANCE = it }
            }
        }
    }
}