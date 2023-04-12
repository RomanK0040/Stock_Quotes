package com.romankurilenko40.stockquotes.domain


import java.text.SimpleDateFormat
import java.util.*


data class News(
    val id: Long,
    val category: String,
    val datetime: Long,
    val headline: String,
    val image: String,
    val related: String,
    val source: String,
    val summary: String,
    val url: String
) {


    val formattedDate: String = convertDate(datetime)


    private fun convertDate(time: Long): String {
        val formatter = SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.US)
        return formatter.format(Date(time * 1000L))
    }
}
