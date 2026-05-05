package com.yuya.currencytracker.presentation.ui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.platform.ViewCompositionStrategy
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.yuya.currencytracker.presentation.ui.screens.CurrencyHistoryScreen
import com.yuya.currencytracker.presentation.ui.theme.CurrencyTrackerTheme
import com.yuya.currencytracker.presentation.viewmodel.CurrencyViewModel
import com.yuya.currencytracker.presentation.viewmodel.CurrencyViewModelFactory

class CurrencyHistoryFragment : Fragment() {

    private val viewModel: CurrencyViewModel by activityViewModels {
        CurrencyViewModelFactory(requireContext().applicationContext)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val currencyId = requireArguments().getInt(ARG_CURRENCY_ID)
        if (viewModel.uiState.selectedCurrencyId != currencyId) {
            viewModel.selectCurrency(currencyId)
        }
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
                    CurrencyHistoryScreen(
                        uiState = viewModel.uiState,
                        onBack = { parentFragmentManager.popBackStack() },
                        onRefresh = viewModel::refreshHistory
                    )
                }
            }
        }
    }

    companion object {
        const val TAG = "CurrencyHistoryFragment"
        private const val ARG_CURRENCY_ID = "arg_currency_id"

        fun newInstance(currencyId: Int): CurrencyHistoryFragment {
            return CurrencyHistoryFragment().apply {
                arguments = Bundle().apply {
                    putInt(ARG_CURRENCY_ID, currencyId)
                }
            }
        }
    }
}
