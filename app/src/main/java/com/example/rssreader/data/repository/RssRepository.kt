package com.example.rssreader.data.repository

import com.example.rssreader.data.dao.NewsDao
import com.example.rssreader.data.dao.RssSourceDao
import com.example.rssreader.data.model.NewsItem
import com.example.rssreader.data.model.RssSource

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
        val cutoff = java.time.LocalDate.now().minusDays(days.toLong()).toString()
        newsDao.deleteOldNews(cutoff)
    }
}