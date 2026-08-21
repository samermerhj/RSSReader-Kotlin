package com.example.rssreader.data.dao

import androidx.room.*
import com.example.rssreader.data.model.NewsItem

@Dao
interface NewsDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(items: List<NewsItem>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(item: NewsItem)

    @Query("SELECT * FROM news_archive ORDER BY archiveDate DESC, pubDate DESC")
    suspend fun getAllNews(): List<NewsItem>

    @Query("SELECT * FROM news_archive WHERE archiveDate = :date ORDER BY pubDate DESC")
    suspend fun getNewsByDate(date: String): List<NewsItem>

    @Query("SELECT * FROM news_archive ORDER BY id DESC LIMIT :limit")
    suspend fun getLatestNews(limit: Int): List<NewsItem>

    @Query("SELECT DISTINCT archiveDate FROM news_archive ORDER BY archiveDate DESC")
    suspend fun getDistinctDates(): List<String>

    @Query("DELETE FROM news_archive WHERE archiveDate < :cutoff")
    suspend fun deleteOldNews(cutoff: String)
}