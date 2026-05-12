package com.yuya.currencytracker.domain.usecase

import com.yuya.currencytracker.domain.model.Currency
import com.yuya.currencytracker.domain.model.CurrencyIcon
import com.yuya.currencytracker.domain.repository.CurrencyRepository
import java.util.UUID
import kotlin.random.Random

class AddCurrencyUseCase(private val repository: CurrencyRepository) {
    operator fun invoke(code: String, name: String, icon: CurrencyIcon, isFavorite: Boolean) {
        val currencies = repository.getCurrencies()
        val newCurrency = Currency(
            id = UUID.randomUUID().toString(),
            code = code.uppercase(),
            name = name.ifBlank { code.uppercase() },
            currentRate = Random.nextDouble(35.0, 120.0),
            icon = icon,
            isFavorite = isFavorite
        )
        
        val newList = currencies.toMutableList().apply { add(newCurrency) }
        repository.saveCurrencies(newList)
    }
}
