package com.romankurilenko40.stockquotes.repository

import com.github.mikephil.charting.data.Entry
import com.romankurilenko40.stockquotes.domain.Company
import com.romankurilenko40.stockquotes.domain.Quote
import com.romankurilenko40.stockquotes.domain.Stock
import com.romankurilenko40.stockquotes.network.*
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow




private const val PAGE_SIZE = 15

class StockQuotesRepository() {

    //in-memory cache
    private var stocks = listOf<Stock>()

    private val stocksFlow = MutableSharedFlow<StockNetworkResult>(replay = 1)

    private val searchFlow = MutableSharedFlow<SearchResultContainer>(replay = 1)

    private var isRequestInProgress = false


    /**
     * Method returns first list of stock with actual quotes
     */
    suspend fun getStocksBySelectedExchange(exchange: String, mic: String): Flow<StockNetworkResult> {

        stocks = fetchStocksBySelectedExchange(exchange, mic)

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
     * Update the stock parameter "inBookmark" to opposite
     */
    suspend fun putStockInBookmark(symbol: String) {
        for( stock in stocks) {
            if (stock.symbol == symbol) {
                stock.inBookmark = !stock.inBookmark
            }
        }
        stocksFlow.emit(StockNetworkResult.Success(stocks))
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
    suspend fun fetchStockCandles(symbol: String, resolution: String): Candles {
        val unixYearSeconds = 31556926
        val currentTimeSeconds = System.currentTimeMillis() / 1000
        val candles = FinnhubApi.apiService.getCandles(
            symbol,
            resolution,
            currentTimeSeconds - unixYearSeconds,
            currentTimeSeconds)

        return candles
    }

    /**
     * Returns quote and timestamp data as list of "Entry" pairs extracted from "candles"
     * to render only line chart
     * @param symbol - company displayed symbol (ticker)
     * @param resolution - time frames to divide the chart values
     */
    suspend fun getQuoteChartData(symbol: String, resolution: String): List<Entry> {
        return fetchStockCandles(symbol, resolution).asQuoteEntryData()
    }




}