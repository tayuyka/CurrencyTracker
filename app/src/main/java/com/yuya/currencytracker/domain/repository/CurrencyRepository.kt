package com.yuya.currencytracker.domain.repository

import com.yuya.currencytracker.domain.model.Currency

interface CurrencyRepository {
    fun getCurrencies(): List<Currency>
    fun getCurrency(currencyId: String): Currency?
    fun saveCurrencies(currencies: List<Currency>)
    suspend fun refreshRatesFromServer(): Result<Unit>
}
