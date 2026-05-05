package com.yuya.currencytracker

import android.os.Bundle
import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.commit
import com.yuya.currencytracker.presentation.ui.fragments.CurrencyHistoryFragment
import com.yuya.currencytracker.presentation.ui.fragments.CurrencyListFragment

class MainActivity : FragmentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        if (savedInstanceState == null) {
            supportFragmentManager.commit {
                replace(R.id.fragment_container, CurrencyListFragment.newInstance())
            }
        }
    }

    fun openCurrencyHistory(currencyId: Int) {
        supportFragmentManager.commit {
            replace(
                R.id.fragment_container,
                CurrencyHistoryFragment.newInstance(currencyId)
            )
            addToBackStack(CurrencyHistoryFragment.TAG)
        }
    }
}
