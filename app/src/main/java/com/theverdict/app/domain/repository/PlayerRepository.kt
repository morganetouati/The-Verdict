package com.theverdict.app.domain.repository

import com.theverdict.app.domain.model.PlayerProfile

interface PlayerRepository {
    suspend fun getProfile(): PlayerProfile
    suspend fun updateProfile(profile: PlayerProfile)
    suspend fun addXp(amount: Int)
    suspend fun applyCredibilityPenalty(uselessClicks: Int)
    suspend fun isCredibilityLocked(): Boolean
    suspend fun getCredibilityLockRemainingMs(): Long
    suspend fun getPersistentCredibility(): Int
    suspend fun getCredibilityTickets(): Int
    suspend fun useCredibilityTicket(): Boolean
    suspend fun incrementCredibilityTickets()
}
