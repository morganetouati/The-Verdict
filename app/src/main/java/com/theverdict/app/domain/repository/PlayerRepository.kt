package com.theverdict.app.domain.repository

import com.theverdict.app.domain.model.PlayerProfile

interface PlayerRepository {
    suspend fun getProfile(): PlayerProfile
    suspend fun updateProfile(profile: PlayerProfile)
    suspend fun addXp(amount: Int)
}
