package com.example.rssreader.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.rssreader.data.model.NewsItem
import com.example.rssreader.data.network.RssFetcher
import com.example.rssreader.data.repository.RssRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class RssViewModel(private val repository: RssRepository) : ViewModel() {

    private val rssFetcher = RssFetcher()

    private val _newsList = MutableStateFlow<List<NewsItem>>(emptyList())
    val newsList: StateFlow<List<NewsItem>> = _newsList

    private val _sources = MutableStateFlow<List<String>>(emptyList())
    val sources: StateFlow<List<String>> = _sources

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    init {
        loadSources()
        loadLatestNews()
    }

    fun loadSources() {
        viewModelScope.launch {
            _sources.value = repository.getAllSources().map { it.name }
        }
    }

    fun loadLatestNews() {
        viewModelScope.launch {
            _newsList.value = repository.getLatestNews()
        }
    }

    /**
     * جلب الأخبار من المصادر وحفظها
     */
    fun fetchAndSaveNews(sources: List<Pair<String, String>>) {
        if (sources.isEmpty()) return

        viewModelScope.launch {
            _isLoading.value = true
            val allItems = mutableListOf<NewsItem>()

            // تاريخ اليوم بصيغة ISO متوافقة مع جميع الإصدارات
            val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.US)
            val todayStr = dateFormat.format(Date())

            for ((name, url) in sources) {
                try {
                    val items = rssFetcher.fetchFeed(url, name)
                    items.forEach { item ->
                        allItems.add(
                            item.copy(
                                description = rssFetcher.cleanHtml(item.description),
                                archiveDate = todayStr
                            )
                        )
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }

            if (allItems.isNotEmpty()) {
                repository.saveArticles(allItems)
            }

            _isLoading.value = false
            loadLatestNews()
        }
    }
}
