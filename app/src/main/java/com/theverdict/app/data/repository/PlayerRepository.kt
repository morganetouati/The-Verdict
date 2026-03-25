package com.theverdict.app.data.repository

import com.theverdict.app.data.local.PreferencesManager
import com.theverdict.app.domain.model.Case
import com.theverdict.app.domain.model.CaseTheme
import com.theverdict.app.domain.model.CaseType
import com.theverdict.app.domain.model.PlayerProfile
import com.theverdict.app.domain.model.Rank
import com.theverdict.app.domain.model.VerdictResult
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first

class PlayerRepository(private val prefs: PreferencesManager) {

    val profile: Flow<PlayerProfile> = prefs.playerProfile

    suspend fun getProfile(): PlayerProfile = prefs.playerProfile.first()

    suspend fun applyVerdict(
        currentProfile: PlayerProfile,
        case: Case,
        selectedLiarIds: List<Int>,
        isReplay: Boolean = false
    ): VerdictResult {
        val isCorrect = checkAnswer(case, selectedLiarIds)
        val pointsChange = if (isReplay) 0 else calculatePoints(case.difficulte, isCorrect)
        val newReputation = (currentProfile.reputation + pointsChange).coerceIn(0, 100)
        val oldRank = currentProfile.rank
        val newRank = Rank.fromReputation(newReputation)

        if (!isReplay) {
            val newThemeProgress = currentProfile.themeProgress.toMutableMap()
            val themeIndex = case.theme.ordinal
            val currentCount = newThemeProgress[themeIndex] ?: 0
            newThemeProgress[themeIndex] = currentCount + 1

            val updatedProfile = currentProfile.copy(
                reputation = newReputation,
                casesPlayed = currentProfile.casesPlayed + 1,
                correctVerdicts = currentProfile.correctVerdicts + if (isCorrect) 1 else 0,
                wrongVerdicts = currentProfile.wrongVerdicts + if (!isCorrect) 1 else 0,
                themeProgress = newThemeProgress,
                completedCaseIds = currentProfile.completedCaseIds + case.id
            )
            prefs.updateProfile(updatedProfile)
        }

        return VerdictResult(
            isCorrect = isCorrect,
            pointsChange = pointsChange,
            newReputation = newReputation,
            oldRank = oldRank,
            newRank = newRank
        )
    }

    private fun checkAnswer(case: Case, selectedLiarIds: List<Int>): Boolean {
        return when (case.type) {
            CaseType.NORMAL -> selectedLiarIds.singleOrNull() in case.coupableIds
            CaseType.AUCUN_MENTEUR, CaseType.PERSONNE_MENT -> selectedLiarIds.isEmpty()
            CaseType.TOUS_MENTENT -> selectedLiarIds.sorted() == case.suspects.map { it.id }.sorted()
            CaseType.DEUX_MENTEURS -> selectedLiarIds.sorted() == case.coupableIds.sorted()
        }
    }

    private fun calculatePoints(difficulty: Int, isCorrect: Boolean): Int {
        return if (isCorrect) {
            when (difficulty) {
                1 -> 2
                2 -> 4
                3 -> 6
                4 -> 8
                5 -> 10
                else -> 4
            }
        } else {
            when (difficulty) {
                1 -> -4
                2 -> -6
                3 -> -8
                4 -> -10
                5 -> -10
                else -> -6
            }
        }
    }

    fun isThemeUnlocked(theme: CaseTheme, profile: PlayerProfile): Boolean {
        if (theme == CaseTheme.ECOLE) return true
        val previousTheme = CaseTheme.entries.getOrNull(theme.ordinal - 1) ?: return false
        val previousCompleted = profile.themeProgress[previousTheme.ordinal] ?: 0
        return previousCompleted >= theme.casesRequiredToUnlock &&
                profile.reputation >= theme.reputationRequiredToUnlock
    }

    suspend fun advanceToNextCase(profile: PlayerProfile): PlayerProfile {
        val themes = CaseTheme.entries
        val currentTheme = themes[profile.currentThemeIndex]
        val nextCaseIndex = profile.currentCaseIndex + 1

        val updatedProfile = if (nextCaseIndex >= 10) {
            val nextThemeIndex = profile.currentThemeIndex + 1
            if (nextThemeIndex < themes.size) {
                profile.copy(currentThemeIndex = nextThemeIndex, currentCaseIndex = 0)
            } else {
                profile.copy(currentCaseIndex = nextCaseIndex)
            }
        } else {
            profile.copy(currentCaseIndex = nextCaseIndex)
        }

        prefs.updateProfile(updatedProfile)
        return updatedProfile
    }

    suspend fun resetAll() {
        prefs.resetProfile()
    }
}
