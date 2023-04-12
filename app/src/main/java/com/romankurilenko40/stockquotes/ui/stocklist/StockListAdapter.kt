package com.romankurilenko40.stockquotes.ui.stocklist

import android.graphics.Color
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil

import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.romankurilenko40.stockquotes.R
import com.romankurilenko40.stockquotes.databinding.StockItemBinding
import com.romankurilenko40.stockquotes.domain.Stock

class StockListAdapter(
    private val onItemClick: (Stock) -> Unit,
    private val onBookmarkClick: (String) -> Unit): ListAdapter<Stock, StockViewHolder>(DIFF_CALLBACK) {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): StockViewHolder {
        val dataBinding: StockItemBinding = DataBindingUtil.inflate(
                LayoutInflater.from(parent.context),
                R.layout.stock_item,
                parent,
                false)
        return StockViewHolder(dataBinding, onItemClick, onBookmarkClick)
    }

    override fun onBindViewHolder(holder: StockViewHolder, position: Int) {
        holder.bind(currentList[position])
    }



    companion object {
        private val DIFF_CALLBACK = object : DiffUtil.ItemCallback<Stock>() {
            override fun areItemsTheSame(oldItem: Stock, newItem: Stock) =
                    oldItem.symbol == newItem.symbol

            override fun areContentsTheSame(oldItem: Stock, newItem: Stock) = oldItem == newItem
        }
    }

}

class StockViewHolder(val binding: StockItemBinding,
                      val onItemClick: (Stock) -> Unit,
                      val onBookmarkClick: (String) -> Unit): RecyclerView.ViewHolder(binding.root) {

    private lateinit var stock: Stock


    init {
        binding.root.setOnClickListener {
            onItemClick(stock)
        }

        binding.favoriteIcon.setOnClickListener {
            onBookmarkClick(stock.symbol)
            if (stock.inBookmark) {
                binding.favoriteIcon.setImageResource(R.drawable.ic_bookmark_added_36)
            } else {
                binding.favoriteIcon.setImageResource(R.drawable.ic_bookmark_not_added_36)
            }
        }
    }

    fun bind(item: Stock) {
        binding.apply {
            stockItem = item
            stock = item
            /*
            item.quote.pricePercentChange?.let { priceChange ->
                if (priceChange > 0) {
                    this.priceChange.setTextColor(Color.parseColor("#008000"))
                    currentPrice.setTextColor(Color.parseColor("#008000"))
                }
                if (priceChange < 0) {
                    this.priceChange.setTextColor(Color.parseColor("#ff0000"))
                    currentPrice.setTextColor(Color.parseColor("#ff0000"))
                }
            }
             */
            executePendingBindings()
        }

    }

}
