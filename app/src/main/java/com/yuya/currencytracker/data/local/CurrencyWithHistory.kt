package com.yuya.currencytracker.data.local

import androidx.room.Embedded
import androidx.room.Relation

data class CurrencyWithHistory(
    @Embedded val currency: CurrencyEntity,
    @Relation(
        parentColumn = "id",
        entityColumn = "currencyId"
    )
    val history: List<CurrencyHistoryEntity>
)
