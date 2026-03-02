package com.theverdict.app.domain.model

/**
 * Result of a completed game session.
 */
data class GameResult(
    val videoId: String,
    val playerTags: List<DetectionTag>,
    val playerVerdict: Boolean, // true = player thinks it's a lie
    val isCorrectVerdict: Boolean,
    val intuitionScore: Int, // 0-100
    val xpEarned: Int,
    val xpDoubled: Boolean = false,
    val evaluatedTags: List<DetectionTag> = emptyList(), // tags with accuracy filled
    val timestampMs: Long = System.currentTimeMillis(),
    val credibility: Int = 100,    // Remaining credibility (0-100)
    val totalPoints: Int = 0,      // Sum of points from all detections
    val uselessClicks: Int = 0     // Number of clicks outside any window
)
