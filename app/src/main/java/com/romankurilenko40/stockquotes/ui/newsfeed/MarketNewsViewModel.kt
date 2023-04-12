package com.romankurilenko40.stockquotes.ui.newsfeed

import android.util.Log
import androidx.lifecycle.*
import com.romankurilenko40.stockquotes.domain.News
import com.romankurilenko40.stockquotes.repository.NewsRepository
import kotlinx.coroutines.flow.collect

private const val DEFAULT_NEWS_CATEGORY = "general"

class MarketNewsViewModel(
    repository: NewsRepository
): ViewModel() {
    private val TAG = "MarketNewsViewModel"

    private val category = MutableLiveData<String>()

    fun setCategory(category: String) {
        this.category.value = category
    }


    private val _news: LiveData<List<News>> = category.switchMap { category ->
        Log.i(TAG, "news list requested")
        repository.getNews(category).asLiveData()
    }
    val news: LiveData<List<News>>
        get() = _news

    init {
        setCategory(DEFAULT_NEWS_CATEGORY)
    }

    companion object {
        val Factory = object : ViewModelProvider.Factory {
            @Suppress("UNCHECKED_CAST")
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                return MarketNewsViewModel(NewsRepository()) as T
            }
        }
    }
}