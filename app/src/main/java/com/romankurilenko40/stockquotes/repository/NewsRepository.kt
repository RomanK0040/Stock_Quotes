package com.romankurilenko40.stockquotes.repository

import android.util.Log
import com.romankurilenko40.stockquotes.domain.News
import com.romankurilenko40.stockquotes.network.FinnhubApi
import com.romankurilenko40.stockquotes.network.asNewsList
import kotlinx.coroutines.flow.*
import java.io.IOException


class NewsRepository {
    private val TAG = "NewsRepository"

    /**
     * Search news concerning general category, exposed as a stream of data
     */
    fun getNews(category: String): Flow<List<News>> = flow {
            val data = fetchNewsByCategory(category)
            emit(data)
        }


    /**
     * Fetch actual news from network to save in cache
     */
    private suspend fun fetchNewsByCategory(category: String): List<News> {
        val defaultMinId = 10
        var response = listOf<News>()
        try {
            response = FinnhubApi.apiService.getMarketNews(category, defaultMinId).asNewsList()
            Log.d(TAG, "Received news with category $category: $response")
        } catch (exception: IOException) {
            Log.e(TAG, "IO exception: ${exception.message}")
        }
        return response
    }

}