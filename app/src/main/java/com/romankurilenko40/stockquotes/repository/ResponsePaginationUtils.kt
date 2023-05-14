package com.romankurilenko40.stockquotes.repository

import android.util.Log
import com.romankurilenko40.stockquotes.domain.Quote
import com.romankurilenko40.stockquotes.domain.Stock
import com.romankurilenko40.stockquotes.network.FinnhubApi
import retrofit2.HttpException

private const val STARTING_PAGE_INDEX = 1


class ResponsePaginationUtils {
    private val TAG = "ResponsePaginationUtils"

    private var lastRequestedPage = STARTING_PAGE_INDEX

    private var isRequestInProgress = false

    //in-memory cache
    private var stocks = mapOf<Int, List<Stock>>()

    //in-memory cache
    private var stocksWithQuote = mutableListOf<Stock>()



    /**
     * метод возвращает часть списка акций
     */
    suspend fun getMoreStocks(exchange: String, mic: String) {
        if (isRequestInProgress) return

        //val successful = requestStocks(mic)
        //if (successful) lastRequestedPage++
    }


    /**
     * fetch quote data to each stock item for current page
     */
//    private suspend fun requestStocksWithQuote(mic: String): Boolean {
//        isRequestInProgress = true
//        var successful = false
//        try {
//            if (lastRequestedPage <= stocks.size) {
//                val stockListByPage = stocks.getValue(lastRequestedPage)
//                if (!stocksWithQuote.containsAll(stockListByPage)) {
//                    Log.i(TAG, "requested page # $lastRequestedPage in $mic")
//                    for (stock in stockListByPage) {
//                        Log.i(TAG, "requested quote for ${stock.symbol}")
//                        stock.quote = (requestQuote(stock.symbol))
//                    }
//                    stocksWithQuote.addAll(stockListByPage)
//                }
//                successful = true
//            }
//        } catch (exception: HttpException) {
//            Log.e(TAG, "Error during requesting quote: ${exception.message()}")
//        }
//        isRequestInProgress = false
//        return successful
//    }


    /**
     * The method splits the provided list into a parts according to the page size
     * fake pagination approach
     */
    private fun paginateList(response: List<Stock>, pageSize: Int): Map<Int, List<Stock>> {
        val paginatedResponse = mutableMapOf<Int, List<Stock>>()
        var page = 1
        val chunkedResponse = response.chunked(pageSize)

        for (chunk in chunkedResponse) {
            paginatedResponse[page] = chunk
            page++
        }
        return paginatedResponse
    }

    /**
     * separately requests an actual quote for the stock
     */
    private suspend fun requestQuote(symbol: String): Quote {
        val quote = FinnhubApi.apiService.getQuote(symbol)
        return Quote(quote.c, quote.d, quote.dp, quote.h, quote.l, quote.o, quote.pc)
    }
}