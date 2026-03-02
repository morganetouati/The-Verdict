package com.theverdict.app.domain.model

/**
 * Accuracy of a player's detection relative to a truth tag.
 */
enum class TagAccuracy(val points: Int, val label: String, val emoji: String) {
    PERFECT(100, "Parfait", "🟢"),        // Within Green zone (200ms–2000ms after truth)
    ANTICIPATION(50, "Anticipation", "🟠"), // Within Orange zone (1500ms before–200ms after truth)
    MISSED(0, "Manqué", "🔴"),            // Outside all windows (no match)
    USELESS(0, "Inutile", "⚫")            // No corresponding truth tag at all
}

/**
 * A tag placed by the player on the video timeline.
 */
data class DetectionTag(
    val type: MicroExpressionType,
    val timestampMs: Long,
    val isCorrect: Boolean? = null,       // Legacy: null = not yet evaluated
    val accuracy: TagAccuracy? = null,     // New: detailed accuracy level
    val pointsEarned: Int = 0,            // Points awarded for this tag
    val credibilityLost: Int = 0,         // Credibility lost (for bad clicks)
    val matchedTruthTag: TruthTag? = null // The truth tag this was matched to (for replay)
)

/**
 * Ground truth tag marked on a video (what's actually there).
 */
data class TruthTag(
    val type: MicroExpressionType,
    val timestampMs: Long,
    val explanation: String = ""
)
