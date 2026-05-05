package com.yuya.currencytracker.domain.usecase

import com.yuya.currencytracker.domain.repository.CurrencyRepository
import kotlin.random.Random

class RefreshRatesUseCase(private val repository: CurrencyRepository) {
    operator fun invoke() {
        val updatedCurrencies = repository.getCurrencies().map { currency ->
            val delta = if (currency.code == "RUB") 0.0 else Random.nextDouble(-1.75, 1.75)
            currency.copy(currentRate = maxOf(0.01, currency.currentRate + delta))
        }
        repository.saveCurrencies(updatedCurrencies)
    }
}
