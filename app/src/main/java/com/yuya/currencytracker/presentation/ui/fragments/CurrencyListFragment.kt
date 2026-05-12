package com.yuya.currencytracker.presentation.ui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.platform.ViewCompositionStrategy
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.yuya.currencytracker.MainActivity
import com.yuya.currencytracker.presentation.ui.screens.CurrencyListScreen
import com.yuya.currencytracker.presentation.ui.theme.CurrencyTrackerTheme
import com.yuya.currencytracker.presentation.viewmodel.CurrencyViewModel
import com.yuya.currencytracker.presentation.viewmodel.CurrencyViewModelFactory

class CurrencyListFragment : Fragment() {

    private val viewModel: CurrencyViewModel by activityViewModels {
        CurrencyViewModelFactory(requireContext().applicationContext)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return ComposeView(requireContext()).apply {
            setViewCompositionStrategy(ViewCompositionStrategy.DisposeOnViewTreeLifecycleDestroyed)
            setContent {
                CurrencyTrackerTheme {
                    CurrencyListScreen(
                        uiState = viewModel.uiState,
                        onAddCurrency = viewModel::onAddCurrencyClick,
                        onEditCurrency = viewModel::onEditCurrencyClick,
                        onToggleFavorite = viewModel::toggleFavorite,
                        onRefreshRates = viewModel::refreshRates,
                        onSnackbarShown = viewModel::onSnackbarShown,
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
