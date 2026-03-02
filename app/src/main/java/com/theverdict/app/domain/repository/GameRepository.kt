package com.theverdict.app.domain.repository

import com.theverdict.app.domain.model.GameResult

interface GameRepository {
    suspend fun saveResult(result: GameResult)
    suspend fun getResults(): List<GameResult>
    suspend fun getDailyPlaysCount(): Int
    suspend fun incrementDailyPlays()
    suspend fun canPlay(): Boolean
    suspend fun addBonusPlays(count: Int)
}
