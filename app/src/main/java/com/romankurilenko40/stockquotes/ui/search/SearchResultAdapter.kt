package com.romankurilenko40.stockquotes.ui.search

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.romankurilenko40.stockquotes.R
import com.romankurilenko40.stockquotes.databinding.SearchItemBinding
import com.romankurilenko40.stockquotes.databinding.StockItemBinding
import com.romankurilenko40.stockquotes.domain.Stock
import com.romankurilenko40.stockquotes.network.SearchResultItem



class SearchResultAdapter(
    private val onItemClick: (SearchResultItem) -> Unit): ListAdapter<SearchResultItem, SearchViewHolder>(DIFF_CALLBACK) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SearchViewHolder {
        val dataBinding: SearchItemBinding = DataBindingUtil.inflate(
            LayoutInflater.from(parent.context),
            R.layout.search_item,
            parent,
            false)
        return SearchViewHolder(dataBinding, onItemClick)
    }

    override fun onBindViewHolder(holder: SearchViewHolder, position: Int) {
        holder.bind(currentList[position])
    }


    companion object {
        private val DIFF_CALLBACK = object : DiffUtil.ItemCallback<SearchResultItem>() {
            override fun areItemsTheSame(oldItem: SearchResultItem, newItem: SearchResultItem) =
                oldItem.symbol == newItem.symbol

            override fun areContentsTheSame(oldItem: SearchResultItem, newItem: SearchResultItem) = oldItem == newItem
        }
    }
}

class SearchViewHolder(
    private val binding: SearchItemBinding,
    val onItemClick: (SearchResultItem) -> Unit): RecyclerView.ViewHolder(binding.root) {

    private lateinit var searchResult: SearchResultItem

    init {
        binding.root.setOnClickListener {
            onItemClick(searchResult)
        }
    }

    fun bind(item: SearchResultItem) {
        binding.apply {
            searchResult = item
            searchItem = item

            executePendingBindings()
        }
    }

}