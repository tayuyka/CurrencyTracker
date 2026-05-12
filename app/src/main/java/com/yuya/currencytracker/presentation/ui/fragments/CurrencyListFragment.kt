package com.yuya.currencytracker.presentation.ui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.platform.ViewCompositionStrategy
import androidx.fragment.app.Fragment
import com.yuya.currencytracker.MainActivity
import com.yuya.currencytracker.presentation.ui.screens.CurrencyListScreen
import com.yuya.currencytracker.presentation.ui.theme.CurrencyTrackerTheme
import com.yuya.currencytracker.presentation.viewmodel.CurrencyViewModel
import org.koin.androidx.viewmodel.ext.android.activityViewModel

class CurrencyListFragment : Fragment() {

    private val viewModel: CurrencyViewModel by activityViewModel()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return ComposeView(requireContext()).apply {
            setViewCompositionStrategy(ViewCompositionStrategy.DisposeOnViewTreeLifecycleDestroyed)
            setContent {
                val uiState by viewModel.uiState.collectAsState()
                CurrencyTrackerTheme {
                    CurrencyListScreen(
                        uiState = uiState,
                        onAddCurrency = viewModel::onAddCurrencyClick,
                        onEditCurrency = viewModel::onEditCurrencyClick,
                        onToggleFavorite = viewModel::toggleFavorite,
                        onRefreshRates = viewModel::refreshRates,
                        onSnackbarShown = viewModel::onSnackbarShown,
                        onCurrencyFilterChange = viewModel::setCurrencyFilter,
                        onCurrencySortChange = viewModel::setCurrencySort,
                        onDismissDialog = viewModel::onDismissDialog,
                        onSaveCurrency = viewModel::saveCurrency,
                        onDeleteCurrency = viewModel::deleteCurrency,
                        onCurrencyClick = { currencyId ->
                            viewModel.selectCurrency(currencyId)
                            (activity as? MainActivity)?.openCurrencyHistory(currencyId)
                        }
                    )
                }
            }
        }
    }

    companion object {
        fun newInstance(): CurrencyListFragment = CurrencyListFragment()
    }
}
