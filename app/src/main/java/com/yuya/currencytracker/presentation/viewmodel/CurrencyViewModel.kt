package com.yuya.currencytracker.presentation.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.yuya.currencytracker.domain.model.Currency
import com.yuya.currencytracker.domain.model.CurrencyHistoryEntry
import com.yuya.currencytracker.domain.model.CurrencyIcon
import com.yuya.currencytracker.domain.repository.CurrencyRepository
import com.yuya.currencytracker.domain.usecase.*
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

data class CurrencyUiState(
    val currencies: List<Currency> = emptyList(),
    val showDialog: Boolean = false,
    val editingCurrency: Currency? = null,
    val selectedCurrencyId: String? = null,
    val selectedCurrency: Currency? = null,
    val history: List<CurrencyHistoryEntry> = emptyList(),
    val isRefreshing: Boolean = false,
    val snackbarMessage: String? = null
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

    init {
        loadCurrencies()
        refreshRates()
        startAutoRefresh()
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

    fun deleteCurrency(currencyId: String) {
        deleteCurrencyUseCase(currencyId)

        if (uiState.selectedCurrencyId == currencyId) {
            uiState = uiState.copy(
                selectedCurrencyId = null,
                selectedCurrency = null,
                history = emptyList()
            )
        }

        loadCurrencies()
    }

    fun toggleFavorite(currencyId: String) {
        toggleFavoriteUseCase(currencyId)
        loadCurrencies()
    }

    fun refreshRates() {
        if (uiState.isRefreshing) return

        viewModelScope.launch {
            uiState = uiState.copy(isRefreshing = true)
            val result = refreshRatesUseCase()
            loadCurrencies()
            uiState = uiState.copy(
                isRefreshing = false,
                snackbarMessage = result.exceptionOrNull()?.message
                    ?.ifBlank { "Не удалось загрузить курсы" }
            )
        }
    }

    fun selectCurrency(currencyId: String) {
        val selectedCurrency = repository.getCurrency(currencyId)
        uiState = uiState.copy(
            selectedCurrencyId = currencyId,
            selectedCurrency = selectedCurrency,
            history = selectedCurrency?.let {
                getCurrencyHistoryUseCase(it)
            } ?: emptyList()
        )
    }

    fun refreshHistory() {
        val selectedCurrencyId = uiState.selectedCurrencyId ?: return
        refreshRates()
        selectCurrency(selectedCurrencyId)
    }

    fun onSnackbarShown() {
        uiState = uiState.copy(snackbarMessage = null)
    }

    private fun loadCurrencies() {
        val currencies = getCurrenciesUseCase()
        val selectedCurrencyId = uiState.selectedCurrencyId
        val selectedCurrency = selectedCurrencyId?.let(repository::getCurrency)

        uiState = uiState.copy(
            currencies = currencies,
            selectedCurrency = selectedCurrency,
            history = selectedCurrency?.let {
                getCurrencyHistoryUseCase(it)
            } ?: emptyList()
        )
    }

    private fun startAutoRefresh() {
        viewModelScope.launch {
            while (true) {
                delay(AUTO_REFRESH_DELAY_MS)
                refreshRates()
            }
        }
    }

    private companion object {
        const val AUTO_REFRESH_DELAY_MS = 60_000L
    }
}
