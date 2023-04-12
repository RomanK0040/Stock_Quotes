package com.romankurilenko40.stockquotes.ui.newsfeed

import android.content.Intent
import android.net.Uri
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.annotation.LayoutRes
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.romankurilenko40.stockquotes.R
import com.romankurilenko40.stockquotes.databinding.NewsItemBinding
import com.romankurilenko40.stockquotes.domain.News

class NewsListAdapter: ListAdapter<News, NewsViewHolder>(DIFF_CALLBACK) {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NewsViewHolder {
        val dataBinding: NewsItemBinding = DataBindingUtil.inflate(
            LayoutInflater.from(parent.context),
            NewsViewHolder.LAYOUT,
            parent,
            false
        )
        return NewsViewHolder(dataBinding)
    }

    override fun onBindViewHolder(holder: NewsViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    companion object {
        private val DIFF_CALLBACK = object : DiffUtil.ItemCallback<News>() {
            override fun areItemsTheSame(oldItem: News, newItem: News) =
                oldItem.id == newItem.id

            override fun areContentsTheSame(oldItem: News, newItem: News) = oldItem == newItem
        }
    }
}

class NewsViewHolder(private val binding: NewsItemBinding): RecyclerView.ViewHolder(binding.root) {
    private lateinit var news: News

    fun bind(item: News) {
        binding.apply {
            newsItem = item
            news = item
            executePendingBindings()
        }
    }

    init {
        binding.newsHeadline.setOnClickListener {
            news.url.let { url ->
                val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
                it.context.startActivity(intent)
            }
        }
    }

    companion object {
        @LayoutRes
        val LAYOUT = R.layout.news_item
    }

}