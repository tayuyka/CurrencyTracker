package com.yuya.currencytracker.di

import androidx.room.Room
import com.yuya.currencytracker.data.local.CurrencyDatabase
import com.yuya.currencytracker.data.remote.ExchangeRatesNetwork
import com.yuya.currencytracker.data.repository.RoomCurrencyRepository
import com.yuya.currencytracker.domain.repository.CurrencyRepository
import com.yuya.currencytracker.domain.usecase.AddCurrencyUseCase
import com.yuya.currencytracker.domain.usecase.DeleteCurrencyUseCase
import com.yuya.currencytracker.domain.usecase.GetCurrenciesUseCase
import com.yuya.currencytracker.domain.usecase.GetCurrencyHistoryUseCase
import com.yuya.currencytracker.domain.usecase.RefreshRatesUseCase
import com.yuya.currencytracker.domain.usecase.ToggleFavoriteUseCase
import com.yuya.currencytracker.domain.usecase.UpdateCurrencyUseCase
import com.yuya.currencytracker.presentation.viewmodel.CurrencyViewModel
import org.koin.android.ext.koin.androidContext
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module

val appModule = module {
    single {
        Room.databaseBuilder(
            androidContext(),
            CurrencyDatabase::class.java,
            "currency_tracker.db"
        ).build()
    }
    single { get<CurrencyDatabase>().currencyDao() }
    single { ExchangeRatesNetwork.createApi() }
    single<CurrencyRepository> { RoomCurrencyRepository(get(), get()) }
    factory { GetCurrenciesUseCase(get()) }
    factory { AddCurrencyUseCase(get()) }
    factory { UpdateCurrencyUseCase(get()) }
    factory { DeleteCurrencyUseCase(get()) }
    factory { ToggleFavoriteUseCase(get()) }
    factory { RefreshRatesUseCase(get()) }
    factory { GetCurrencyHistoryUseCase() }
    viewModelOf(::CurrencyViewModel)
}
