package com.yuya.currencytracker.presentation.viewmodel

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.yuya.currencytracker.data.local.CurrencyPreferencesDataSource
import com.yuya.currencytracker.data.repository.SharedPreferencesCurrencyRepository
import com.yuya.currencytracker.domain.usecase.*

class CurrencyViewModelFactory(
    private val context: Context
) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(CurrencyViewModel::class.java)) {
            val repository = SharedPreferencesCurrencyRepository(
                CurrencyPreferencesDataSource(context.applicationContext)
            )
            
            val getCurrenciesUseCase = GetCurrenciesUseCase(repository)
            val addCurrencyUseCase = AddCurrencyUseCase(repository)
            val updateCurrencyUseCase = UpdateCurrencyUseCase(repository)
            val deleteCurrencyUseCase = DeleteCurrencyUseCase(repository)
            val toggleFavoriteUseCase = ToggleFavoriteUseCase(repository)
            val refreshRatesUseCase = RefreshRatesUseCase(repository)
            val getCurrencyHistoryUseCase = GetCurrencyHistoryUseCase()
            
            @Suppress("UNCHECKED_CAST")
            return CurrencyViewModel(
                repository,
                getCurrenciesUseCase,
                addCurrencyUseCase,
                updateCurrencyUseCase,
                deleteCurrencyUseCase,
                toggleFavoriteUseCase,
                refreshRatesUseCase,
                getCurrencyHistoryUseCase
            ) as T
        }

        throw IllegalArgumentException("Unknown ViewModel class: ${modelClass.name}")
    }
}
