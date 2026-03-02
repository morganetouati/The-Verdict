package com.theverdict.app.data.local

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.*
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import java.time.LocalDate
import java.time.format.DateTimeFormatter

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "verdict_prefs")

class PreferencesManager(private val context: Context) {

    companion object {
        private val KEY_DISPLAY_NAME = stringPreferencesKey("display_name")
        private val KEY_TOTAL_XP = intPreferencesKey("total_xp")
        private val KEY_GAMES_PLAYED = intPreferencesKey("games_played")
        private val KEY_CORRECT_VERDICTS = intPreferencesKey("correct_verdicts")
        private val KEY_CURRENT_STREAK = intPreferencesKey("current_streak")
        private val KEY_BEST_STREAK = intPreferencesKey("best_streak")
        private val KEY_LAST_PLAY_DATE = stringPreferencesKey("last_play_date")
        private val KEY_HAS_SEEN_ONBOARDING = booleanPreferencesKey("has_seen_onboarding")
        private val KEY_AVATAR = stringPreferencesKey("avatar_emoji")
        private val KEY_PLAYED_IDS = stringPreferencesKey("played_video_ids")
        private val KEY_MUSIC_ENABLED = booleanPreferencesKey("music_enabled")
    }

    private val dateFormat = DateTimeFormatter.ISO_LOCAL_DATE

    suspend fun getDisplayName(): String {
        return context.dataStore.data.map { it[KEY_DISPLAY_NAME] ?: "Joueur" }.first()
    }

    suspend fun setDisplayName(name: String) {
        context.dataStore.edit { it[KEY_DISPLAY_NAME] = name }
    }

    suspend fun getTotalXp(): Int {
        return context.dataStore.data.map { it[KEY_TOTAL_XP] ?: 0 }.first()
    }

    suspend fun addXp(amount: Int) {
        context.dataStore.edit { prefs ->
            prefs[KEY_TOTAL_XP] = (prefs[KEY_TOTAL_XP] ?: 0) + amount
        }
    }

    suspend fun getGamesPlayed(): Int {
        return context.dataStore.data.map { it[KEY_GAMES_PLAYED] ?: 0 }.first()
    }

    suspend fun incrementGamesPlayed() {
        context.dataStore.edit { prefs ->
            prefs[KEY_GAMES_PLAYED] = (prefs[KEY_GAMES_PLAYED] ?: 0) + 1
        }
    }

    suspend fun getCorrectVerdicts(): Int {
        return context.dataStore.data.map { it[KEY_CORRECT_VERDICTS] ?: 0 }.first()
    }

    suspend fun incrementCorrectVerdicts() {
        context.dataStore.edit { prefs ->
            prefs[KEY_CORRECT_VERDICTS] = (prefs[KEY_CORRECT_VERDICTS] ?: 0) + 1
        }
    }

    suspend fun getStreak(): Pair<Int, Int> {
        val prefs = context.dataStore.data.first()
        return Pair(
            prefs[KEY_CURRENT_STREAK] ?: 0,
            prefs[KEY_BEST_STREAK] ?: 0
        )
    }

    suspend fun updateStreak() {
        context.dataStore.edit { prefs ->
            val today = LocalDate.now().format(dateFormat)
            val lastDate = prefs[KEY_LAST_PLAY_DATE] ?: ""
            val yesterday = LocalDate.now().minusDays(1).format(dateFormat)

            val currentStreak = prefs[KEY_CURRENT_STREAK] ?: 0
            val bestStreak = prefs[KEY_BEST_STREAK] ?: 0

            val newStreak = when (lastDate) {
                today -> currentStreak // Already played today
                yesterday -> currentStreak + 1 // Continuing streak
                else -> 1 // Streak broken, start new
            }

            prefs[KEY_CURRENT_STREAK] = newStreak
            prefs[KEY_BEST_STREAK] = maxOf(bestStreak, newStreak)
            prefs[KEY_LAST_PLAY_DATE] = today
        }
    }

    suspend fun getAvatar(): String {
        return context.dataStore.data.map { it[KEY_AVATAR] ?: "🕵️" }.first()
    }

    suspend fun setAvatar(emoji: String) {
        context.dataStore.edit { it[KEY_AVATAR] = emoji }
    }

    /**
     * Sync check for onboarding (uses SharedPreferences fallback for instant access).
     */
    fun hasSeenOnboardingSync(): Boolean {
        val sharedPrefs = context.getSharedPreferences("verdict_onboarding", Context.MODE_PRIVATE)
        return sharedPrefs.getBoolean("has_seen_onboarding", false)
    }

    fun setOnboardingComplete() {
        val sharedPrefs = context.getSharedPreferences("verdict_onboarding", Context.MODE_PRIVATE)
        sharedPrefs.edit().putBoolean("has_seen_onboarding", true).apply()
    }

    // ── Played video IDs (persist across restarts) ──

    suspend fun getPlayedIds(): Set<String> {
        val raw = context.dataStore.data.map { it[KEY_PLAYED_IDS] ?: "" }.first()
        return if (raw.isBlank()) emptySet() else raw.split(",").toSet()
    }

    suspend fun addPlayedId(id: String) {
        context.dataStore.edit { prefs ->
            val current = prefs[KEY_PLAYED_IDS] ?: ""
            val ids = if (current.isBlank()) mutableSetOf() else current.split(",").toMutableSet()
            ids.add(id)
            prefs[KEY_PLAYED_IDS] = ids.joinToString(",")
        }
    }

    suspend fun clearPlayedIds() {
        context.dataStore.edit { prefs -> prefs[KEY_PLAYED_IDS] = "" }
    }

    // ── Music preference ──

    suspend fun isMusicEnabled(): Boolean {
        return context.dataStore.data.map { it[KEY_MUSIC_ENABLED] ?: true }.first()
    }

    fun isMusicEnabledSync(): Boolean {
        val sharedPrefs = context.getSharedPreferences("verdict_onboarding", Context.MODE_PRIVATE)
        return sharedPrefs.getBoolean("music_enabled", true)
    }

    suspend fun setMusicEnabled(enabled: Boolean) {
        context.dataStore.edit { it[KEY_MUSIC_ENABLED] = enabled }
        // Also persist to SharedPreferences for sync access
        val sharedPrefs = context.getSharedPreferences("verdict_onboarding", Context.MODE_PRIVATE)
        sharedPrefs.edit().putBoolean("music_enabled", enabled).apply()
    }
}
