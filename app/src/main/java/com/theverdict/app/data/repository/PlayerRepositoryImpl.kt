package com.theverdict.app.data.repository

import com.theverdict.app.data.local.PreferencesManager
import com.theverdict.app.domain.model.PlayerLevel
import com.theverdict.app.domain.model.PlayerProfile
import com.theverdict.app.domain.repository.PlayerRepository

class PlayerRepositoryImpl(
    private val prefs: PreferencesManager
) : PlayerRepository {

    override suspend fun getProfile(): PlayerProfile {
        val xp = prefs.getTotalXp()
        val gamesPlayed = prefs.getGamesPlayed()
        val correctVerdicts = prefs.getCorrectVerdicts()
        val (currentStreak, bestStreak) = prefs.getStreak()
        val displayName = prefs.getDisplayName()
        val avatar = prefs.getAvatar()

        return PlayerProfile(
            displayName = displayName,
            avatarUrl = avatar,
            totalXp = xp,
            level = PlayerLevel.fromXp(xp),
            gamesPlayed = gamesPlayed,
            correctVerdicts = correctVerdicts,
            currentStreak = currentStreak,
            bestStreak = bestStreak
        )
    }

    override suspend fun updateProfile(profile: PlayerProfile) {
        prefs.setDisplayName(profile.displayName)
        prefs.setAvatar(profile.avatarUrl)
    }

    override suspend fun addXp(amount: Int) {
        prefs.addXp(amount)
    }
}
