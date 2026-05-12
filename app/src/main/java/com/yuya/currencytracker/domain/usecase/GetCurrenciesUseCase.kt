package com.yuya.currencytracker.domain.usecase

import com.yuya.currencytracker.domain.model.Currency
import com.yuya.currencytracker.domain.repository.CurrencyRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class GetCurrenciesUseCase(private val repository: CurrencyRepository) {
    operator fun invoke(): Flow<List<Currency>> {
        return repository.observeCurrencies().map { currencies ->
            currencies.sortedForDisplay()
        }
    }

    suspend fun snapshot(): List<Currency> {
        return repository.getCurrencies().sortedForDisplay()
    }

    private fun List<Currency>.sortedForDisplay(): List<Currency> {
        return sortedWith(
            compareByDescending<Currency> { it.isFavorite }
                .thenBy { it.code }
        )
    }
}
