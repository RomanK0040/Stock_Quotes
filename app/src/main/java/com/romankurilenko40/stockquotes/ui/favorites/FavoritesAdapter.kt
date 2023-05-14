package com.romankurilenko40.stockquotes.ui.favorites

import androidx.recyclerview.widget.ListAdapter
import com.romankurilenko40.stockquotes.domain.Stock
import com.romankurilenko40.stockquotes.ui.stocklist.StockListAdapter

class FavoritesAdapter(
    private val onItemClick: (Stock) -> Unit,
    private val onBookmarkClick: (Stock) -> Unit): StockListAdapter(onItemClick, onBookmarkClick)