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
    YEUX(
        label = "Yeux",
        emoji = "👁️",
        relatedClues = listOf(Clue.REGARDE_AILLEURS, Clue.EVITE_REGARD),
        left = 0.15f, top = 0.27f, right = 0.85f, bottom = 0.38f
    ),
    JOUES(
        label = "Joues",
        emoji = "💧",
        relatedClues = listOf(Clue.TRANSPIRE, Clue.NERVEUX),
        left = 0.10f, top = 0.38f, right = 0.90f, bottom = 0.46f
    ),
    BOUCHE(
        label = "Bouche",
        emoji = "👄",
        relatedClues = listOf(
            Clue.SOURIT_TROP, Clue.VOIX_CHANGE, Clue.PARLE_VITE, Clue.HESITE,
            Clue.MAINS_TREMBLENT, Clue.BRAS_CROISES, Clue.TROP_CALME, Clue.CONFIANT,
            Clue.SE_CONTREDIT, Clue.REPOND_VITE, Clue.DETAIL_SUSPECT, Clue.HISTOIRE_FLOUE
        ),
        left = 0.25f, top = 0.44f, right = 0.75f, bottom = 0.53f
    )
}
