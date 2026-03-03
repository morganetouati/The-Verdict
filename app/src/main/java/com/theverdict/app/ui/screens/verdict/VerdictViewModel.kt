package com.theverdict.app.ui.screens.verdict

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.theverdict.app.data.ads.AdManager
import com.theverdict.app.data.local.PreferencesManager
import com.theverdict.app.domain.model.*
import com.theverdict.app.domain.repository.GameRepository
import com.theverdict.app.domain.repository.PlayerRepository
import com.theverdict.app.domain.repository.VideoRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

/**
 * Represents one segment in the pedagogical replay.
 */
data class ReplaySegment(
    val truthTag: TruthTag,
    val playerTag: DetectionTag?,       // null if the player missed this clue entirely
    val accuracy: TagAccuracy,          // PERFECT, ANTICIPATION, MISSED
    val pointsEarned: Int,
    val explanation: String             // Pedagogical explanation from TruthTag
)

data class VerdictUiState(
    val challenge: VideoChallenge? = null,
    val evaluatedTags: List<DetectionTag> = emptyList(),
    val playerVerdict: Boolean? = null,
    val isCorrectVerdict: Boolean = false,
    val intuitionScore: Int = 0,
    val xpEarned: Int = 0,
    val xpDoubled: Boolean = false,
    val phase: VerdictPhase = VerdictPhase.CHOOSE_VERDICT,
    val isDoubleXpAdReady: Boolean = false,
    val credibility: Int = 100,
    val totalPoints: Int = 0,
    val uselessClicks: Int = 0,
    val replaySegments: List<ReplaySegment> = emptyList(),
    val dailyMultiplier: Int = 1
)

enum class VerdictPhase {
    CHOOSE_VERDICT,
    REVEAL,
    REPLAY,     // Pedagogical replay: show each clue with explanation
    LESSON
}

class VerdictViewModel(
    private val videoId: String,
    private val playerTags: List<DetectionTag>,
    private val intuitionScore: Int,
    private val credibility: Int,
    private val uselessClicks: Int,
    private val isDailyCase: Boolean = false,
    private val dailyMultiplier: Int = 1,
    private val videoRepo: VideoRepository,
    private val playerRepo: PlayerRepository,
    private val gameRepo: GameRepository,
    private val adManager: AdManager,
    private val prefs: PreferencesManager? = null
) : ViewModel() {

    private val _uiState = MutableStateFlow(VerdictUiState())
    val uiState: StateFlow<VerdictUiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            val challenge = videoRepo.getChallengeById(videoId)
            val totalPoints = playerTags.sumOf { it.pointsEarned }

            // Build replay segments from truth tags
            val segments = buildReplaySegments(challenge, playerTags)

            _uiState.value = _uiState.value.copy(
                challenge = challenge,
                evaluatedTags = playerTags,
                intuitionScore = intuitionScore,
                credibility = credibility,
                uselessClicks = uselessClicks,
                totalPoints = totalPoints,
                replaySegments = segments,
                dailyMultiplier = dailyMultiplier
            )
        }

        viewModelScope.launch {
            adManager.isDoubleXpAdReady.collect { ready ->
                _uiState.value = _uiState.value.copy(isDoubleXpAdReady = ready)
            }
        }
    }

    /**
     * Build replay segments by matching truth tags to evaluated player tags.
     * Shows what the player found, missed, or anticipated.
     */
    private fun buildReplaySegments(
        challenge: VideoChallenge?,
        evaluatedTags: List<DetectionTag>
    ): List<ReplaySegment> {
        if (challenge == null) return emptyList()

        return challenge.truthTags.map { truthTag ->
            // Find the player tag that was matched to this truth tag
            val matchedPlayerTag = evaluatedTags.find { playerTag ->
                playerTag.matchedTruthTag?.type == truthTag.type &&
                        playerTag.matchedTruthTag?.timestampMs == truthTag.timestampMs
            }

            if (matchedPlayerTag != null) {
                ReplaySegment(
                    truthTag = truthTag,
                    playerTag = matchedPlayerTag,
                    accuracy = matchedPlayerTag.accuracy ?: TagAccuracy.MISSED,
                    pointsEarned = matchedPlayerTag.pointsEarned,
                    explanation = truthTag.explanation
                )
            } else {
                // Player missed this truth tag entirely
                ReplaySegment(
                    truthTag = truthTag,
                    playerTag = null,
                    accuracy = TagAccuracy.MISSED,
                    pointsEarned = 0,
                    explanation = truthTag.explanation
                )
            }
        }.sortedBy { it.truthTag.timestampMs }
    }

    fun submitVerdict(isLie: Boolean) {
        val challenge = _uiState.value.challenge ?: return
        val isCorrect = (isLie == challenge.isLie)

        // New XP formula: (totalPoints × difficultyMultiplier) - (uselessClicks × 10) + verdictBonus
        val difficultyMultiplier = challenge.difficulty.xpMultiplier
        val verdictBonus = if (isCorrect) 50 else 0
        val totalPoints = _uiState.value.totalPoints
        val useless = _uiState.value.uselessClicks

        val xpEarned = (
                (totalPoints * difficultyMultiplier * dailyMultiplier).toInt() - (useless * 10) + verdictBonus
                ).coerceAtLeast(0)

        _uiState.value = _uiState.value.copy(
            playerVerdict = isLie,
            isCorrectVerdict = isCorrect,
            xpEarned = xpEarned,
            phase = VerdictPhase.REVEAL
        )

        // Save result
        viewModelScope.launch {
            val result = GameResult(
                videoId = videoId,
                playerTags = playerTags,
                playerVerdict = isLie,
                isCorrectVerdict = isCorrect,
                intuitionScore = _uiState.value.intuitionScore,
                xpEarned = xpEarned,
                xpDoubled = false,
                evaluatedTags = playerTags,
                credibility = _uiState.value.credibility,
                totalPoints = totalPoints,
                uselessClicks = useless
            )
            gameRepo.saveResult(result)

            // Mark daily case as played
            if (isDailyCase) prefs?.setDailyCasePlayed()

            // Apply persistent credibility penalty (>= 3 useless clicks)
            if (useless >= 3) playerRepo.applyCredibilityPenalty(useless)
        }
    }

    fun doubleXp() {
        val current = _uiState.value
        _uiState.value = current.copy(
            xpEarned = current.xpEarned * 2,
            xpDoubled = true
        )

        viewModelScope.launch {
            playerRepo.addXp(current.xpEarned)
        }
    }

    fun goToReplay() {
        _uiState.value = _uiState.value.copy(phase = VerdictPhase.REPLAY)
    }

    fun goToLesson() {
        _uiState.value = _uiState.value.copy(phase = VerdictPhase.LESSON)
    }

    class Factory(
        private val videoId: String,
        private val playerTags: List<DetectionTag>,
        private val intuitionScore: Int,
        private val credibility: Int,
        private val uselessClicks: Int,
        private val isDailyCase: Boolean = false,
        private val dailyMultiplier: Int = 1,
        private val videoRepo: VideoRepository,
        private val playerRepo: PlayerRepository,
        private val gameRepo: GameRepository,
        private val adManager: AdManager,
        private val prefs: PreferencesManager? = null
    ) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return VerdictViewModel(
                videoId, playerTags, intuitionScore, credibility, uselessClicks,
                isDailyCase, dailyMultiplier, videoRepo, playerRepo, gameRepo, adManager, prefs
            ) as T
        }
    }
}
