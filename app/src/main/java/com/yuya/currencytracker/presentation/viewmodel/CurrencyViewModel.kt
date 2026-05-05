package com.yuya.currencytracker.presentation.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.yuya.currencytracker.domain.model.Currency
import com.yuya.currencytracker.domain.model.CurrencyHistoryEntry
import com.yuya.currencytracker.domain.model.CurrencyIcon
import com.yuya.currencytracker.domain.repository.CurrencyRepository
import com.yuya.currencytracker.domain.usecase.*

data class CurrencyUiState(
    val currencies: List<Currency> = emptyList(),
    val showDialog: Boolean = false,
    val editingCurrency: Currency? = null,
    val selectedCurrencyId: Int? = null,
    val selectedCurrency: Currency? = null,
    val history: List<CurrencyHistoryEntry> = emptyList()
)

class CurrencyViewModel(
    private val repository: CurrencyRepository,
    private val getCurrenciesUseCase: GetCurrenciesUseCase,
    private val addCurrencyUseCase: AddCurrencyUseCase,
    private val updateCurrencyUseCase: UpdateCurrencyUseCase,
    private val deleteCurrencyUseCase: DeleteCurrencyUseCase,
    private val toggleFavoriteUseCase: ToggleFavoriteUseCase,
    private val refreshRatesUseCase: RefreshRatesUseCase,
    private val getCurrencyHistoryUseCase: GetCurrencyHistoryUseCase
) : ViewModel() {

    var uiState by mutableStateOf(CurrencyUiState())
        private set

    private var historyRefreshVersion by mutableIntStateOf(0)

    init {
        loadCurrencies()
    }

    fun onAddCurrencyClick() {
        uiState = uiState.copy(showDialog = true, editingCurrency = null)
    }

    fun onEditCurrencyClick(currency: Currency) {
        uiState = uiState.copy(showDialog = true, editingCurrency = currency)
    }

    fun onDismissDialog() {
        uiState = uiState.copy(showDialog = false, editingCurrency = null)
    }

    fun saveCurrency(code: String, name: String, icon: CurrencyIcon, isFavorite: Boolean) {
        val editingCurrency = uiState.editingCurrency
        if (editingCurrency == null) {
            addCurrencyUseCase(code, name, icon, isFavorite)
        } else {
            updateCurrencyUseCase(editingCurrency.id, code, name, icon, isFavorite)
        }

        uiState = uiState.copy(showDialog = false, editingCurrency = null)
        loadCurrencies()
    }

    fun deleteCurrency(currencyId: Int) {
        deleteCurrencyUseCase(currencyId)

        if (uiState.selectedCurrencyId == currencyId) {
            historyRefreshVersion = 0
            uiState = uiState.copy(
                selectedCurrencyId = null,
                selectedCurrency = null,
                history = emptyList()
            )
        }

        loadCurrencies()
    }

    fun toggleFavorite(currencyId: Int) {
        toggleFavoriteUseCase(currencyId)
        loadCurrencies()
    }

    fun refreshRates() {
        refreshRatesUseCase()
        loadCurrencies()
    }

    fun selectCurrency(currencyId: Int) {
        historyRefreshVersion = 0
        val selectedCurrency = repository.getCurrency(currencyId)
        uiState = uiState.copy(
            selectedCurrencyId = currencyId,
            selectedCurrency = selectedCurrency,
            history = selectedCurrency?.let {
                getCurrencyHistoryUseCase(it, historyRefreshVersion)
            } ?: emptyList()
        )
    }

    fun refreshHistory() {
        val selectedCurrencyId = uiState.selectedCurrencyId ?: return
        val selectedCurrency = repository.getCurrency(selectedCurrencyId) ?: return

        historyRefreshVersion += 1
        uiState = uiState.copy(
            selectedCurrency = selectedCurrency,
            history = getCurrencyHistoryUseCase(selectedCurrency, historyRefreshVersion)
        )
    }

    private fun loadCurrencies() {
        val currencies = getCurrenciesUseCase()
        val selectedCurrencyId = uiState.selectedCurrencyId
        val selectedCurrency = selectedCurrencyId?.let(repository::getCurrency)

        uiState = uiState.copy(
            currencies = currencies,
            selectedCurrency = selectedCurrency,
            history = selectedCurrency?.let {
                getCurrencyHistoryUseCase(it, historyRefreshVersion)
            } ?: emptyList()
        )
    }
}
