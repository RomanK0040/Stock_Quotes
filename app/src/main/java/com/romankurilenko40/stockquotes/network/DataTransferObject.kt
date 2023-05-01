package com.romankurilenko40.stockquotes.network

import com.github.mikephil.charting.data.Entry
import com.romankurilenko40.stockquotes.domain.News
import com.romankurilenko40.stockquotes.domain.Stock
import com.squareup.moshi.JsonClass
import com.squareup.moshi.Moshi

@JsonClass(generateAdapter = true)
data class SearchResultContainer(
        val count: Int?,
        val result: List<SearchResultItem>?
)

@JsonClass(generateAdapter = true)
data class SearchResultItem(
        val description: String,
        val displaySymbol: String,
        val symbol: String,
        val type: String
)

@JsonClass(generateAdapter = true)
data class SupportedStock(
        val description: String,
        val displaySymbol: String,
        val symbol: String,
        val type: String,
        val mic: String,
        val figi: String,
        val currency: String)

fun List<SupportedStock>.asStockList(exchange: String): List<Stock> {
        return this.map {
                Stock(
                        it.symbol,
                        it.description,
                        it.currency,
                        exchange,
                        it.mic
                )
        }
}

@JsonClass(generateAdapter = true)
data class Candles(
        //List of close prices
        val c: List<Double>?,
        //List of high prices
        val h: List<Double>?,
        //List of low prices
        val l: List<Double>?,
        //List of open prices
        val o: List<Double>?,
        //Status of the response
        val s: String,
        //List of timestamp
        val t: List<Long>?,
        //List of volume data
        val v: List<Double>?
)

fun Candles.asQuoteEntryData(): List<Entry> {

        val quoteData = ArrayList<Entry>()
        if (this.c != null && this.t != null) {
                for (i in 0 until this.c.size-1) {
                        quoteData.add(Entry(this.t[i].toFloat(), this.c[i].toFloat()))
                }
        }
        return quoteData
}

@JsonClass(generateAdapter = true)
data class CompanyProfile(
        val country: String?,
        val currency: String?,
        val exchange: String?,
        val finnhubIndustry: String?,
        val ipo: String?,
        val logo: String?,
        val marketCapitalization: Double?,
        val name: String?,
        val phone: String?,
        val shareOutstanding: Double?,
        val ticker: String?,
        val weburl: String?,
)

@JsonClass(generateAdapter = true)
data class MarketNews(
        val category: String,
        val datetime: Long,
        val headline: String,
        val id: Long,
        val image: String,
        val related: String,
        val source: String,
        val summary: String,
        val url: String
)

fun List<MarketNews>.asNewsList(): List<News> {
        return this.map {
                News(
                        it.id,
                        it.category,
                        it.datetime,
                        it.headline,
                        it.image,
                        it.related,
                        it.source,
                        it.summary,
                        it.url
                )
        }
}



@JsonClass(generateAdapter = true)
data class StockQuote (
        //Current price
        val c: Double,
        //Change
        val d: Double?,
        //Percent change
        val dp: Double?,
        //High price of the day
        val h: Double,
        //Low price of the day
        val l: Double,
        //Open price of the day
        val o: Double,
        //Previous close price
        val pc: Double,
        //Time
        val t: Long)
