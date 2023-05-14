package com.romankurilenko40.stockquotes.domain

import androidx.room.Entity
import androidx.room.PrimaryKey


/**
 * Class represented a stock with last updated information
 * @symbol = displayed symbol of a company
 */
@Entity(tableName = "stock_table")
data class Stock(
        @PrimaryKey
        val symbol: String,

        val description: String,
        val currency: String,
        val exchange: String,
        val mic: String) {

    var inBookmark: Boolean = false

    //var quote: Quote = Quote(0.0,0.0,0.0,0.0,0.0,0.0,0.0)

}