package com.yuya.currencytracker.domain.usecase

import com.yuya.currencytracker.domain.repository.CurrencyRepository

class ToggleFavoriteUseCase(private val repository: CurrencyRepository) {
    suspend operator fun invoke(currencyId: String) {
        val updatedList = repository.getCurrencies().map { currency ->
            if (currency.id == currencyId) {
                currency.copy(isFavorite = !currency.isFavorite)
            } else {
                currency
            }
        }
        repository.saveCurrencies(updatedList)
    }
}
