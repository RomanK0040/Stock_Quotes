package com.romankurilenko40.stockquotes.network

import com.romankurilenko40.stockquotes.domain.Stock
import java.lang.Exception

sealed class StockNetworkResult {
    data class Success(val data: List<Stock>): StockNetworkResult()
    data class Error(val error: Exception): StockNetworkResult()
}