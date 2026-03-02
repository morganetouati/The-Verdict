package com.theverdict.app.data.repository

import com.theverdict.app.data.local.DailyPlayManager
import com.theverdict.app.data.local.PreferencesManager
import com.theverdict.app.domain.model.GameResult
import com.theverdict.app.domain.repository.GameRepository
import kotlinx.coroutines.flow.first

class GameRepositoryImpl(
    private val dailyPlayManager: DailyPlayManager,
    private val prefs: PreferencesManager
) : GameRepository {

    // In-memory results for MVP (will be stored in Supabase for v2)
    private val results = mutableListOf<GameResult>()

    override suspend fun saveResult(result: GameResult) {
        results.add(result)

        // Update player stats
        prefs.incrementGamesPlayed()
        if (result.isCorrectVerdict) {
            prefs.incrementCorrectVerdicts()
        }
        prefs.addXp(result.xpEarned)
        prefs.updateStreak()

        // Increment daily play count
        dailyPlayManager.incrementPlayCount()
    }

    override suspend fun getResults(): List<GameResult> {
        return results.toList()
    }

    override suspend fun getDailyPlaysCount(): Int {
        val total = dailyPlayManager.getTotalAllowed()
        val remaining = dailyPlayManager.getRemainingPlays()
        return total - remaining
    }

    override suspend fun incrementDailyPlays() {
        dailyPlayManager.incrementPlayCount()
    }

    override suspend fun canPlay(): Boolean {
        return dailyPlayManager.canPlay()
    }

    override suspend fun addBonusPlays(count: Int) {
        dailyPlayManager.addBonusPlays()
    }
}
