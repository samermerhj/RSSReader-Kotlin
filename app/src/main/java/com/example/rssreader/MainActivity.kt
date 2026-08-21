package com.example.rssreader

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.rssreader.data.model.NewsItem
import com.example.rssreader.databinding.ActivityMainBinding
import com.example.rssreader.ui.NewsAdapter
import com.example.rssreader.ui.RssViewModel
import com.example.rssreader.ui.RssViewModelFactory
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private val viewModel: RssViewModel by viewModels {
        RssViewModelFactory((application as RssApplication).repository)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val newsAdapter = NewsAdapter(emptyList()) { item ->
            // عند الضغط على الخبر – سيفتح المتصفح لاحقاً
            Toast.makeText(this, item.title, Toast.LENGTH_SHORT).show()
        }

        binding.recyclerViewNews.apply {
            layoutManager = LinearLayoutManager(this@MainActivity)
            adapter = newsAdapter
        }

        // مراقبة الأخبار
        lifecycleScope.launch {
            viewModel.newsList.collect { news ->
                newsAdapter.updateList(news)
            }
        }

        // مراقبة حالة التحميل
        lifecycleScope.launch {
            viewModel.isLoading.collect { loading ->
                binding.progressBar.visibility = if (loading) View.VISIBLE else View.GONE
            }
        }

        // زر التحديث
        binding.btnRefresh.setOnClickListener {
            viewModel.fetchAndSaveNews(defaultSources())
        }

        // تحميل الأخبار المحفوظة عند البدء
        viewModel.loadLatestNews()
    }

    private fun defaultSources(): List<Pair<String, String>> {
        return listOf(
            "BBC علوم" to "http://www.bbc.co.uk/arabic/scienceandtech/index.xml",
            "روسيا اليوم" to "https://arabic.rt.com/rss/",
            "سكاي نيوز عربية" to "https://www.skynewsarabia.com/rss"
        )
    }
}
