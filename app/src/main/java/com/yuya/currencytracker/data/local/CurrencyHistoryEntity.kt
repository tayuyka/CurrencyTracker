package com.yuya.currencytracker.data.local

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index

@Entity(
    tableName = "currency_history",
    primaryKeys = ["currencyId", "dayLabel"],
    foreignKeys = [
        ForeignKey(
            entity = CurrencyEntity::class,
            parentColumns = ["id"],
            childColumns = ["currencyId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index("currencyId")]
)
data class CurrencyHistoryEntity(
    val currencyId: String,
    val dayLabel: String,
    val rate: Double
)
