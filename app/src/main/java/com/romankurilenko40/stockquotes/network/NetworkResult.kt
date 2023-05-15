package com.romankurilenko40.stockquotes.network

import com.romankurilenko40.stockquotes.domain.Company
import com.romankurilenko40.stockquotes.domain.Stock
import java.lang.Exception

sealed class StockNetworkResult {
    data class Success(val data: List<Stock>): StockNetworkResult()
    data class Error(val error: Exception): StockNetworkResult()
}


sealed class ProfileNetworkResult {
    data class Success(val data: Company): ProfileNetworkResult()
    data class Error(val error: Exception): ProfileNetworkResult()
}

sealed class SearchNetworkResult {
    data class Success(val data: SearchResultContainer): SearchNetworkResult()
    data class Error(val error: Exception): SearchNetworkResult()
}