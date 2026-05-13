package com.theverdict.app.domain.model

enum class Rank(
    val displayName: String,
    val minReputation: Int,
    val maxReputation: Int,
    val emoji: String
) {
    OBSERVATEUR("Observateur",  0,  9,  "👁"),
    ENQUETEUR("Enquêteur",     10, 19,  "🔎"),
    INSPECTEUR("Inspecteur",   20, 29,  "🕵️"),
    DETECTIVE("Détective",     30, 44,  "🧩"),
    COMMISSAIRE("Commissaire", 45, 59,  "⚖️"),
    PROFILEUR("Profileur",     60, 69,  "🧠"),
    MENTALISTE("Mentaliste",   70, 79,  "🌀"),
    ORACLE("Oracle",           80, 89,  "🔮"),
    INFILTRE("Infiltré",       90, 98,  "🕶️"),
    LEGENDE("Légende Absolue", 99, 100, "👑");

    companion object {
        fun fromReputation(reputation: Int): Rank {
            return entries.lastOrNull { reputation >= it.minReputation } ?: OBSERVATEUR
        }
    }
}
