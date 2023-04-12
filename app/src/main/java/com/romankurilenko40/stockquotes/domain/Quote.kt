package com.romankurilenko40.stockquotes.domain

data class Quote(
    val currentPrice: Double,
    val priceChange: Double?,
    val pricePercentChange: Double?,
    val highPrice: Double,
    val lowPrice: Double,
    val openPrice: Double,
    val prevClosePrice: Double) {

    fun getActualPriceString(): String {
        return "$ ${"%.2f".format(currentPrice)}"
    }

    /**
     * property represented changing of price according qoute response
     */
    fun getPriceChangeString(): String {
        return " ${"%.2f".format(pricePercentChange)}%"
    }
}