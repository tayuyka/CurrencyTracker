package com.yuya.currencytracker.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "currencies")
data class CurrencyEntity(
    @PrimaryKey val id: String,
    val code: String,
    val name: String,
    val currentRate: Double,
    val icon: String,
    val isFavorite: Boolean,
    val isStandard: Boolean
)
