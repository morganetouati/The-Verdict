package com.theverdict.app.ui.screens.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.theverdict.app.data.local.PreferencesManager
import com.theverdict.app.domain.model.MentalistRank
import com.theverdict.app.domain.model.PlayerProfile
import com.theverdict.app.domain.model.VideoChallenge
import com.theverdict.app.domain.repository.GameRepository
import com.theverdict.app.domain.repository.PlayerRepository
import com.theverdict.app.domain.repository.VideoRepository
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class HomeUiState(
    val profile: PlayerProfile = PlayerProfile(
        displayName = "Joueur",
        totalXp = 0,
        rank = MentalistRank.OBSERVATEUR_DISTRAIT,
        gamesPlayed = 0,
        correctVerdicts = 0,
        currentStreak = 0,
        bestStreak = 0
    ),
    val remainingPlays: Int = 5,
    val canPlay: Boolean = true,
    val isLoading: Boolean = true,

    // ── Daily Case ──────────────────────────────────────────────────
    val dailyChallenge: VideoChallenge? = null,
    val isDailyCasePlayed: Boolean = false,
    val dailyCaseSecondsLeft: Long = 0L,

    // ── Lucidity Flame (streak) ──────────────────────────────────────
    val streakBroken: Boolean = false,          // True only once per session if streak was lost
    val streakBrokenValue: Int = 0,             // The streak value before it broke

    // ── Persistent Credibility ───────────────────────────────────────
    val persistentCredibility: Int = 100,
    val isCredibilityLocked: Boolean = false,
    val credibilityLockSecondsLeft: Long = 0L,
    val credibilityTickets: Int = 0
)

class HomeViewModel(
    private val playerRepo: PlayerRepository,
    private val gameRepo: GameRepository,
    private val videoRepo: VideoRepository,
    private val prefs: PreferencesManager
) : ViewModel() {

    private val _uiState = MutableStateFlow(HomeUiState())
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()

    private var lockCountdownJob: Job? = null
    private var dailyCountdownJob: Job? = null

    init {
        loadData()
    }

    fun loadData() {
        viewModelScope.launch {
            try {
                // Trigger regen before reading credibility
                prefs.regenerateCredibility()

                val profile = playerRepo.getProfile()
                val canPlay = gameRepo.canPlay()
                val dailyPlays = gameRepo.getDailyPlaysCount()
                val remaining = (10 - dailyPlays).coerceAtLeast(0)

                // Daily case
                val dailyChallenge = videoRepo.getDailyCaseChallenge()
                val isDailyCasePlayed = prefs.isDailyCaseDoneToday()

                // Credibility
                val credibility = prefs.getPersistentCredibility()
                val isLocked = prefs.isCredibilityLocked()
                val lockRemainingMs = prefs.getCredibilityLockRemainingMs()
                val tickets = prefs.getCredibilityTickets()

                // Streak broken detection (check if last play was before yesterday)
                var streakBroken = false
                var streakBrokenValue = 0
                val lastPlayDate = prefs.getLastPlayDate()
                if (lastPlayDate.isNotBlank() && profile.currentStreak == 0 && lastPlayDate != java.time.LocalDate.now().format(java.time.format.DateTimeFormatter.ISO_LOCAL_DATE)) {
                    // Streak was recently broken — show dialog once
                    streakBroken = true
                    streakBrokenValue = profile.bestStreak
                }

                _uiState.value = HomeUiState(
                    profile = profile,
                    remainingPlays = remaining,
                    canPlay = canPlay,
                    isLoading = false,
                    dailyChallenge = dailyChallenge,
                    isDailyCasePlayed = isDailyCasePlayed,
                    persistentCredibility = credibility,
                    isCredibilityLocked = isLocked,
                    credibilityLockSecondsLeft = lockRemainingMs / 1000L,
                    credibilityTickets = tickets,
                    streakBroken = streakBroken,
                    streakBrokenValue = streakBrokenValue
                )

                // Start countdowns if needed
                if (isLocked) startLockCountdown()
                startDailyCountdown()

            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(isLoading = false)
            }
        }
    }

    fun dismissStreakBrokenDialog() {
        _uiState.value = _uiState.value.copy(streakBroken = false)
    }

    fun useCredibilityTicket() {
        viewModelScope.launch {
            val success = playerRepo.useCredibilityTicket()
            if (success) {
                _uiState.value = _uiState.value.copy(
                    isCredibilityLocked = false,
                    credibilityLockSecondsLeft = 0L,
                    persistentCredibility = 50,
                    credibilityTickets = (_uiState.value.credibilityTickets - 1).coerceAtLeast(0)
                )
                lockCountdownJob?.cancel()
            }
        }
    }

    fun onCredibilityTicketEarned() {
        viewModelScope.launch {
            playerRepo.incrementCredibilityTickets()
            _uiState.value = _uiState.value.copy(
                credibilityTickets = _uiState.value.credibilityTickets + 1
            )
        }
    }

    private fun startLockCountdown() {
        lockCountdownJob?.cancel()
        lockCountdownJob = viewModelScope.launch {
            while (true) {
                val remainingMs = prefs.getCredibilityLockRemainingMs()
                if (remainingMs <= 0L) {
                    _uiState.value = _uiState.value.copy(
                        isCredibilityLocked = false,
                        credibilityLockSecondsLeft = 0L,
                        persistentCredibility = prefs.getPersistentCredibility()
                    )
                    break
                }
                _uiState.value = _uiState.value.copy(credibilityLockSecondsLeft = remainingMs / 1000L)
                delay(1000L)
            }
        }
    }

    private fun startDailyCountdown() {
        dailyCountdownJob?.cancel()
        dailyCountdownJob = viewModelScope.launch {
            while (true) {
                val now = java.time.LocalTime.now()
                val secondsLeft = ((23 - now.hour) * 3600L
                        + (59 - now.minute) * 60L
                        + (59 - now.second))
                _uiState.value = _uiState.value.copy(dailyCaseSecondsLeft = secondsLeft)
                delay(1000L)
            }
        }
    }

    fun updateDisplayName(name: String) {
        val trimmed = name.trim().take(20)
        if (trimmed.isBlank()) return
        val updatedProfile = _uiState.value.profile.copy(displayName = trimmed)
        _uiState.value = _uiState.value.copy(profile = updatedProfile)
        viewModelScope.launch {
            playerRepo.updateProfile(updatedProfile)
        }
    }

    fun updateAvatar(emoji: String) {
        val updatedProfile = _uiState.value.profile.copy(avatarUrl = emoji)
        _uiState.value = _uiState.value.copy(profile = updatedProfile)
        viewModelScope.launch {
            playerRepo.updateProfile(updatedProfile)
        }
    }

    override fun onCleared() {
        super.onCleared()
        lockCountdownJob?.cancel()
        dailyCountdownJob?.cancel()
    }

    class Factory(
        private val playerRepo: PlayerRepository,
        private val gameRepo: GameRepository,
        private val videoRepo: VideoRepository,
        private val prefs: PreferencesManager
    ) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return HomeViewModel(playerRepo, gameRepo, videoRepo, prefs) as T
        }
    }
}
