package com.yuya.currencytracker.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.yuya.currencytracker.domain.model.Currency
import com.yuya.currencytracker.domain.model.CurrencyHistoryEntry
import com.yuya.currencytracker.domain.model.CurrencyIcon
import com.yuya.currencytracker.domain.repository.CurrencyRepository
import com.yuya.currencytracker.domain.usecase.AddCurrencyUseCase
import com.yuya.currencytracker.domain.usecase.DeleteCurrencyUseCase
import com.yuya.currencytracker.domain.usecase.GetCurrenciesUseCase
import com.yuya.currencytracker.domain.usecase.GetCurrencyHistoryUseCase
import com.yuya.currencytracker.domain.usecase.RefreshRatesUseCase
import com.yuya.currencytracker.domain.usecase.ToggleFavoriteUseCase
import com.yuya.currencytracker.domain.usecase.UpdateCurrencyUseCase
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

enum class CurrencyFilter {
    ALL,
    FAVORITES,
    STANDARD,
    CUSTOM
}

enum class CurrencySort {
    CODE,
    RATE_ASC,
    RATE_DESC
}

enum class HistorySort {
    DATE_DESC,
    DATE_ASC,
    RATE_ASC,
    RATE_DESC
}

data class CurrencyUiState(
    val currencies: List<Currency> = emptyList(),
    val allCurrencies: List<Currency> = emptyList(),
    val showDialog: Boolean = false,
    val editingCurrency: Currency? = null,
    val selectedCurrencyId: String? = null,
    val selectedCurrency: Currency? = null,
    val history: List<CurrencyHistoryEntry> = emptyList(),
    val currencyFilter: CurrencyFilter = CurrencyFilter.ALL,
    val currencySort: CurrencySort = CurrencySort.CODE,
    val historySort: HistorySort = HistorySort.DATE_DESC,
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

    private val _uiState = MutableStateFlow(CurrencyUiState())
    val uiState: StateFlow<CurrencyUiState> = _uiState.asStateFlow()

    init {
        observeCurrencies()
        refreshRates()
        startAutoRefresh()
    }

    fun onAddCurrencyClick() {
        _uiState.update { it.copy(showDialog = true, editingCurrency = null) }
    }

    fun onEditCurrencyClick(currency: Currency) {
        _uiState.update { it.copy(showDialog = true, editingCurrency = currency) }
    }

    fun onDismissDialog() {
        _uiState.update { it.copy(showDialog = false, editingCurrency = null) }
    }

    fun saveCurrency(code: String, name: String, icon: CurrencyIcon, isFavorite: Boolean) {
        viewModelScope.launch {
            val editingCurrency = _uiState.value.editingCurrency
            if (editingCurrency == null) {
                addCurrencyUseCase(code, name, icon, isFavorite)
            } else {
                updateCurrencyUseCase(editingCurrency.id, code, name, icon, isFavorite)
            }

            _uiState.update { it.copy(showDialog = false, editingCurrency = null) }
        }
    }

    fun deleteCurrency(currencyId: String) {
        viewModelScope.launch {
            deleteCurrencyUseCase(currencyId)
            if (_uiState.value.selectedCurrencyId == currencyId) {
                _uiState.update {
                    it.copy(
                        selectedCurrencyId = null,
                        selectedCurrency = null,
                        history = emptyList()
                    )
                }
            }
        }
    }

    fun toggleFavorite(currencyId: String) {
        viewModelScope.launch {
            toggleFavoriteUseCase(currencyId)
        }
    }

    fun refreshRates() {
        if (_uiState.value.isRefreshing) return

        viewModelScope.launch {
            _uiState.update { it.copy(isRefreshing = true) }
            val result = refreshRatesUseCase()
            _uiState.update {
                it.copy(
                    isRefreshing = false,
                    snackbarMessage = result.exceptionOrNull()?.message
                        ?.ifBlank { "Не удалось загрузить курсы" }
                )
            }
        }
    }

    fun selectCurrency(currencyId: String) {
        viewModelScope.launch {
            val selectedCurrency = repository.getCurrency(currencyId)
            _uiState.update { state ->
                state.copy(
                    selectedCurrencyId = currencyId,
                    selectedCurrency = selectedCurrency,
                    history = selectedCurrency?.let {
                        applyHistorySort(getCurrencyHistoryUseCase(it), state.historySort)
                    } ?: emptyList()
                )
            }
        }
    }

    fun refreshHistory() {
        refreshRates()
    }

    fun setCurrencyFilter(filter: CurrencyFilter) {
        _uiState.update { state ->
            state.copy(
                currencyFilter = filter,
                currencies = applyCurrencyFilterAndSort(state.allCurrencies, filter, state.currencySort)
            )
        }
    }

    fun setCurrencySort(sort: CurrencySort) {
        _uiState.update { state ->
            state.copy(
                currencySort = sort,
                currencies = applyCurrencyFilterAndSort(state.allCurrencies, state.currencyFilter, sort)
            )
        }
    }

    fun setHistorySort(sort: HistorySort) {
        _uiState.update { state ->
            state.copy(
                historySort = sort,
                history = applyHistorySort(state.history, sort)
            )
        }
    }

    fun onSnackbarShown() {
        _uiState.update { it.copy(snackbarMessage = null) }
    }

    private fun observeCurrencies() {
        viewModelScope.launch {
            repository.initialize()
            getCurrenciesUseCase().collect { currencies ->
                _uiState.update { state ->
                    val selectedCurrency = state.selectedCurrencyId?.let { selectedId ->
                        currencies.firstOrNull { it.id == selectedId }
                    }
                    state.copy(
                        allCurrencies = currencies,
                        currencies = applyCurrencyFilterAndSort(
                            currencies,
                            state.currencyFilter,
                            state.currencySort
                        ),
                        selectedCurrency = selectedCurrency,
                        history = selectedCurrency?.let {
                            applyHistorySort(getCurrencyHistoryUseCase(it), state.historySort)
                        } ?: state.history
                    )
                }
            }
        }
    }

    private fun applyCurrencyFilterAndSort(
        currencies: List<Currency>,
        filter: CurrencyFilter,
        sort: CurrencySort
    ): List<Currency> {
        val filtered = when (filter) {
            CurrencyFilter.ALL -> currencies
            CurrencyFilter.FAVORITES -> currencies.filter { it.isFavorite }
            CurrencyFilter.STANDARD -> currencies.filter { it.isStandard }
            CurrencyFilter.CUSTOM -> currencies.filterNot { it.isStandard }
        }

        return when (sort) {
            CurrencySort.CODE -> filtered.sortedBy { it.code }
            CurrencySort.RATE_ASC -> filtered.sortedBy { it.currentRate }
            CurrencySort.RATE_DESC -> filtered.sortedByDescending { it.currentRate }
        }
    }

    private fun applyHistorySort(
        history: List<CurrencyHistoryEntry>,
        sort: HistorySort
    ): List<CurrencyHistoryEntry> {
        return when (sort) {
            HistorySort.DATE_DESC -> history.sortedByDescending { it.dayLabel }
            HistorySort.DATE_ASC -> history.sortedBy { it.dayLabel }
            HistorySort.RATE_ASC -> history.sortedBy { it.rate }
            HistorySort.RATE_DESC -> history.sortedByDescending { it.rate }
        }
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
