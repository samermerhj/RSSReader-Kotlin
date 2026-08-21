package com.example.rssreader.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "rss_sources")
data class RssSource(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val name: String,
    val url: String
)