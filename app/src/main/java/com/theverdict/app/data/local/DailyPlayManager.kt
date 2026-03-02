package com.theverdict.app.data.local

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.*
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import java.time.LocalDate
import java.time.format.DateTimeFormatter

private val Context.dailyStore: DataStore<Preferences> by preferencesDataStore(name = "daily_plays")

/**
 * Manages the daily play limit (5 free + 5 bonus from rewarded ad).
 * Resets at midnight.
 */
class DailyPlayManager(private val context: Context) {

    companion object {
        private const val FREE_PLAYS_PER_DAY = 5
        private const val BONUS_PLAYS_PER_AD = 5

        private val KEY_PLAY_COUNT = intPreferencesKey("play_count")
        private val KEY_BONUS_PLAYS = intPreferencesKey("bonus_plays")
        private val KEY_PLAY_DATE = stringPreferencesKey("play_date")
    }

    private val dateFormat = DateTimeFormatter.ISO_LOCAL_DATE

    private suspend fun resetIfNewDay() {
        context.dailyStore.edit { prefs ->
            val today = LocalDate.now().format(dateFormat)
            val lastDate = prefs[KEY_PLAY_DATE] ?: ""
            if (lastDate != today) {
                prefs[KEY_PLAY_COUNT] = 0
                prefs[KEY_BONUS_PLAYS] = 0
                prefs[KEY_PLAY_DATE] = today
            }
        }
    }

    suspend fun canPlay(): Boolean {
        resetIfNewDay()
        val prefs = context.dailyStore.data.first()
        val playCount = prefs[KEY_PLAY_COUNT] ?: 0
        val bonusPlays = prefs[KEY_BONUS_PLAYS] ?: 0
        val totalAllowed = FREE_PLAYS_PER_DAY + bonusPlays
        return playCount < totalAllowed
    }

    suspend fun getRemainingPlays(): Int {
        resetIfNewDay()
        val prefs = context.dailyStore.data.first()
        val playCount = prefs[KEY_PLAY_COUNT] ?: 0
        val bonusPlays = prefs[KEY_BONUS_PLAYS] ?: 0
        val totalAllowed = FREE_PLAYS_PER_DAY + bonusPlays
        return maxOf(0, totalAllowed - playCount)
    }

    suspend fun getTotalAllowed(): Int {
        resetIfNewDay()
        val prefs = context.dailyStore.data.first()
        val bonusPlays = prefs[KEY_BONUS_PLAYS] ?: 0
        return FREE_PLAYS_PER_DAY + bonusPlays
    }

    suspend fun incrementPlayCount() {
        resetIfNewDay()
        context.dailyStore.edit { prefs ->
            prefs[KEY_PLAY_COUNT] = (prefs[KEY_PLAY_COUNT] ?: 0) + 1
        }
    }

    suspend fun addBonusPlays() {
        resetIfNewDay()
        context.dailyStore.edit { prefs ->
            prefs[KEY_BONUS_PLAYS] = (prefs[KEY_BONUS_PLAYS] ?: 0) + BONUS_PLAYS_PER_AD
        }
    }

    suspend fun hasUsedFreePlays(): Boolean {
        resetIfNewDay()
        val prefs = context.dailyStore.data.first()
        val playCount = prefs[KEY_PLAY_COUNT] ?: 0
        return playCount >= FREE_PLAYS_PER_DAY
    }
}
