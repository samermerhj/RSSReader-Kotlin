package com.example.rssreader.data.repository

import com.example.rssreader.data.dao.NewsDao
import com.example.rssreader.data.dao.RssSourceDao
import com.example.rssreader.data.model.NewsItem
import com.example.rssreader.data.model.RssSource
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class RssRepository(
    private val newsDao: NewsDao,
    private val sourceDao: RssSourceDao
) {
    suspend fun getAllSources(): List<RssSource> = sourceDao.getAllSources()

    suspend fun addSource(name: String, url: String) {
        sourceDao.insert(RssSource(name = name, url = url))
    }

    suspend fun deleteSource(source: RssSource) = sourceDao.delete(source)

    suspend fun saveArticles(items: List<NewsItem>) = newsDao.insertAll(items)

    suspend fun getLatestNews(limit: Int = 100): List<NewsItem> = newsDao.getLatestNews(limit)

    suspend fun getNewsByDate(date: String): List<NewsItem> = newsDao.getNewsByDate(date)

    suspend fun getDistinctDates(): List<String> = newsDao.getDistinctDates()

    suspend fun deleteOldNews(days: Int) {
        val calendar = Calendar.getInstance()
        calendar.add(Calendar.DAY_OF_YEAR, -days)
        val cutoff = SimpleDateFormat("yyyy-MM-dd", Locale.US).format(calendar.time)
        newsDao.deleteOldNews(cutoff)
    }
}
