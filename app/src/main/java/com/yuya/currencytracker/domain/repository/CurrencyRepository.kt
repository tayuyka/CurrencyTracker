package com.yuya.currencytracker.domain.repository

import com.yuya.currencytracker.domain.model.Currency

interface CurrencyRepository {
    fun getCurrencies(): List<Currency>
    fun getCurrency(currencyId: Int): Currency?
    fun saveCurrencies(currencies: List<Currency>)
}
