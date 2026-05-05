package com.yuya.currencytracker.domain.usecase

import com.yuya.currencytracker.domain.model.Currency
import com.yuya.currencytracker.domain.model.CurrencyHistoryEntry
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Locale
import kotlin.math.max
import kotlin.random.Random

class GetCurrencyHistoryUseCase {
    private val formatter = DateTimeFormatter.ofPattern("dd MMM", Locale.forLanguageTag("ru"))

    operator fun invoke(currency: Currency, version: Int): List<CurrencyHistoryEntry> {
        return (6 downTo 0).map { offset ->
            val day = LocalDate.now().minusDays(offset.toLong())
            val random = Random(currency.code.hashCode() + offset + version * 101)
            val delta = random.nextDouble(-2.8, 2.8)
            CurrencyHistoryEntry(
                dayLabel = day.format(formatter),
                rate = max(0.01, currency.currentRate + delta)
            )
        }
    }
}
