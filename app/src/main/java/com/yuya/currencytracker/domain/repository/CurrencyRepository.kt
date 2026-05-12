package com.yuya.currencytracker.domain.repository

import com.yuya.currencytracker.domain.model.Currency
import kotlinx.coroutines.flow.Flow

interface CurrencyRepository {
    fun observeCurrencies(): Flow<List<Currency>>
    suspend fun initialize()
    suspend fun getCurrencies(): List<Currency>
    suspend fun getCurrency(currencyId: String): Currency?
    suspend fun saveCurrencies(currencies: List<Currency>)
    suspend fun refreshRatesFromServer(): Result<Unit>
}
