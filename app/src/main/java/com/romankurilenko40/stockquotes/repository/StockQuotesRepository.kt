package com.romankurilenko40.stockquotes.repository

import androidx.lifecycle.asLiveData
import com.github.mikephil.charting.data.Entry
import com.romankurilenko40.stockquotes.database.StockDao
import com.romankurilenko40.stockquotes.domain.Company
import com.romankurilenko40.stockquotes.domain.Quote
import com.romankurilenko40.stockquotes.domain.Stock
import com.romankurilenko40.stockquotes.network.*
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow


enum class IntervalsInSeconds(val value: Long) {
    YEAR_SECONDS(31556926),
    MONTH_SECONDS(2629743),
    WEEK_SECONDS(604800),
    DAY_SECONDS(86400),
    HOUR_SECONDS(3600)
}

private const val PAGE_SIZE = 15

class StockQuotesRepository(private val stockDao: StockDao) {

    //in-memory cache - не использовать для метода putStockInBookmark
    //private var stocks = listOf<Stock>()

    private val stocksFlow = MutableSharedFlow<StockNetworkResult>(replay = 1)

    private val searchFlow = MutableSharedFlow<SearchResultContainer>(replay = 1)


    private var isRequestInProgress = false

    val favoritesFlow = stockDao.getAllBookmarks()


    /**
     * Method returns first list of stock with actual quotes
     */
    suspend fun getStocksBySelectedExchange(exchange: String, mic: String): Flow<StockNetworkResult> {

        val stocks = fetchStocksBySelectedExchange(exchange, mic)
        for (stock in stocks) {
            stock.inBookmark = checkBookmarks(stock)
        }

        stocksFlow.emit(StockNetworkResult.Success(stocks))

        return stocksFlow
    }

    /**
     * Search for best-matching symbols based on the query.
     * @param query - symbol or a name of the company
     */
    suspend fun searchSymbol(query: String): Flow<SearchResultContainer> {
        val result = FinnhubApi.apiService.searchSymbol(query)
        searchFlow.emit(result)

        return searchFlow
    }


    /**
     * Add or delete stock from bookmarks storage
     */
    suspend fun putStockInBookmark(stock: Stock) {
        if (stockDao.getStock(stock.symbol) != null) {
            stockDao.deleteFromBookmark(stock)
        } else {
            stock.inBookmark = true
            stockDao.insertNewBookmark(stock)
        }

        //stocksFlow.emit(StockNetworkResult.Success(stocks))
    }


    /**
     * check whether particular stock store in bookmarks
     */
    private suspend fun checkBookmarks(stock: Stock): Boolean {
        if (stockDao.getStock(stock.symbol) != null) {
            return true
        }
        return false
    }


    /**
     * Fetch stock list from network //and save it in a memory
     */
    private suspend fun fetchStocksBySelectedExchange(exchange: String, mic: String?): List<Stock> {
        return FinnhubApi.apiService.getStocksList(exchange, mic).asStockList(exchange)
    }



    /**
     * separately requests an actual quote for the stock
     */
    suspend fun requestQuote(symbol: String): Quote {
        val quote = FinnhubApi.apiService.getQuote(symbol)
        return Quote(quote.c, quote.d, quote.dp, quote.h, quote.l, quote.o, quote.pc)
    }

    /**
     * Fetch company profile info by provied stock symbol/ticker
     */
    suspend fun fetchCompanyProfileInfo(symbol: String): Company {
        val company = FinnhubApi.apiService.getCompanyProfile(symbol)
        return Company(
            company.country, company.currency, company.exchange,
            company.finnhubIndustry, company.ipo, company.logo,
            company.marketCapitalization, company.name, company.phone,
            company.shareOutstanding, company.ticker, company.weburl)
    }

    /**
     * Returns "Candles" structure contains data to draw candlestick chart
     * dataset contains all quotes between time of request and year ago
     * to render just line chart use getQuoteChartData method
     * @param symbol - company displayed symbol (ticker)
     * @param resolution - time frames to divide the chart values
     */
    suspend fun fetchStockCandles(symbol: String, resolution: String): CandlesDataResult {

        val currentTimeSeconds = System.currentTimeMillis() / 1000
        val startTimeInterval = getInitialIntervalValue(currentTimeSeconds,resolution)
        val response = try {
            val candlesData = FinnhubApi.apiService.getCandles(
                symbol,
                resolution,
                from = startTimeInterval,
                to = currentTimeSeconds)
            if (candlesData.s == "ok") {
                CandlesDataResult.AvailableChartData(candlesData)
            } else {
                CandlesDataResult.NoChartData("Chart data is not available")
            }
        } catch (e: Exception) {
            e.message
            CandlesDataResult.NoChartData("Error during fetching chart data")
        }

        return response
    }

    /**
     * Returns quote and timestamp data as list of "Entry" pairs extracted from "candles"
     * to render only line chart
     * @param symbol - company displayed symbol (ticker)
     * @param resolution - time frames to divide the chart values
     */
    suspend fun getQuoteChartData(symbol: String, resolution: String): List<Entry> {

        val data = fetchStockCandles(symbol, resolution)
        return if (data is CandlesDataResult.AvailableChartData) {
            data.candles.asQuoteEntryData()
        } else {
            emptyList()
        }

    }


    /**
     * returns  value of time interval corresponding selected time resolution
     * to avoid retrieving too much Entry values for chart rendering
     */
    private fun getInitialIntervalValue(endValue: Long, resolution: String): Long {

        val interval: Long = when (resolution) {
                           "1" ->  IntervalsInSeconds.HOUR_SECONDS.value * 2
                           "5" ->  IntervalsInSeconds.HOUR_SECONDS.value * 5
                           "15" ->  IntervalsInSeconds.HOUR_SECONDS.value * 5
                           "30" ->  IntervalsInSeconds.DAY_SECONDS.value
                           "60" -> IntervalsInSeconds.WEEK_SECONDS.value
                           "D" -> IntervalsInSeconds.MONTH_SECONDS.value * 6
                           "W" -> IntervalsInSeconds.YEAR_SECONDS.value
                           "M" -> IntervalsInSeconds.YEAR_SECONDS.value
            else -> {IntervalsInSeconds.DAY_SECONDS.value}
        }
        return endValue - interval
    }

}