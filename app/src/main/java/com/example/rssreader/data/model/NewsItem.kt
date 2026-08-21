package com.example.rssreader.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "news_archive")
data class NewsItem(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val source: String = "",
    val title: String = "",
    val description: String = "",
    val link: String = "",
    val pubDate: String = "",
    val archiveDate: String = "",
    val imageUrl: String = ""
)