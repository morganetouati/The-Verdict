package com.theverdict.app.domain.model

data class PlayerProfile(
    val pseudo: String = "",
    val costumeIndex: Int = 0,
    val reputation: Int = 0,
    val casesPlayed: Int = 0,
    val correctVerdicts: Int = 0,
    val wrongVerdicts: Int = 0,
    val currentThemeIndex: Int = 0,
    val currentCaseIndex: Int = 0,
    val themeProgress: Map<Int, Int> = emptyMap(),
    val completedCaseIds: Set<Int> = emptySet()
) {
    val rank: Rank get() = Rank.fromReputation(reputation)

    val successRate: Int
        get() = if (casesPlayed > 0) (correctVerdicts * 100) / casesPlayed else 0

    val allCasesCompleted: Boolean
        get() = completedCaseIds.size >= 80

    val displayName: String
        get() = pseudo.ifBlank { "Juge Anonyme" }
}
