package com.romankurilenko40.stockquotes.domain

data class Company(
    val country: String?,
    val currency: String?,
    val exchange: String?,
    val finnhubIndustry: String?,
    val ipoDate: String?,
    val logoUrl: String?,
    val marketCapitalization: Double?,
    val name: String?,
    val phone: String?,
    val shareQutstanding: Double?,
    val ticker: String?,
    val weburl: String?
)
