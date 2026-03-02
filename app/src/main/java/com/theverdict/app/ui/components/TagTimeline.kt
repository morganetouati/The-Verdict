package com.theverdict.app.ui.components

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.theverdict.app.domain.model.DetectionTag
import com.theverdict.app.ui.theme.*

/**
 * Timeline de la vidéo avec marqueurs de détection.
 * Chaque tag est visualisé comme un point sur la barre.
 */
@Composable
fun TagTimeline(
    currentPositionMs: Long,
    durationMs: Long,
    tags: List<DetectionTag>,
    onSeek: (Long) -> Unit,
    modifier: Modifier = Modifier
) {
    if (durationMs <= 0) return

    val progress = (currentPositionMs.toFloat() / durationMs).coerceIn(0f, 1f)

    Column(modifier = modifier.fillMaxWidth()) {
        // Tag markers overlay on canvas
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(40.dp)
        ) {
            // Draw tag markers
            Canvas(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(40.dp)
                    .padding(horizontal = 16.dp)
            ) {
                val trackY = size.height / 2

                // Track background
                drawLine(
                    color = SurfaceLight,
                    start = Offset(0f, trackY),
                    end = Offset(size.width, trackY),
                    strokeWidth = 4.dp.toPx()
                )

                // Progress
                drawLine(
                    color = Gold,
                    start = Offset(0f, trackY),
                    end = Offset(size.width * progress, trackY),
                    strokeWidth = 4.dp.toPx()
                )

                // Tag markers
                tags.forEach { tag ->
                    val tagPos = (tag.timestampMs.toFloat() / durationMs).coerceIn(0f, 1f)
                    val markerColor = when (tag.isCorrect) {
                        true -> GreenTruth
                        false -> RedLie
                        null -> Gold // Not yet evaluated
                    }
                    drawCircle(
                        color = markerColor,
                        radius = 6.dp.toPx(),
                        center = Offset(size.width * tagPos, trackY)
                    )
                    // Inner dot
                    drawCircle(
                        color = NoirDeep,
                        radius = 2.dp.toPx(),
                        center = Offset(size.width * tagPos, trackY)
                    )
                }

                // Playhead
                drawCircle(
                    color = Gold,
                    radius = 8.dp.toPx(),
                    center = Offset(size.width * progress, trackY)
                )
            }
        }

        // Time labels
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = formatTime(currentPositionMs),
                style = MaterialTheme.typography.labelSmall,
                color = TextSecondary
            )
            Text(
                text = formatTime(durationMs),
                style = MaterialTheme.typography.labelSmall,
                color = TextSecondary
            )
        }
    }
}

private fun formatTime(ms: Long): String {
    val totalSec = ms / 1000
    val min = totalSec / 60
    val sec = totalSec % 60
    return "%d:%02d".format(min, sec)
}
