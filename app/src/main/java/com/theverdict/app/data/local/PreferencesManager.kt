package com.theverdict.app.data.local

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.*
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.Instant

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

        // ── Daily Case ───────────────────────────────────────────────
        private val KEY_DAILY_CASE_PLAYED_DATE = stringPreferencesKey("daily_case_played_date")

        // ── Persistent Credibility ───────────────────────────────────
        private val KEY_PERSISTENT_CREDIBILITY = intPreferencesKey("persistent_credibility")
        private val KEY_CREDIBILITY_LOCKED_UNTIL = longPreferencesKey("credibility_locked_until")
        private val KEY_CREDIBILITY_LAST_REGEN = longPreferencesKey("credibility_last_regen")
        private val KEY_CREDIBILITY_TICKETS = intPreferencesKey("credibility_tickets")
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

    // ── Daily Case ────────────────────────────────────────────────────

    suspend fun getDailyCasePlayedDate(): String {
        return context.dataStore.data.map { it[KEY_DAILY_CASE_PLAYED_DATE] ?: "" }.first()
    }

    suspend fun setDailyCasePlayed() {
        val today = LocalDate.now().format(dateFormat)
        context.dataStore.edit { it[KEY_DAILY_CASE_PLAYED_DATE] = today }
    }

    suspend fun isDailyCaseDoneToday(): Boolean {
        val today = LocalDate.now().format(dateFormat)
        return getDailyCasePlayedDate() == today
    }

    suspend fun getLastPlayDate(): String {
        return context.dataStore.data.map { it[KEY_LAST_PLAY_DATE] ?: "" }.first()
    }

    // ── Persistent Credibility ────────────────────────────────────────

    /** Returns current credibility (0-100), applying passive regen first. */
    suspend fun getPersistentCredibility(): Int {
        regenerateCredibility()
        return context.dataStore.data.map { it[KEY_PERSISTENT_CREDIBILITY] ?: 100 }.first()
    }

    /** Passive regen: +1 pt per minute since last regen, capped at 100. */
    suspend fun regenerateCredibility() {
        context.dataStore.edit { prefs ->
            val now = Instant.now().toEpochMilli()
            val lastRegen = prefs[KEY_CREDIBILITY_LAST_REGEN] ?: now
            val current = prefs[KEY_PERSISTENT_CREDIBILITY] ?: 100
            val minutesElapsed = ((now - lastRegen) / 60_000L).toInt().coerceAtLeast(0)
            val newCredibility = (current + minutesElapsed).coerceAtMost(100)
            prefs[KEY_PERSISTENT_CREDIBILITY] = newCredibility
            prefs[KEY_CREDIBILITY_LAST_REGEN] = now
        }
    }

    /**
     * Applies penalty from useless clicks.
     * Penalty = uselessClicks * 8 pts.
     * If credibility reaches 0 → lock for 30 minutes.
     */
    suspend fun applyCredibilityPenalty(uselessClicks: Int) {
        if (uselessClicks < 3) return // Give beginners a break
        regenerateCredibility()
        context.dataStore.edit { prefs ->
            val current = prefs[KEY_PERSISTENT_CREDIBILITY] ?: 100
            val penalty = uselessClicks * 8
            val newVal = (current - penalty).coerceAtLeast(0)
            prefs[KEY_PERSISTENT_CREDIBILITY] = newVal
            if (newVal == 0) {
                val lockUntil = Instant.now().toEpochMilli() + 30 * 60 * 1000L
                prefs[KEY_CREDIBILITY_LOCKED_UNTIL] = lockUntil
            }
        }
    }

    suspend fun isCredibilityLocked(): Boolean {
        val lockedUntil = context.dataStore.data.map { it[KEY_CREDIBILITY_LOCKED_UNTIL] ?: 0L }.first()
        return Instant.now().toEpochMilli() < lockedUntil
    }

    suspend fun getCredibilityLockRemainingMs(): Long {
        val lockedUntil = context.dataStore.data.map { it[KEY_CREDIBILITY_LOCKED_UNTIL] ?: 0L }.first()
        return (lockedUntil - Instant.now().toEpochMilli()).coerceAtLeast(0L)
    }

    suspend fun getCredibilityTickets(): Int {
        return context.dataStore.data.map { it[KEY_CREDIBILITY_TICKETS] ?: 0 }.first()
    }

    suspend fun incrementCredibilityTickets() {
        context.dataStore.edit { prefs ->
            prefs[KEY_CREDIBILITY_TICKETS] = (prefs[KEY_CREDIBILITY_TICKETS] ?: 0) + 1
        }
    }

    /** Consumes 1 ticket: unlocks immediately, restores credibility to 50. */
    suspend fun useCredibilityTicket(): Boolean {
        val tickets = getCredibilityTickets()
        if (tickets <= 0) return false
        context.dataStore.edit { prefs ->
            prefs[KEY_CREDIBILITY_TICKETS] = tickets - 1
            prefs[KEY_CREDIBILITY_LOCKED_UNTIL] = 0L
            prefs[KEY_PERSISTENT_CREDIBILITY] = 50
            prefs[KEY_CREDIBILITY_LAST_REGEN] = Instant.now().toEpochMilli()
        }
        return true
    }
}
