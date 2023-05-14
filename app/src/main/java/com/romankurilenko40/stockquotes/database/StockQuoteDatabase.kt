package com.romankurilenko40.stockquotes.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.romankurilenko40.stockquotes.domain.Stock

@Database(entities = [Stock::class], version = 1, exportSchema = false)
abstract class StockQuoteDatabase: RoomDatabase() {

    abstract fun stockDao(): StockDao

    companion object {
        @Volatile
        private var INSTANCE: StockQuoteDatabase? = null

        fun getDatabase(context: Context): StockQuoteDatabase {
            return INSTANCE?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    StockQuoteDatabase::class.java,
                    "stock_database"
                ).build()
                INSTANCE = instance

                instance
            }
        }
    }
}