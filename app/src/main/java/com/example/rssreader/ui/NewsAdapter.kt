package com.example.rssreader.ui

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.rssreader.data.model.NewsItem
import com.example.rssreader.databinding.ItemNewsBinding

class NewsAdapter(
    private var items: List<NewsItem> = emptyList(),
    private val onClick: (NewsItem) -> Unit
) : RecyclerView.Adapter<NewsAdapter.NewsViewHolder>() {

    inner class NewsViewHolder(
        private val binding: ItemNewsBinding
    ) : RecyclerView.ViewHolder(binding.root) {
        fun bind(item: NewsItem) {
            binding.textTitle.text = item.title
            binding.textDescription.text = item.description
            binding.textSource.text = "${item.source} - ${item.pubDate}"
            binding.root.setOnClickListener { onClick(item) }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NewsViewHolder {
        val binding = ItemNewsBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return NewsViewHolder(binding)
    }

    override fun onBindViewHolder(holder: NewsViewHolder, position: Int) {
        holder.bind(items[position])
    }

    override fun getItemCount(): Int = items.size

    fun updateList(newItems: List<NewsItem>) {
        items = newItems
        notifyDataSetChanged()
    }
}
