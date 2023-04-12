package com.romankurilenko40.stockquotes.domain

data class StockExchange(
    val id: Int,
    val exchangeCountry: String,
    val mic: String,
    val marketName: String
) {
    override fun toString(): String {
        return marketName
    }
}
