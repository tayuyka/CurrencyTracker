package com.yuya.currencytracker.data.repository

import com.google.gson.JsonParser
import com.yuya.currencytracker.data.local.CurrencyPreferencesDataSource
import com.yuya.currencytracker.data.remote.ExchangeRatesApi
import com.yuya.currencytracker.data.remote.ExchangeRatesMapper
import com.yuya.currencytracker.domain.model.Currency
import com.yuya.currencytracker.domain.model.CurrencyIcon
import com.yuya.currencytracker.domain.repository.CurrencyRepository
import java.time.LocalDate

class SharedPreferencesCurrencyRepository(
    private val dataSource: CurrencyPreferencesDataSource,
    private val api: ExchangeRatesApi,
    private val mapper: ExchangeRatesMapper = ExchangeRatesMapper()
) : CurrencyRepository {

    override fun getCurrencies(): List<Currency> = 
        dataSource.loadCurrencies() ?: defaultCurrencies()

    override fun getCurrency(currencyId: String): Currency? =
        getCurrencies().firstOrNull { it.id == currencyId }

    override fun saveCurrencies(currencies: List<Currency>) {
        dataSource.saveCurrencies(currencies)
    }

    override suspend fun refreshRatesFromServer(): Result<Unit> {
        return runCatching {
            val currencies = getCurrencies()
            val quotes = currencies
                .filter { it.isStandard && it.code != RUB_CODE }
                .joinToString(",") { it.code }

            if (quotes.isBlank()) {
                saveCurrencies(currencies)
                return@runCatching
            }

            val latestResponse = api.getLatestRates(base = RUB_CODE, quotes = quotes)
            if (!latestResponse.isSuccessful) {
                throw IllegalStateException(latestResponse.errorMessage())
            }

            val today = LocalDate.now()
            val historyResponse = api.getHistoryRates(
                from = today.minusDays(7).toString(),
                to = today.toString(),
                base = RUB_CODE,
                quotes = quotes
            )
            if (!historyResponse.isSuccessful) {
                throw IllegalStateException(historyResponse.errorMessage())
            }

            saveCurrencies(
                mapper.map(
                    latestRates = latestResponse.body().orEmpty(),
                    historyRates = historyResponse.body().orEmpty(),
                    existingCurrencies = currencies
                )
            )
        }
    }

    private fun retrofit2.Response<*>.errorMessage(): String {
        val rawError = errorBody()?.string().orEmpty()
        val serverMessage = runCatching {
            val json = JsonParser.parseString(rawError).asJsonObject
            json.get("message")?.asString
                ?: json.getAsJsonArray("errors")?.joinToString { it.asString }
        }.getOrNull()

        return when {
            !serverMessage.isNullOrBlank() -> "Ошибка сервера: $serverMessage"
            rawError.isNotBlank() -> "Ошибка HTTP ${code()}: $rawError"
            else -> "Ошибка HTTP ${code()}"
        }
    }

    private fun defaultCurrencies(): List<Currency> = listOf(
        Currency(
            id = "usd",
            code = "USD",
            name = "Доллар США",
            currentRate = 89.42,
            icon = CurrencyIcon.DOLLAR,
            isFavorite = true,
            isStandard = true
        ),
        Currency(
            id = "eur",
            code = "EUR",
            name = "Евро",
            currentRate = 97.16,
            icon = CurrencyIcon.EURO,
            isStandard = true
        ),
        Currency(
            id = "gbp",
            code = "GBP",
            name = "Фунт стерлингов",
            currentRate = 113.84,
            icon = CurrencyIcon.POUND,
            isStandard = true
        ),
        Currency(
            id = "jpy",
            code = "JPY",
            name = "Японская йена",
            currentRate = 0.58,
            icon = CurrencyIcon.YEN,
            isStandard = true
        ),
        Currency(
            id = "cny",
            code = "CNY",
            name = "Китайский юань",
            currentRate = 12.31,
            icon = CurrencyIcon.COIN,
            isStandard = true
        ),
        Currency(
            id = "rub",
            code = "RUB",
            name = "Российский рубль",
            currentRate = 1.0,
            icon = CurrencyIcon.TREND,
            isStandard = true
        )
    )

    private companion object {
        const val RUB_CODE = "RUB"
    }
}
