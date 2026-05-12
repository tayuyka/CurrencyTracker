package com.yuya.currencytracker.presentation.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CurrencyExchange
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.yuya.currencytracker.domain.model.Currency
import com.yuya.currencytracker.domain.model.CurrencyIcon
import com.yuya.currencytracker.presentation.ui.components.CurrencyCard
import com.yuya.currencytracker.presentation.ui.components.CurrencyDialog
import com.yuya.currencytracker.presentation.viewmodel.CurrencyFilter
import com.yuya.currencytracker.presentation.viewmodel.CurrencySort
import com.yuya.currencytracker.presentation.viewmodel.CurrencyUiState

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CurrencyListScreen(
    uiState: CurrencyUiState,
    onAddCurrency: () -> Unit,
    onEditCurrency: (Currency) -> Unit,
    onToggleFavorite: (String) -> Unit,
    onRefreshRates: () -> Unit,
    onSnackbarShown: () -> Unit,
    onCurrencyFilterChange: (CurrencyFilter) -> Unit,
    onCurrencySortChange: (CurrencySort) -> Unit,
    onDismissDialog: () -> Unit,
    onSaveCurrency: (code: String, name: String, icon: CurrencyIcon, isFavorite: Boolean) -> Unit,
    onDeleteCurrency: (String) -> Unit,
    onCurrencyClick: (String) -> Unit
) {
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(uiState.snackbarMessage) {
        val message = uiState.snackbarMessage ?: return@LaunchedEffect
        snackbarHostState.showSnackbar(
            message = message,
            duration = SnackbarDuration.Short
        )
        onSnackbarShown()
    }

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) },
        topBar = {
            TopAppBar(
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background,
                    titleContentColor = MaterialTheme.colorScheme.onBackground,
                    actionIconContentColor = MaterialTheme.colorScheme.onBackground
                ),
                title = {
                    Text(
                        text = "Курсы валют",
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.Bold
                    )
                },
                actions = {
                    IconButton(
                        onClick = onRefreshRates,
                        enabled = !uiState.isRefreshing
                    ) {
                        if (uiState.isRefreshing) {
                            CircularProgressIndicator(modifier = Modifier.size(22.dp))
                        } else {
                            Icon(
                                imageVector = Icons.Default.Refresh,
                                contentDescription = "Обновить курсы"
                            )
                        }
                    }
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = onAddCurrency,
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = MaterialTheme.colorScheme.onPrimary
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = "Добавить валюту"
                )
            }
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            if (uiState.currencies.isEmpty()) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.CurrencyExchange,
                        contentDescription = null,
                        modifier = Modifier.size(64.dp),
                        tint = MaterialTheme.colorScheme.onBackground
                    )
                    Text(
                        text = "Список валют пуст",
                        style = MaterialTheme.typography.headlineSmall,
                        modifier = Modifier.padding(top = 16.dp)
                    )
                    Text(
                        text = "Добавьте валюту через кнопку внизу экрана.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.padding(top = 8.dp)
                    )
                }
            } else {
                Column(modifier = Modifier.fillMaxSize()) {
                    CurrencyListControls(
                        uiState = uiState,
                        onCurrencyFilterChange = onCurrencyFilterChange,
                        onCurrencySortChange = onCurrencySortChange
                    )

                    LazyVerticalGrid(
                        columns = GridCells.Adaptive(minSize = 168.dp),
                        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp),
                        horizontalArrangement = Arrangement.spacedBy(12.dp),
                        modifier = Modifier.fillMaxSize()
                    ) {
                        items(
                            items = uiState.currencies,
                            key = { it.id }
                        ) { currency ->
                            CurrencyCard(
                                currency = currency,
                                onClick = { onEditCurrency(currency) },
                                onToggleFavorite = { onToggleFavorite(currency.id) },
                                onOpenHistory = { onCurrencyClick(currency.id) }
                            )
                        }
                    }
                }
            }

            if (uiState.showDialog) {
                CurrencyDialog(
                    currency = uiState.editingCurrency,
                    onDismiss = onDismissDialog,
                    onConfirm = onSaveCurrency,
                    onDelete = {
                        uiState.editingCurrency?.let { editingCurrency ->
                            onDeleteCurrency(editingCurrency.id)
                            onDismissDialog()
                        }
                    }
                )
            }
        }
    }
}

@Composable
private fun CurrencyListControls(
    uiState: CurrencyUiState,
    onCurrencyFilterChange: (CurrencyFilter) -> Unit,
    onCurrencySortChange: (CurrencySort) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Text(
            text = "${uiState.currencies.size} валют",
            style = MaterialTheme.typography.titleSmall,
            color = MaterialTheme.colorScheme.onBackground,
            fontWeight = FontWeight.Bold
        )

        Row(
            modifier = Modifier.horizontalScroll(rememberScrollState()),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            CurrencyFilter.entries.forEach { filter ->
                FilterChip(
                    selected = uiState.currencyFilter == filter,
                    onClick = { onCurrencyFilterChange(filter) },
                    colors = purpleFilterChipColors(),
                    label = { Text(filter.title()) }
                )
            }
        }

        Row(
            modifier = Modifier.horizontalScroll(rememberScrollState()),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            CurrencySort.entries.forEach { sort ->
                FilterChip(
                    selected = uiState.currencySort == sort,
                    onClick = { onCurrencySortChange(sort) },
                    colors = purpleFilterChipColors(),
                    label = { Text(sort.title()) }
                )
            }
        }
    }
}

@Composable
private fun purpleFilterChipColors() = FilterChipDefaults.filterChipColors(
    selectedContainerColor = MaterialTheme.colorScheme.primaryContainer,
    selectedLabelColor = MaterialTheme.colorScheme.primary,
    selectedLeadingIconColor = MaterialTheme.colorScheme.primary
)

private fun CurrencyFilter.title(): String {
    return when (this) {
        CurrencyFilter.ALL -> "Все"
        CurrencyFilter.FAVORITES -> "Избранные"
        CurrencyFilter.STANDARD -> "Стандартные"
        CurrencyFilter.CUSTOM -> "Свои"
    }
}

private fun CurrencySort.title(): String {
    return when (this) {
        CurrencySort.CODE -> "По коду"
        CurrencySort.RATE_ASC -> "Курс ↑"
        CurrencySort.RATE_DESC -> "Курс ↓"
    }
}
