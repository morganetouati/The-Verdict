package com.theverdict.app.data.local

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.theverdict.app.domain.model.PlayerProfile
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "the_verdict_prefs")

class PreferencesManager(private val context: Context) {

    private object Keys {
        val REPUTATION = intPreferencesKey("reputation")
        val CASES_PLAYED = intPreferencesKey("cases_played")
        val CORRECT_VERDICTS = intPreferencesKey("correct_verdicts")
        val WRONG_VERDICTS = intPreferencesKey("wrong_verdicts")
        val CURRENT_THEME_INDEX = intPreferencesKey("current_theme_index")
        val CURRENT_CASE_INDEX = intPreferencesKey("current_case_index")
        val THEME_PROGRESS = stringPreferencesKey("theme_progress")
        val COMPLETED_CASE_IDS = stringPreferencesKey("completed_case_ids")
        val HAS_SEEN_TUTORIAL = booleanPreferencesKey("has_seen_tutorial")
    }

    val playerProfile: Flow<PlayerProfile> = context.dataStore.data.map { prefs ->
        PlayerProfile(
            reputation = prefs[Keys.REPUTATION] ?: 0,
            casesPlayed = prefs[Keys.CASES_PLAYED] ?: 0,
            correctVerdicts = prefs[Keys.CORRECT_VERDICTS] ?: 0,
            wrongVerdicts = prefs[Keys.WRONG_VERDICTS] ?: 0,
            currentThemeIndex = prefs[Keys.CURRENT_THEME_INDEX] ?: 0,
            currentCaseIndex = prefs[Keys.CURRENT_CASE_INDEX] ?: 0,
            themeProgress = deserializeIntMap(prefs[Keys.THEME_PROGRESS]),
            completedCaseIds = deserializeIntSet(prefs[Keys.COMPLETED_CASE_IDS])
        )
    }

    suspend fun updateProfile(profile: PlayerProfile) {
        context.dataStore.edit { prefs ->
            prefs[Keys.REPUTATION] = profile.reputation
            prefs[Keys.CASES_PLAYED] = profile.casesPlayed
            prefs[Keys.CORRECT_VERDICTS] = profile.correctVerdicts
            prefs[Keys.WRONG_VERDICTS] = profile.wrongVerdicts
            prefs[Keys.CURRENT_THEME_INDEX] = profile.currentThemeIndex
            prefs[Keys.CURRENT_CASE_INDEX] = profile.currentCaseIndex
            prefs[Keys.THEME_PROGRESS] = Json.encodeToString(profile.themeProgress)
            prefs[Keys.COMPLETED_CASE_IDS] = Json.encodeToString(profile.completedCaseIds)
        }
    }

    suspend fun resetProfile() {
        context.dataStore.edit { it.clear() }
    }

    val hasSeenTutorial: Flow<Boolean> = context.dataStore.data.map { prefs ->
        prefs[Keys.HAS_SEEN_TUTORIAL] ?: false
    }

    suspend fun setTutorialSeen() {
        context.dataStore.edit { prefs ->
            prefs[Keys.HAS_SEEN_TUTORIAL] = true
        }
    }

    private fun deserializeIntMap(raw: String?): Map<Int, Int> {
        if (raw.isNullOrEmpty()) return emptyMap()
        return try {
            Json.decodeFromString<Map<Int, Int>>(raw)
        } catch (_: Exception) {
            emptyMap()
        }
    }

    private fun deserializeIntSet(raw: String?): Set<Int> {
        if (raw.isNullOrEmpty()) return emptySet()
        return try {
            Json.decodeFromString<Set<Int>>(raw)
        } catch (_: Exception) {
            emptySet()
        }
    }
}
