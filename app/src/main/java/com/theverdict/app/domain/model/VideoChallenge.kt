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
