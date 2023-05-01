package com.romankurilenko40.stockquotes.network


sealed class CandlesDataResult {
    data class AvailableChartData(val candles: Candles): CandlesDataResult()
    data class NoChartData(val msg: String): CandlesDataResult()
}