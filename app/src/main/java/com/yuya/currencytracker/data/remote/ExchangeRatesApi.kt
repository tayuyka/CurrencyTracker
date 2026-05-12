package com.yuya.currencytracker.data.remote

import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

data class FrankfurterRateDto(
    val date: String,
    val base: String,
    val quote: String,
    val rate: Double
)

interface ExchangeRatesApi {
    @GET("rates")
    suspend fun getLatestRates(
        @Query("base") base: String,
        @Query("quotes") quotes: String
    ): Response<List<FrankfurterRateDto>>

    @GET("rates")
    suspend fun getHistoryRates(
        @Query("from") from: String,
        @Query("to") to: String,
        @Query("base") base: String,
        @Query("quotes") quotes: String
    ): Response<List<FrankfurterRateDto>>
}
