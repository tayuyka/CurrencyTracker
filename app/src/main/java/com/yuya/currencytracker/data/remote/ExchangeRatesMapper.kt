package com.yuya.currencytracker.data.remote

import com.yuya.currencytracker.domain.model.Currency
import com.yuya.currencytracker.domain.model.CurrencyHistoryEntry

class ExchangeRatesMapper {
    fun map(
        latestRates: List<FrankfurterRateDto>,
        historyRates: List<FrankfurterRateDto>,
        existingCurrencies: List<Currency>
    ): List<Currency> {
        val latestByCode = latestRates.associateBy { it.quote.uppercase() }
        val historyByCode = historyRates
            .groupBy { it.quote.uppercase() }
            .mapValues { (_, rates) ->
                rates
                    .distinctBy { it.date }
                    .sortedBy { it.date }
                    .takeLast(7)
                    .map { rate ->
                        CurrencyHistoryEntry(
                            dayLabel = rate.date,
                            rate = rate.toRubRate()
                        )
                    }
            }
        val rubHistory = historyRates
            .distinctBy { it.date }
            .sortedBy { it.date }
            .takeLast(7)
            .map { CurrencyHistoryEntry(dayLabel = it.date, rate = 1.0) }

        return existingCurrencies.map { currency ->
            if (currency.code == RUB_CODE) {
                currency.copy(
                    currentRate = 1.0,
                    history = rubHistory
                )
            } else {
                val latest = latestByCode[currency.code]
                currency.copy(
                    currentRate = latest?.toRubRate() ?: currency.currentRate,
                    history = historyByCode[currency.code].orEmpty()
                )
            }
        }
    }

    private fun FrankfurterRateDto.toRubRate(): Double {
        return if (rate == 0.0) 0.0 else 1.0 / rate
    }

    private companion object {
        const val RUB_CODE = "RUB"
    }
}
