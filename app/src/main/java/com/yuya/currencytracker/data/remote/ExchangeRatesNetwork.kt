package com.yuya.currencytracker.data.remote

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object ExchangeRatesNetwork {
    fun createApi(): ExchangeRatesApi {
        return Retrofit.Builder()
            .baseUrl("https://api.frankfurter.dev/v2/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(ExchangeRatesApi::class.java)
    }
}
