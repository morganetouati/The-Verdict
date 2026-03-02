package com.theverdict.app.ui.screens.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.theverdict.app.domain.model.PlayerLevel
import com.theverdict.app.domain.model.PlayerProfile
import com.theverdict.app.domain.repository.GameRepository
import com.theverdict.app.domain.repository.PlayerRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class HomeUiState(
    val profile: PlayerProfile = PlayerProfile(
        displayName = "Joueur",
        totalXp = 0,
        level = PlayerLevel.NOVICE,
        gamesPlayed = 0,
        correctVerdicts = 0,
        currentStreak = 0,
        bestStreak = 0
    ),
    val remainingPlays: Int = 5,
    val canPlay: Boolean = true,
    val isLoading: Boolean = true
)

class HomeViewModel(
    private val playerRepo: PlayerRepository,
    private val gameRepo: GameRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(HomeUiState())
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()

    init {
        loadData()
    }

    fun loadData() {
        viewModelScope.launch {
            try {
                val profile = playerRepo.getProfile()
                val canPlay = gameRepo.canPlay()
                val dailyPlays = gameRepo.getDailyPlaysCount()
                val remaining = (10 - dailyPlays).coerceAtLeast(0)

                _uiState.value = HomeUiState(
                    profile = profile,
                    remainingPlays = remaining,
                    canPlay = canPlay,
                    isLoading = false
                )
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(isLoading = false)
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

    class Factory(
        private val playerRepo: PlayerRepository,
        private val gameRepo: GameRepository
    ) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return HomeViewModel(playerRepo, gameRepo) as T
        }
    }
}
