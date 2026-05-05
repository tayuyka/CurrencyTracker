package com.yuya.currencytracker.domain.model

import java.util.Locale

data class Currency(
    val id: Int,
    val code: String,
    val name: String,
    val currentRate: Double,
    val icon: CurrencyIcon,
    val isFavorite: Boolean = false,
    val isStandard: Boolean = false
) {
    fun formattedRate(): String = String.format(Locale.US, "%.4f", currentRate)
}
