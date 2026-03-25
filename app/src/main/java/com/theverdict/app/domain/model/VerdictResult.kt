package com.theverdict.app.domain.model

data class VerdictResult(
    val isCorrect: Boolean,
    val pointsChange: Int,
    val newReputation: Int,
    val oldRank: Rank,
    val newRank: Rank,
    val wasPromoted: Boolean = newRank.ordinal > oldRank.ordinal,
    val wasDemoted: Boolean = newRank.ordinal < oldRank.ordinal,
    val isGameOver: Boolean = newReputation <= 0
)
