package com.yuya.currencytracker.domain.usecase

import com.yuya.currencytracker.domain.model.Currency
import com.yuya.currencytracker.domain.model.CurrencyHistoryEntry
class GetCurrencyHistoryUseCase {
    operator fun invoke(currency: Currency): List<CurrencyHistoryEntry> {
        return currency.history
    }
}
