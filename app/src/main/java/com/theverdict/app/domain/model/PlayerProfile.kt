package com.theverdict.app.domain.model

/**
 * Player profile and progression.
 */
data class PlayerProfile(
    val id: String = "",
    val displayName: String = "Joueur",
    val avatarUrl: String = "",
    val intuitionScore: Int = 0,
    val totalXp: Int = 0,
    val gamesPlayed: Int = 0,
    val correctVerdicts: Int = 0,
    val currentStreak: Int = 0,
    val bestStreak: Int = 0,
    val level: PlayerLevel = PlayerLevel.NOVICE
)

enum class PlayerLevel(
    val label: String,
    val emoji: String,
    val minXp: Int
) {
    NOVICE("Novice", "🔍", 0),
    DETECTIVE("Détective", "🕵️", 500),
    MENTALIST("Mentaliste", "🧠", 2000),
    ORACLE("Oracle", "👁️", 5000);

    companion object {
        fun fromXp(xp: Int): PlayerLevel {
            return entries.reversed().first { xp >= it.minXp }
        }
    }
}
