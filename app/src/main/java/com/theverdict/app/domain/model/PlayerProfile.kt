package com.theverdict.app.domain.model

import kotlin.math.sqrt

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
    val rank: MentalistRank = MentalistRank.OBSERVATEUR_DISTRAIT
) {
    /** Numeric level computed from XP. Level 10 ≈ 810 XP, Level 50 ≈ 24010 XP. */
    val levelNumber: Int get() = MentalistRank.levelFromXp(totalXp)
}

/**
 * 15-tier prestige rank system.
 * Each rank has a title, a (mildly mocking or epic) subtitle, an emoji badge,
 * a Long ARGB color, and a minimum XP threshold.
 */
enum class MentalistRank(
    val title: String,
    val subtitle: String,
    val badge: String,
    val rankColorArgb: Long,
    val minXp: Int
) {
    OBSERVATEUR_DISTRAIT(
        title = "Observateur Distrait",
        subtitle = "Tu regardes mais tu ne vois rien.",
        badge = "😴",
        rankColorArgb = 0xFF9E9E9EL,
        minXp = 0
    ),
    CURIEUX_CONFUS(
        title = "Curieux Confus",
        subtitle = "Tu confonds les suspects avec les témoins.",
        badge = "🤔",
        rankColorArgb = 0xFF78909CL,
        minXp = 200
    ),
    PROFILEUR_STAGIAIRE(
        title = "Profileur Stagiaire",
        subtitle = "Contrat de 3 mois. Renouvelable. Peut-être.",
        badge = "📋",
        rankColorArgb = 0xFF8D6E63L,
        minXp = 810
    ),
    PROFILEUR_CONFIRME(
        title = "Profileur Confirmé",
        subtitle = "Les dossiers s'ouvrent. Enfin.",
        badge = "🗂️",
        rankColorArgb = 0xFFCD7F32L,
        minXp = 2250
    ),
    ENQUETEUR_PERSPICACE(
        title = "Enquêteur Perspicace",
        subtitle = "Tes intuitions commencent à avoir du sens.",
        badge = "🔍",
        rankColorArgb = 0xFFAAAAAFL,
        minXp = 4000
    ),
    ANALYSTE_COMPORTEMENTAL(
        title = "Analyste Comportemental",
        subtitle = "Tu lis les corps comme un livre ouvert.",
        badge = "📖",
        rankColorArgb = 0xFFC0C0C0L,
        minXp = 6000
    ),
    DETECTIVE_PRIVE(
        title = "Détective Privé",
        subtitle = "Les pros viennent te demander des conseils.",
        badge = "🕵️",
        rankColorArgb = 0xFFDAA520L,
        minXp = 8500
    ),
    EXPERT_EN_DECEPTION(
        title = "Expert en Déception",
        subtitle = "Tu vois le mensonge avant qu'il soit dit.",
        badge = "🎭",
        rankColorArgb = 0xFFD4A847L,
        minXp = 11500
    ),
    PSYCHOLOGUE_TERRAIN(
        title = "Psychologue de Terrain",
        subtitle = "Science et instinct ne font plus qu'un.",
        badge = "🧪",
        rankColorArgb = 0xFFD4A847L,
        minXp = 15000
    ),
    MAITRE_PROFILEUR(
        title = "Maître Profileur",
        subtitle = "Le FBI te connaît. De réputation.",
        badge = "🏅",
        rankColorArgb = 0xFFFFD700L,
        minXp = 19500
    ),
    DETECTEUR_DE_MENSONGES(
        title = "Détecteur de Mensonges Humain",
        subtitle = "Tu n'as plus besoin de machine.",
        badge = "⚡",
        rankColorArgb = 0xFFFFD700L,
        minXp = 24010
    ),
    MENTALISTE_ELITE(
        title = "Mentaliste d'Élite",
        subtitle = "Rare. Craint. Respecté.",
        badge = "🌟",
        rankColorArgb = 0xFFE5E4E2L,
        minXp = 32000
    ),
    CHASSEUR_DE_VERITE(
        title = "Chasseur de Vérité",
        subtitle = "La vérité ne peut pas te fuir.",
        badge = "🔮",
        rankColorArgb = 0xFFE8D5F5L,
        minXp = 43000
    ),
    ORACLE_DES_AMES(
        title = "Oracle des Âmes",
        subtitle = "Tu perces le mystère de l'âme humaine.",
        badge = "👁️",
        rankColorArgb = 0xFF9B59B6L,
        minXp = 57000
    ),
    MENTALISTE_SUPREME(
        title = "Mentaliste Suprême",
        subtitle = "INTOUCHABLE. LÉGENDAIRE.",
        badge = "👑",
        rankColorArgb = 0xFFFFD700L,
        minXp = 75000
    );

    val isMaxRank: Boolean get() = this == MENTALISTE_SUPREME

    companion object {
        fun fromXp(xp: Int): MentalistRank =
            entries.reversed().first { xp >= it.minXp }

        /** Niveau numérique 1-100. Niveau 10 ≈ 810 XP, Niveau 50 ≈ 24 010 XP. */
        fun levelFromXp(xp: Int): Int =
            minOf(100, (sqrt(xp.toDouble() / 10.0) + 1).toInt())
    }
}

// Keep PlayerLevel as a typealias for backward compat during refactor
@Deprecated("Use MentalistRank instead")
typealias PlayerLevel = MentalistRank

