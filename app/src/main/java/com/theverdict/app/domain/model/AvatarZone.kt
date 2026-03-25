package com.theverdict.app.domain.model

enum class AvatarZone(
    val label: String,
    val emoji: String,
    val relatedClues: List<Clue>,
    // Relative bounds within avatar (fractions 0..1 of width/height)
    val left: Float,
    val top: Float,
    val right: Float,
    val bottom: Float
) {
    FRONT(
        label = "Front",
        emoji = "💧",
        relatedClues = listOf(Clue.TRANSPIRE),
        left = 0.25f, top = 0.08f, right = 0.75f, bottom = 0.25f
    ),
    YEUX(
        label = "Yeux",
        emoji = "👁️",
        relatedClues = listOf(Clue.REGARDE_AILLEURS, Clue.EVITE_REGARD),
        left = 0.18f, top = 0.25f, right = 0.82f, bottom = 0.38f
    ),
    SOURCILS(
        label = "Sourcils",
        emoji = "😰",
        relatedClues = listOf(Clue.NERVEUX),
        left = 0.15f, top = 0.18f, right = 0.85f, bottom = 0.27f
    ),
    BOUCHE(
        label = "Bouche",
        emoji = "👄",
        relatedClues = listOf(Clue.SOURIT_TROP, Clue.VOIX_CHANGE, Clue.PARLE_VITE, Clue.HESITE),
        left = 0.25f, top = 0.40f, right = 0.75f, bottom = 0.55f
    ),
    MAINS(
        label = "Mains",
        emoji = "🤲",
        relatedClues = listOf(Clue.MAINS_TREMBLENT),
        left = 0.0f, top = 0.72f, right = 0.30f, bottom = 0.95f
    ),
    BRAS(
        label = "Bras",
        emoji = "💪",
        relatedClues = listOf(Clue.BRAS_CROISES),
        left = 0.70f, top = 0.72f, right = 1.0f, bottom = 0.95f
    ),
    CORPS(
        label = "Corps",
        emoji = "🧍",
        relatedClues = listOf(Clue.TROP_CALME, Clue.CONFIANT),
        left = 0.25f, top = 0.58f, right = 0.75f, bottom = 0.78f
    ),
    ATTITUDE(
        label = "Attitude",
        emoji = "🔍",
        relatedClues = listOf(Clue.SE_CONTREDIT, Clue.REPOND_VITE, Clue.DETAIL_SUSPECT, Clue.HISTOIRE_FLOUE),
        left = 0f, top = 0f, right = 0f, bottom = 0f // Rendered as a button, not a zone overlay
    )
}
