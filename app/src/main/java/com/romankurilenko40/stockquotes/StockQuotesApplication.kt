package com.romankurilenko40.stockquotes

import android.app.Application
import com.romankurilenko40.stockquotes.database.StockQuoteDatabase
import com.romankurilenko40.stockquotes.repository.StockQuotesRepository

class StockQuotesApplication: Application() {

    val database by lazy { StockQuoteDatabase.getDatabase(this) }
    val repository by lazy { StockQuotesRepository(database.stockDao()) }
}