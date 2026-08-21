package com.example.rssreader.data.network

import com.example.rssreader.data.model.NewsItem
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.Request
import org.xmlpull.v1.XmlPullParser
import org.xmlpull.v1.XmlPullParserFactory
import java.io.StringReader
import java.net.URL
import java.util.concurrent.TimeUnit

class RssFetcher {

    private val client = OkHttpClient.Builder()
        .connectTimeout(10, TimeUnit.SECONDS)
        .readTimeout(10, TimeUnit.SECONDS)
        .build()

    /**
     * جلب أخبار من مصدر RSS/Atom محدد
     */
    suspend fun fetchFeed(url: String, sourceName: String): List<NewsItem> = withContext(Dispatchers.IO) {
        try {
            val request = Request.Builder()
                .url(url)
                .header("User-Agent", "Mozilla/5.0 (Android)")
                .build()

            client.newCall(request).execute().use { response ->
                if (!response.isSuccessful) {
                    return@withContext emptyList()
                }
                val body = response.body?.string() ?: return@withContext emptyList()
                return@withContext parseFeed(body, sourceName)
            }
        } catch (e: Exception) {
            e.printStackTrace()
            return@withContext emptyList()
        }
    }

    /**
     * تحليل XML (يدعم RSS 2.0 و Atom)
     */
    private fun parseFeed(xmlData: String, sourceName: String): List<NewsItem> {
        val items = mutableListOf<NewsItem>()

        try {
            val factory = XmlPullParserFactory.newInstance()
            val parser = factory.newPullParser()
            parser.setInput(StringReader(xmlData))

            var eventType = parser.eventType
            var currentItem: NewsItem? = null
            var tagName: String? = null

            while (eventType != XmlPullParser.END_DOCUMENT) {
                when (eventType) {
                    XmlPullParser.START_TAG -> {
                        tagName = parser.name

                        if (tagName == "item" || tagName == "entry") {
                            currentItem = NewsItem(source = sourceName)
                        }

                        // التعامل مع وسائط الصور
                        if (tagName == "enclosure" && currentItem != null) {
                            val type = parser.getAttributeValue(null, "type")
                            val url = parser.getAttributeValue(null, "url")
                            if (type?.startsWith("image") == true && url != null) {
                                currentItem = currentItem.copy(imageUrl = url)
                            }
                        }

                        if (tagName == "media:content" && currentItem != null) {
                            val url = parser.getAttributeValue(null, "url")
                            if (url != null) {
                                currentItem = currentItem.copy(imageUrl = url)
                            }
                        }

                        if (tagName == "media:thumbnail" && currentItem != null) {
                            val url = parser.getAttributeValue(null, "url")
                            if (url != null && currentItem.imageUrl.isEmpty()) {
                                currentItem = currentItem.copy(imageUrl = url)
                            }
                        }

                        // روابط Atom
                        if (tagName == "link" && currentItem != null) {
                            val rel = parser.getAttributeValue(null, "rel")
                            val href = parser.getAttributeValue(null, "href")
                            if (rel == "enclosure" && href != null && href.startsWith("http")) {
                                currentItem = currentItem.copy(imageUrl = href)
                            }
                            if (rel == "alternate" && href != null && currentItem.link.isEmpty()) {
                                currentItem = currentItem.copy(link = href)
                            }
                        }
                    }

                    XmlPullParser.TEXT -> {
                        if (currentItem != null && tagName != null) {
                            val text = parser.text.trim()
                            when (tagName) {
                                "title" -> currentItem = currentItem.copy(title = (currentItem.title + " " + text).trim())
                                "description", "summary", "content" ->
                                    currentItem = currentItem.copy(description = (currentItem.description + " " + text).trim())
                                "link" -> if (currentItem.link.isEmpty()) currentItem = currentItem.copy(link = text)
                                "pubDate", "published", "updated" ->
                                    currentItem = currentItem.copy(pubDate = text)
                                "dc:date" -> if (currentItem.pubDate.isEmpty()) currentItem = currentItem.copy(pubDate = text)
                            }
                        }
                    }

                    XmlPullParser.END_TAG -> {
                        if ((parser.name == "item" || parser.name == "entry") && currentItem != null) {
                            if (currentItem.title.isNotEmpty() && currentItem.link.isNotEmpty()) {
                                items.add(currentItem)
                            }
                            currentItem = null
                        }
                        tagName = null
                    }
                }
                eventType = parser.next()
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }

        return items
    }

    /**
     * تنظيف وصف HTML وإزالة الوسوم
     */
    fun cleanHtml(html: String): String {
        return html
            .replace(Regex("<[^>]*>"), "")
            .replace("&nbsp;", " ")
            .replace("&amp;", "&")
            .replace("&lt;", "<")
            .replace("&gt;", ">")
            .replace("&quot;", "\"")
            .replace(Regex("\\s+"), " ")
            .trim()
    }
}