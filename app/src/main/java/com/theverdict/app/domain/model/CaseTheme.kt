package com.theverdict.app.domain.model

import kotlinx.serialization.Serializable

@Serializable
enum class CaseTheme(
    val displayName: String,
    val emoji: String,
    val difficulty: Int,
    val casesRequiredToUnlock: Int,
    val reputationRequiredToUnlock: Int,
    val hasTimer: Boolean,
    val maxInterrogationsPerSuspect: Int
) {
    ECOLE(
        displayName = "École",
        emoji = "🎓",
        difficulty = 1,
        casesRequiredToUnlock = 0,
        reputationRequiredToUnlock = 0,
        hasTimer = false,
        maxInterrogationsPerSuspect = Int.MAX_VALUE
    ),
    TRAVAIL(
        displayName = "Travail",
        emoji = "🏢",
        difficulty = 2,
        casesRequiredToUnlock = 7,
        reputationRequiredToUnlock = 20,
        hasTimer = false,
        maxInterrogationsPerSuspect = Int.MAX_VALUE
    ),
    FAMILLE(
        displayName = "Famille",
        emoji = "👨‍👩‍👧",
        difficulty = 2,
        casesRequiredToUnlock = 7,
        reputationRequiredToUnlock = 30,
        hasTimer = false,
        maxInterrogationsPerSuspect = Int.MAX_VALUE
    ),
    POLICE(
        displayName = "Police",
        emoji = "🚓",
        difficulty = 3,
        casesRequiredToUnlock = 7,
        reputationRequiredToUnlock = 40,
        hasTimer = true,
        maxInterrogationsPerSuspect = 2
    ),
    TRIBUNAL(
        displayName = "Tribunal",
        emoji = "⚖️",
        difficulty = 3,
        casesRequiredToUnlock = 7,
        reputationRequiredToUnlock = 50,
        hasTimer = true,
        maxInterrogationsPerSuspect = 2
    ),
    ENQUETE(
        displayName = "Enquête",
        emoji = "🕵️",
        difficulty = 4,
        casesRequiredToUnlock = 7,
        reputationRequiredToUnlock = 60,
        hasTimer = true,
        maxInterrogationsPerSuspect = 1
    ),
    ESPIONNAGE(
        displayName = "Espionnage",
        emoji = "🕶️",
        difficulty = 4,
        casesRequiredToUnlock = 7,
        reputationRequiredToUnlock = 70,
        hasTimer = true,
        maxInterrogationsPerSuspect = 1
    ),
    ELITE(
        displayName = "Élite",
        emoji = "👑",
        difficulty = 5,
        casesRequiredToUnlock = 7,
        reputationRequiredToUnlock = 80,
        hasTimer = true,
        maxInterrogationsPerSuspect = 1
    );

    val hasSpecialVerdicts: Boolean
        get() = this == ENQUETE || this == ESPIONNAGE || this == ELITE
}
