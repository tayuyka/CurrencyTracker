package com.yuya.currencytracker.data.local

import android.content.Context
import android.content.SharedPreferences
import com.yuya.currencytracker.domain.model.Currency
import com.yuya.currencytracker.domain.model.CurrencyIcon
import org.json.JSONArray
import org.json.JSONObject

class CurrencyPreferencesDataSource(context: Context) {

    private val preferences: SharedPreferences =
        context.getSharedPreferences(PREFERENCES_NAME, Context.MODE_PRIVATE)

    fun loadCurrencies(): List<Currency>? {
        val raw = preferences.getString(KEY_CURRENCIES, null) ?: return null
        val array = JSONArray(raw)

        return buildList {
            for (index in 0 until array.length()) {
                val item = array.getJSONObject(index)
                add(
                    Currency(
                        id = item.getInt(KEY_ID),
                        code = item.getString(KEY_CODE),
                        name = item.getString(KEY_NAME),
                        currentRate = item.getDouble(KEY_RATE),
                        icon = CurrencyIcon.valueOf(item.getString(KEY_ICON)),
                        isFavorite = item.optBoolean(KEY_FAVORITE, false),
                        isStandard = item.optBoolean(KEY_STANDARD, false)
                    )
                )
            }
        }
    }

    fun saveCurrencies(currencies: List<Currency>) {
        val array = JSONArray()
        currencies.forEach { currency ->
            array.put(
                JSONObject()
                    .put(KEY_ID, currency.id)
                    .put(KEY_CODE, currency.code)
                    .put(KEY_NAME, currency.name)
                    .put(KEY_RATE, currency.currentRate)
                    .put(KEY_ICON, currency.icon.name)
                    .put(KEY_FAVORITE, currency.isFavorite)
                    .put(KEY_STANDARD, currency.isStandard)
            )
        }

        preferences.edit().putString(KEY_CURRENCIES, array.toString()).apply()
    }

    private companion object {
        const val PREFERENCES_NAME = "currency_tracker_prefs"
        const val KEY_CURRENCIES = "currencies"
        const val KEY_ID = "id"
        const val KEY_CODE = "code"
        const val KEY_NAME = "name"
        const val KEY_RATE = "rate"
        const val KEY_ICON = "icon"
        const val KEY_FAVORITE = "is_favorite"
        const val KEY_STANDARD = "is_standard"
    }
}
