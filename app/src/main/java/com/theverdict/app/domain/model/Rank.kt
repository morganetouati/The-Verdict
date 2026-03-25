package com.theverdict.app.domain.model

enum class Rank(
    val displayName: String,
    val minReputation: Int,
    val maxReputation: Int,
    val emoji: String
) {
    DEBUTANT("Débutant", 0, 20, "📖"),
    JUGE("Juge", 21, 40, "⚖️"),
    BON_JUGE("Bon Juge", 41, 60, "🏛️"),
    EXPERT("Expert", 61, 80, "🎯"),
    LEGENDE("Légende", 81, 100, "👑");

    companion object {
        fun fromReputation(reputation: Int): Rank {
            return entries.lastOrNull { reputation >= it.minReputation } ?: DEBUTANT
        }
    }
}
