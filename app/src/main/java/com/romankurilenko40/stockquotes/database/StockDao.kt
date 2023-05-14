package com.romankurilenko40.stockquotes.database

import androidx.room.*
import com.romankurilenko40.stockquotes.domain.Stock
import kotlinx.coroutines.flow.Flow

@Dao
interface StockDao {

    @Query("SELECT * FROM stock_table")
    fun getAllBookmarks(): Flow<List<Stock>>

    @Query("SELECT * FROM stock_table WHERE symbol = :stockSymbol")
    suspend fun getStock(stockSymbol: String): Stock?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertNewBookmark(stock: Stock)

    @Delete
    suspend fun deleteFromBookmark(stock: Stock)
}