package com.example.rssreader.data.dao

import androidx.room.*
import com.example.rssreader.data.model.RssSource

@Dao
interface RssSourceDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(sources: List<RssSource>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(source: RssSource)

    @Delete
    suspend fun delete(source: RssSource)

    @Query("SELECT * FROM rss_sources ORDER BY name")
    suspend fun getAllSources(): List<RssSource>
}