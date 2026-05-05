package com.yuya.currencytracker.data.repository

import com.yuya.currencytracker.data.local.CurrencyPreferencesDataSource
import com.yuya.currencytracker.domain.model.Currency
import com.yuya.currencytracker.domain.model.CurrencyIcon
import com.yuya.currencytracker.domain.repository.CurrencyRepository

class SharedPreferencesCurrencyRepository(
    private val dataSource: CurrencyPreferencesDataSource
) : CurrencyRepository {

    override fun getCurrencies(): List<Currency> = 
        dataSource.loadCurrencies() ?: defaultCurrencies()

    override fun getCurrency(currencyId: Int): Currency? =
        getCurrencies().firstOrNull { it.id == currencyId }

    override fun saveCurrencies(currencies: List<Currency>) {
        dataSource.saveCurrencies(currencies)
    }

    private fun defaultCurrencies(): List<Currency> = listOf(
        Currency(
            id = 1,
            code = "USD",
            name = "Доллар США",
            currentRate = 89.42,
            icon = CurrencyIcon.DOLLAR,
            isFavorite = true,
            isStandard = true
        ),
        Currency(
            id = 2,
            code = "EUR",
            name = "Евро",
            currentRate = 97.16,
            icon = CurrencyIcon.EURO,
            isStandard = true
        ),
        Currency(
            id = 3,
            code = "GBP",
            name = "Фунт стерлингов",
            currentRate = 113.84,
            icon = CurrencyIcon.POUND,
            isStandard = true
        ),
        Currency(
            id = 4,
            code = "JPY",
            name = "Японская йена",
            currentRate = 0.58,
            icon = CurrencyIcon.YEN,
            isStandard = true
        ),
        Currency(
            id = 5,
            code = "CNY",
            name = "Китайский юань",
            currentRate = 12.31,
            icon = CurrencyIcon.COIN,
            isStandard = true
        ),
        Currency(
            id = 6,
            code = "RUB",
            name = "Российский рубль",
            currentRate = 1.0,
            icon = CurrencyIcon.TREND,
            isStandard = true
        )
    )
}
