package com.yuya.currencytracker.data.local

import com.yuya.currencytracker.domain.model.Currency
import com.yuya.currencytracker.domain.model.CurrencyHistoryEntry
import com.yuya.currencytracker.domain.model.CurrencyIcon

fun CurrencyWithHistory.toDomain(): Currency {
    return Currency(
        id = currency.id,
        code = currency.code,
        name = currency.name,
        currentRate = currency.currentRate,
        icon = CurrencyIcon.valueOf(currency.icon),
        isFavorite = currency.isFavorite,
        isStandard = currency.isStandard,
        history = history.sortedBy { it.dayLabel }.map {
            CurrencyHistoryEntry(dayLabel = it.dayLabel, rate = it.rate)
        }
    )
}

fun Currency.toEntity(): CurrencyEntity {
    return CurrencyEntity(
        id = id,
        code = code,
        name = name,
        currentRate = currentRate,
        icon = icon.name,
        isFavorite = isFavorite,
        isStandard = isStandard
    )
}

fun Currency.toHistoryEntities(): List<CurrencyHistoryEntity> {
    return history.map {
        CurrencyHistoryEntity(
            currencyId = id,
            dayLabel = it.dayLabel,
            rate = it.rate
        )
    }
}
