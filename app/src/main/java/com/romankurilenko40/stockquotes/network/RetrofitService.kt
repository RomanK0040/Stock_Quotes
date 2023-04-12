package com.romankurilenko40.stockquotes.network

import com.romankurilenko40.stockquotes.BuildConfig
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.http.GET
import retrofit2.http.Query


private const val BASE_URL = "https://finnhub.io/api/v1/"
private const val API_KEY = BuildConfig.FINNHUB_API_KEY

private val retrofit = Retrofit.Builder()
        .baseUrl(BASE_URL)
        .addConverterFactory(MoshiConverterFactory.create())
        .build()


interface FinnhubApiService {

    @GET("stock/symbol?token=${API_KEY}")
    suspend fun getStocksList(
        @Query("exchange") exchange: String = "US",
        @Query("mic") mic: String?): List<SupportedStock>

    @GET("search?token=${API_KEY}")
    suspend fun searchSymbol(@Query("q") query: String): SearchResultContainer

    @GET("quote?token=${API_KEY}")
    suspend fun getQuote(@Query("symbol") symbol: String): StockQuote

    @GET("stock/candle?token=${API_KEY}")
    suspend fun getCandles(
        @Query("symbol") query: String,
        @Query("resolution") resolution: String = "1",
        @Query("from") from: Long,
        @Query("to") to: Long): Candles

    @GET("stock/profile2?token=${API_KEY}")
    suspend fun getCompanyProfile(@Query("symbol") symbol: String): CompanyProfile

    @GET("news?token=${API_KEY}")
    suspend fun getMarketNews(
        @Query("category") category: String,
        @Query("minId") minId: Int): List<MarketNews>
}

object FinnhubApi {
    val apiService: FinnhubApiService by lazy {
        retrofit.create(FinnhubApiService::class.java)
    }
}