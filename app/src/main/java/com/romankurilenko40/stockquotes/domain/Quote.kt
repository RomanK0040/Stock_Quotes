package com.romankurilenko40.stockquotes.domain

data class Quote(
    val currentPrice: Double = 0.0,
    val priceChange: Double? = 0.0,
    val pricePercentChange: Double? = 0.0,
    val highPrice: Double = 0.0,
    val lowPrice: Double = 0.0,
    val openPrice: Double = 0.0,
    val prevClosePrice: Double = 0.0) {

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
