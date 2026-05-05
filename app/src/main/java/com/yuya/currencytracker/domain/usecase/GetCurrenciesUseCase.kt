package com.yuya.currencytracker.domain.usecase

import com.yuya.currencytracker.domain.model.Currency
import com.yuya.currencytracker.domain.repository.CurrencyRepository

class GetCurrenciesUseCase(private val repository: CurrencyRepository) {
    operator fun invoke(): List<Currency> {
        return repository.getCurrencies().sortedWith(
            compareByDescending<Currency> { it.isFavorite }
                .thenBy { it.code }
        )
    }
}
