package com.theverdict.app.ui.screens.daily

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Lightbulb
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.theverdict.app.domain.model.DailyCaseMode
import com.theverdict.app.ui.theme.*

@Composable
fun DailyCaseDifficultyScreen(
    screenTitle: String = "CAS DU JOUR",
    challengeTitle: String,
    onModeSelected: (DailyCaseMode) -> Unit,
    onBack: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(NoirDeep)
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(48.dp))

        Icon(
            imageVector = Icons.Default.Star,
            contentDescription = null,
            tint = Gold,
            modifier = Modifier.size(40.dp)
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = screenTitle,
            style = MaterialTheme.typography.displaySmall,
            color = Gold,
            textAlign = TextAlign.Center
        )
        Spacer(modifier = Modifier.height(4.dp))
        if (challengeTitle.isNotBlank()) {
            Text(
                text = challengeTitle,
                style = MaterialTheme.typography.titleMedium,
                color = TextSecondary,
                textAlign = TextAlign.Center
            )
        }

        Spacer(modifier = Modifier.height(12.dp))

        Text(
            text = "Choisis ton niveau de difficulté",
            style = MaterialTheme.typography.bodyMedium,
            color = TextPrimary,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(32.dp))

        // ── EASY ─────────────────────────────────────────────
        DifficultyCard(
            mode = DailyCaseMode.EASY,
            borderColor = GreenTruth,
            multiplierColor = GreenTruth,
            onClick = { onModeSelected(DailyCaseMode.EASY) }
        )

        Spacer(modifier = Modifier.height(16.dp))

        // ── MEDIUM ───────────────────────────────────────────
        DifficultyCard(
            mode = DailyCaseMode.MEDIUM,
            borderColor = FlameOrange,
            multiplierColor = FlameOrange,
            onClick = { onModeSelected(DailyCaseMode.MEDIUM) }
        )

        Spacer(modifier = Modifier.height(16.dp))

        // ── HARD ─────────────────────────────────────────────
        DifficultyCard(
            mode = DailyCaseMode.HARD,
            borderColor = FlameRed,
            multiplierColor = FlameRed,
            onClick = { onModeSelected(DailyCaseMode.HARD) }
        )

        Spacer(modifier = Modifier.weight(1f))

        // Back button
        TextButton(onClick = onBack) {
            Text("Retour", color = TextSecondary)
        }

        Spacer(modifier = Modifier.height(16.dp))
    }
}

@Composable
private fun DifficultyCard(
    mode: DailyCaseMode,
    borderColor: androidx.compose.ui.graphics.Color,
    multiplierColor: androidx.compose.ui.graphics.Color,
    onClick: () -> Unit
) {
    val durationText = if (mode.maxDurationMs == Long.MAX_VALUE) "Vidéo complète" else "${mode.maxDurationMs / 1000} s"

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .border(
                width = 1.5.dp,
                color = borderColor.copy(alpha = 0.6f),
                shape = RoundedCornerShape(16.dp)
            )
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Surface)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Left: mode label + description
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = mode.label.uppercase(),
                    style = MaterialTheme.typography.titleMedium,
                    color = borderColor,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(6.dp))

                // Duration row
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        Icons.Default.Timer,
                        contentDescription = null,
                        tint = TextSecondary,
                        modifier = Modifier.size(14.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = durationText,
                        style = MaterialTheme.typography.bodySmall,
                        color = TextSecondary
                    )
                }

                Spacer(modifier = Modifier.height(4.dp))

                // Hints row
                Row(verticalAlignment = Alignment.CenterVertically) {
                    if (mode.hintCount > 0) {
                        repeat(mode.hintCount.coerceAtMost(5)) {
                            Icon(
                                Icons.Default.Lightbulb,
                                contentDescription = null,
                                tint = Gold,
                                modifier = Modifier.size(16.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "${mode.hintCount} indices",
                            style = MaterialTheme.typography.bodySmall,
                            color = TextSecondary
                        )
                    } else {
                        Text(
                            text = "Sans indices",
                            style = MaterialTheme.typography.bodySmall,
                            color = TextSecondary
                        )
                    }
                }
            }

            // Right: multiplier badge
            Box(
                modifier = Modifier
                    .size(56.dp)
                    .background(
                        Brush.linearGradient(
                            listOf(
                                multiplierColor.copy(alpha = 0.2f),
                                multiplierColor.copy(alpha = 0.05f)
                            )
                        ),
                        RoundedCornerShape(12.dp)
                    )
                    .border(1.dp, multiplierColor.copy(alpha = 0.4f), RoundedCornerShape(12.dp)),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "×${mode.scoreMultiplier}",
                    style = MaterialTheme.typography.headlineSmall,
                    color = multiplierColor,
                    fontWeight = FontWeight.Black
                )
            }
        }
    }
}
