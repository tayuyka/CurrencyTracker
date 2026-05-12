package com.yuya.currencytracker.data.local

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(
    entities = [CurrencyEntity::class, CurrencyHistoryEntity::class],
    version = 1,
    exportSchema = false
)
abstract class CurrencyDatabase : RoomDatabase() {
    abstract fun currencyDao(): CurrencyDao
}
