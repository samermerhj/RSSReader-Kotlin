package com.example.rssreader

import android.os.Bundle
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.rssreader.databinding.ActivityMainBinding
import com.example.rssreader.ui.NewsAdapter
import com.example.rssreader.ui.RssViewModel
import com.example.rssreader.ui.RssViewModelFactory
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    private val viewModel: RssViewModel by viewModels {
        try {
            RssViewModelFactory((application as RssApplication).repository)
        } catch (e: Exception) {
            // في حال فشل الحصول على التطبيق (مثلاً إذا لم يكن RssApplication مسجلاً)
            throw RuntimeException("فشل تهيئة ViewModel: ${e.message}", e)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // معالج الأخطاء العام - يعرض الخطأ على الشاشة عند حدوث أي انهيار
        Thread.setDefaultUncaughtExceptionHandler { _, throwable ->
            val stackTrace = throwable.stackTraceToString()
            runOnUiThread {
                try {
                    val tv = TextView(this)
                    tv.text = "❌ خطأ غير متوقع:\n\n$stackTrace"
                    tv.setPadding(30, 30, 30, 30)
                    tv.textSize = 12f
                    setContentView(tv)
                } catch (e: Exception) {
                    // تجاهل
                }
            }
            // إبقاء التطبيق مفتوحًا لمدة 8 ثوان لعرض الخطأ
            Thread.sleep(8000)
            finish()
        }

        try {
            binding = ActivityMainBinding.inflate(layoutInflater)
            setContentView(binding.root)

            // إنشاء المحول مع رد فعل عند الضغط على خبر
            val newsAdapter = NewsAdapter(emptyList()) { item ->
                Toast.makeText(this, item.title, Toast.LENGTH_SHORT).show()
            }

            // إعداد RecyclerView
            binding.recyclerViewNews.apply {
                layoutManager = LinearLayoutManager(this@MainActivity)
                adapter = newsAdapter
            }

            // مراقبة قائمة الأخبار
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

        } catch (e: Exception) {
            // عرض الخطأ على الشاشة إذا فشل onCreate
            val tv = TextView(this)
            tv.text = "❌ خطأ أثناء التهيئة:\n\n${e.stackTraceToString()}"
            tv.setPadding(30, 30, 30, 30)
            tv.textSize = 12f
            setContentView(tv)
        }
    }

    /**
     * مصادر RSS الافتراضية
     */
    private fun defaultSources(): List<Pair<String, String>> {
        return listOf(
            "BBC علوم" to "http://www.bbc.co.uk/arabic/scienceandtech/index.xml",
            "روسيا اليوم" to "https://arabic.rt.com/rss/",
            "سكاي نيوز عربية" to "https://www.skynewsarabia.com/rss"
        )
    }
}