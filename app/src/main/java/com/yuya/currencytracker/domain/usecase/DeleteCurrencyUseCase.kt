package com.yuya.currencytracker.domain.usecase

import com.yuya.currencytracker.domain.repository.CurrencyRepository

class DeleteCurrencyUseCase(private val repository: CurrencyRepository) {
    suspend operator fun invoke(currencyId: String) {
        val updatedList = repository.getCurrencies().filterNot { it.id == currencyId }
        repository.saveCurrencies(updatedList)
    }
}
