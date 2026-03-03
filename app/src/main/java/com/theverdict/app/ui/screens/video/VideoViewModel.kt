package com.theverdict.app.ui.screens.video

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import com.theverdict.app.domain.model.*
import com.theverdict.app.domain.model.DailyCaseMode
import com.theverdict.app.domain.repository.VideoRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class VideoUiState(
    val challenge: VideoChallenge? = null,
    val playerTags: List<DetectionTag> = emptyList(),
    val currentPositionMs: Long = 0,
    val isPlaying: Boolean = false,
    val isVideoEnded: Boolean = false,
    val isLoading: Boolean = true,
    val credibility: Int = 100,
    val errorMessage: String? = null,
    val isBuffering: Boolean = false,
    // ── Daily Case hints ─────────────────────
    val dailyMode: DailyCaseMode? = null,
    val hintsAvailable: List<String> = emptyList(),
    val hintsRevealed: Set<Int> = emptySet()
)

class VideoViewModel(
    private val videoRepo: VideoRepository,
    private val appContext: Context? = null,
    private val loadDailyCase: Boolean = false,
    private val dailyMode: DailyCaseMode? = null
) : ViewModel() {

    private val _uiState = MutableStateFlow(VideoUiState())
    val uiState: StateFlow<VideoUiState> = _uiState.asStateFlow()

    // Cached evaluation result (avoids redundant 3x computation)
    private var cachedEvaluatedTags: List<DetectionTag>? = null

    init {
        loadChallenge()
    }

    private fun loadChallenge() {
        viewModelScope.launch {
            try {
                // I9: Check network before streaming
                if (appContext != null && !isNetworkAvailable(appContext)) {
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        errorMessage = "Pas de connexion internet. Connectez-vous pour analyser une vidéo."
                    )
                    return@launch
                }
                val challenge = if (loadDailyCase) videoRepo.getDailyCaseChallenge()
                                else videoRepo.getRandomChallenge()
                val hints = if (dailyMode != null) challenge.hints.take(dailyMode.hintCount) else emptyList()
                _uiState.value = VideoUiState(
                    challenge = challenge,
                    isLoading = false,
                    isPlaying = false,
                    dailyMode = dailyMode,
                    hintsAvailable = hints
                )
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    errorMessage = "Impossible de charger l'affaire. Réessayez."
                )
            }
        }
    }

    private fun isNetworkAvailable(context: Context): Boolean {
        val cm = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val network = cm.activeNetwork ?: return false
        val caps = cm.getNetworkCapabilities(network) ?: return false
        return caps.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
    }

    fun addDetectionTag(type: MicroExpressionType) {
        val currentPos = _uiState.value.currentPositionMs
        if (_uiState.value.isVideoEnded) return

        // Invalidate cache when tags change
        cachedEvaluatedTags = null

        val tag = DetectionTag(
            type = type,
            timestampMs = currentPos,
            isCorrect = null
        )
        _uiState.value = _uiState.value.copy(
            playerTags = _uiState.value.playerTags + tag
        )
    }

    fun updatePosition(positionMs: Long) {
        _uiState.value = _uiState.value.copy(currentPositionMs = positionMs)
        // Duration cap for daily case modes
        val mode = _uiState.value.dailyMode
        if (mode != null && positionMs >= mode.maxDurationMs && !_uiState.value.isVideoEnded) {
            onVideoEnded()
        }
    }

    fun revealHint(index: Int) {
        val current = _uiState.value
        if (index in current.hintsAvailable.indices && index !in current.hintsRevealed) {
            _uiState.value = current.copy(hintsRevealed = current.hintsRevealed + index)
        }
    }

    fun getDailyMultiplier(): Int = dailyMode?.scoreMultiplier ?: 1

    fun onVideoEnded() {
        _uiState.value = _uiState.value.copy(
            isVideoEnded = true,
            isPlaying = false
        )
    }

    fun setPlaying(playing: Boolean) {
        _uiState.value = _uiState.value.copy(isPlaying = playing)
    }

    fun onPlayerError(message: String) {
        _uiState.value = _uiState.value.copy(
            errorMessage = message,
            isPlaying = false,
            isLoading = false
        )
    }

    fun setBuffering(buffering: Boolean) {
        _uiState.value = _uiState.value.copy(isBuffering = buffering)
    }

    fun retryLoad() {
        cachedEvaluatedTags = null
        _uiState.value = VideoUiState()
        loadChallenge()
    }

    /**
     * Evaluate player tags against truth tags using tolerance windows.
     *
     * Green zone:  truthTag + 200ms  → truthTag + 2000ms  → 100 pts (PERFECT)
     * Orange zone: truthTag - 1500ms → truthTag + 200ms   → 50 pts (ANTICIPATION)
     * Red zone:    outside all windows                     → 0 pts, -15 credibility (USELESS)
     *
     * Each truth tag can only be matched once (best match wins).
     */
    fun evaluateTags(): List<DetectionTag> {
        // Return cached result if available (R4: avoid 3x redundant computation)
        cachedEvaluatedTags?.let { return it }

        val challenge = _uiState.value.challenge ?: return emptyList()
        val truthTags = challenge.truthTags
        var credibility = 100

        // For each truth tag, track which player tag best matched it
        val truthTagMatched = mutableMapOf<Int, Int>() // truthIndex -> playerIndex
        val playerTagResults = mutableMapOf<Int, DetectionTag>() // playerIndex -> evaluated tag

        // First pass: find best matches for each player tag
        data class Match(
            val playerIndex: Int,
            val truthIndex: Int,
            val accuracy: TagAccuracy,
            val delta: Long // absolute time difference
        )

        val allMatches = mutableListOf<Match>()

        _uiState.value.playerTags.forEachIndexed { pIdx, playerTag ->
            truthTags.forEachIndexed { tIdx, truthTag ->
                if (truthTag.type == playerTag.type) {
                    val delta = playerTag.timestampMs - truthTag.timestampMs

                    val accuracy = when {
                        // Green zone: 200ms to 2000ms after truth tag
                        delta in 200..2000 -> TagAccuracy.PERFECT
                        // Orange zone: 1500ms before to 200ms after truth tag
                        delta in -1500..199 -> TagAccuracy.ANTICIPATION
                        else -> null
                    }

                    if (accuracy != null) {
                        allMatches.add(
                            Match(pIdx, tIdx, accuracy, kotlin.math.abs(delta))
                        )
                    }
                }
            }
        }

        // Sort: prefer PERFECT over ANTICIPATION, then by closest delta
        allMatches.sortWith(compareBy<Match> {
            when (it.accuracy) {
                TagAccuracy.PERFECT -> 0
                TagAccuracy.ANTICIPATION -> 1
                else -> 2
            }
        }.thenBy { it.delta })

        // Greedy assignment: each truth tag matched at most once, each player tag matched at most once
        val usedTruthIndices = mutableSetOf<Int>()
        val usedPlayerIndices = mutableSetOf<Int>()

        for (match in allMatches) {
            if (match.truthIndex in usedTruthIndices || match.playerIndex in usedPlayerIndices) continue

            val playerTag = _uiState.value.playerTags[match.playerIndex]
            val truthTag = truthTags[match.truthIndex]

            playerTagResults[match.playerIndex] = playerTag.copy(
                isCorrect = true,
                accuracy = match.accuracy,
                pointsEarned = match.accuracy.points,
                credibilityLost = 0,
                matchedTruthTag = truthTag
            )

            usedTruthIndices.add(match.truthIndex)
            usedPlayerIndices.add(match.playerIndex)
        }

        // Unmatched player tags are USELESS (red zone)
        _uiState.value.playerTags.forEachIndexed { pIdx, playerTag ->
            if (pIdx !in usedPlayerIndices) {
                credibility = (credibility - 15).coerceAtLeast(0)
                playerTagResults[pIdx] = playerTag.copy(
                    isCorrect = false,
                    accuracy = TagAccuracy.USELESS,
                    pointsEarned = 0,
                    credibilityLost = 15
                )
            }
        }

        // Update credibility in state
        _uiState.value = _uiState.value.copy(credibility = credibility)

        // Cache and return evaluated tags in original order
        val result = _uiState.value.playerTags.indices.map { idx ->
            playerTagResults[idx] ?: _uiState.value.playerTags[idx]
        }
        cachedEvaluatedTags = result
        return result
    }

    /**
     * Calculate final score using anti-spam formula:
     * Score = (totalPoints × difficultyMultiplier) - (uselessClicks × 10)
     *
     * Returns the score clamped to 0+
     */
    fun calculateScore(): Int {
        val challenge = _uiState.value.challenge ?: return 0
        val evaluatedTags = evaluateTags()

        val totalPoints = evaluatedTags.sumOf { it.pointsEarned }
        val uselessClicks = evaluatedTags.count { it.accuracy == TagAccuracy.USELESS }
        val difficultyMultiplier = challenge.difficulty.xpMultiplier

        val score = ((totalPoints * difficultyMultiplier) - (uselessClicks * 10)).toInt()
        return score.coerceAtLeast(0)
    }

    /**
     * Get the number of useless clicks (for passing to verdict).
     */
    fun getUselessClicks(): Int {
        val evaluatedTags = evaluateTags()
        return evaluatedTags.count { it.accuracy == TagAccuracy.USELESS }
    }

    /**
     * Get the current credibility level.
     */
    fun getCredibility(): Int = _uiState.value.credibility

    class Factory(
        private val videoRepo: VideoRepository,
        private val appContext: Context? = null,
        private val mode: DailyCaseMode? = null
    ) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return VideoViewModel(videoRepo, appContext, loadDailyCase = false, dailyMode = mode) as T
        }
    }

    class DailyFactory(
        private val videoRepo: VideoRepository,
        private val appContext: Context? = null,
        private val dailyMode: DailyCaseMode = DailyCaseMode.EASY
    ) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return VideoViewModel(videoRepo, appContext, loadDailyCase = true, dailyMode = dailyMode) as T
        }
    }
}
