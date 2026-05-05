package com.yuya.currencytracker.domain.usecase

import com.yuya.currencytracker.domain.model.CurrencyIcon
import com.yuya.currencytracker.domain.repository.CurrencyRepository

class UpdateCurrencyUseCase(private val repository: CurrencyRepository) {
    operator fun invoke(currencyId: Int, code: String, name: String, icon: CurrencyIcon, isFavorite: Boolean) {
        val updatedList = repository.getCurrencies().map { currency ->
            if (currency.id == currencyId) {
                currency.copy(
                    code = code.uppercase(),
                    name = name.ifBlank { code.uppercase() },
                    icon = icon,
                    isFavorite = isFavorite
                )
            } else {
                currency
            }
        }
        repository.saveCurrencies(updatedList)
    }
}
