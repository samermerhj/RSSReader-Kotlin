package com.example.rssreader

import android.app.Application
import com.example.rssreader.data.AppDatabase
import com.example.rssreader.data.repository.RssRepository

class RssApplication : Application() {
    val database by lazy { AppDatabase.getInstance(this) }
    val repository by lazy { RssRepository(database.newsDao(), database.rssSourceDao()) }
}