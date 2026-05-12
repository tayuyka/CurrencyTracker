package com.yuya.currencytracker.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import kotlinx.coroutines.flow.Flow

@Dao
interface CurrencyDao {
    @Transaction
    @Query("SELECT * FROM currencies")
    fun observeCurrencies(): Flow<List<CurrencyWithHistory>>

    @Transaction
    @Query("SELECT * FROM currencies")
    suspend fun getCurrencies(): List<CurrencyWithHistory>

    @Transaction
    @Query("SELECT * FROM currencies WHERE id = :currencyId LIMIT 1")
    suspend fun getCurrency(currencyId: String): CurrencyWithHistory?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertCurrencies(currencies: List<CurrencyEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertHistory(history: List<CurrencyHistoryEntity>)

    @Query("DELETE FROM currencies")
    suspend fun deleteAllCurrencies()

    @Query("DELETE FROM currencies WHERE id = :currencyId")
    suspend fun deleteCurrency(currencyId: String)

    @Query("DELETE FROM currency_history WHERE currencyId = :currencyId")
    suspend fun deleteHistoryForCurrency(currencyId: String)

    @Query("DELETE FROM currency_history")
    suspend fun deleteAllHistory()

    @Transaction
    suspend fun replaceAll(currencies: List<CurrencyEntity>, history: List<CurrencyHistoryEntity>) {
        deleteAllCurrencies()
        upsertCurrencies(currencies)
        upsertHistory(history)
    }

    @Transaction
    suspend fun replaceCurrency(currency: CurrencyEntity, history: List<CurrencyHistoryEntity>) {
        upsertCurrencies(listOf(currency))
        deleteHistoryForCurrency(currency.id)
        upsertHistory(history)
    }
}
