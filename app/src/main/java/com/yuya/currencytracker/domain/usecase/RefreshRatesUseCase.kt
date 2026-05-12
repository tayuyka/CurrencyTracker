package com.yuya.currencytracker.domain.usecase

import com.yuya.currencytracker.domain.repository.CurrencyRepository
class RefreshRatesUseCase(private val repository: CurrencyRepository) {
    suspend operator fun invoke(): Result<Unit> {
        return repository.refreshRatesFromServer()
    }
}
