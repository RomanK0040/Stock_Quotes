package com.romankurilenko40.stockquotes.domain

data class PagedStockResponse(val data: List<Stock>, val nextPage: Int)