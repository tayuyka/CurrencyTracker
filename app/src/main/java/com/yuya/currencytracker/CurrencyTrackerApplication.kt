package com.yuya.currencytracker

import android.app.Application
import com.yuya.currencytracker.di.appModule
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin

class CurrencyTrackerApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        startKoin {
            androidContext(this@CurrencyTrackerApplication)
            modules(appModule)
        }
    }
}
