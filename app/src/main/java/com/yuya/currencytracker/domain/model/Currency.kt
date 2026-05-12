package com.yuya.currencytracker.domain.model

import java.util.Locale

data class Currency(
    val id: String,
    val code: String,
    val name: String,
    val currentRate: Double,
    val icon: CurrencyIcon,
    val isFavorite: Boolean = false,
    val isStandard: Boolean = false,
    val history: List<CurrencyHistoryEntry> = emptyList()
) {
    fun formattedRate(): String = String.format(Locale.US, "%.4f", currentRate)
}
