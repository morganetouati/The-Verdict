package com.theverdict.app.domain.model

/**
 * A video challenge that the player must analyze.
 * Uses public domain archive footage for an "Archives Secrètes / Cold Case" aesthetic.
 */
data class VideoChallenge(
    val id: String,
    val title: String,
    val videoUrl: String,
    val thumbnailUrl: String = "",
    val durationMs: Long,
    val isLie: Boolean,
    val difficulty: Difficulty = Difficulty.EASY,
    val truthTags: List<TruthTag> = emptyList(),
    val hints: List<String> = emptyList(), // Behavioural observation hints (💡) for daily case modes
    val storyPrompt: String = "", // What the person was asked to talk about
    val archiveContext: String = "", // Narrative context describing the archive footage
    val sourceAttribution: String = "", // Public domain source (e.g. "Internet Archive", "Prelinger Archives")
    val sourceUrl: String = "", // URL of the original public domain source for attribution
    val verdictExplanation: String = "" // Why the person is lying or telling the truth — shown after verdict
)

enum class Difficulty(val label: String, val xpMultiplier: Float) {
    EASY("Facile", 1.0f),
    MEDIUM("Moyen", 1.5f),
    HARD("Difficile", 2.0f)
}

/**
 * Difficulty mode chosen by the player for a Daily Case.
 * Controls video duration cap, number of hints, and score multiplier.
 */
enum class DailyCaseMode(
    val label: String,
    val maxDurationMs: Long,
    val hintCount: Int,
    val scoreMultiplier: Int,
    val description: String
) {
    EASY("Facile", 60_000L, 5, 1, "60 s · 5 indices · ×1"),
    MEDIUM("Moyen", 20_000L, 3, 2, "20 s · 3 indices · ×2"),
    HARD("Difficile", 15_000L, 0, 3, "15 s · sans indices · ×3")
}
