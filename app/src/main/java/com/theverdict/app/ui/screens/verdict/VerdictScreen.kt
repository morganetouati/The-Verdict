package com.theverdict.app.ui.screens.verdict

import android.app.Activity
import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Replay
import androidx.compose.material.icons.filled.School
import androidx.compose.material.icons.filled.Lightbulb
import androidx.compose.material.icons.filled.VideoLibrary
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.theverdict.app.data.ads.AdManager
import com.theverdict.app.domain.model.TagAccuracy
import com.theverdict.app.ui.components.ScoreCounter
import com.theverdict.app.ui.components.VerdictStamp
import com.theverdict.app.ui.components.XpCounter
import com.theverdict.app.ui.theme.*

@Composable
fun VerdictScreen(
    viewModel: VerdictViewModel,
    adManager: AdManager,
    onGoHome: () -> Unit,
    onGoToLesson: () -> Unit
) {
    val state by viewModel.uiState.collectAsState()
    val context = LocalContext.current

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(NoirDeep)
            .verticalScroll(rememberScrollState())
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(32.dp))

        when (state.phase) {
            VerdictPhase.CHOOSE_VERDICT -> {
                // -- Choose: Mensonge or Vérité --
                Text(
                    text = "VOTRE VERDICT",
                    style = MaterialTheme.typography.displayMedium,
                    color = Gold,
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(8.dp))

                state.challenge?.let {
                    Text(
                        text = it.title,
                        style = MaterialTheme.typography.titleMedium,
                        color = TextSecondary,
                        textAlign = TextAlign.Center
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Stats summary before verdict
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = "${state.evaluatedTags.size}",
                            style = MaterialTheme.typography.headlineMedium,
                            color = Gold,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "marqueurs",
                            style = MaterialTheme.typography.labelSmall,
                            color = TextSecondary
                        )
                    }
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = "${state.credibility}%",
                            style = MaterialTheme.typography.headlineMedium,
                            color = when {
                                state.credibility > 60 -> Gold
                                state.credibility > 30 -> Color(0xFFE67E22)
                                else -> RedLie
                            },
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "crédibilité",
                            style = MaterialTheme.typography.labelSmall,
                            color = TextSecondary
                        )
                    }
                }

                Spacer(modifier = Modifier.height(48.dp))

                Text(
                    text = "Cette personne ment-elle ?",
                    style = MaterialTheme.typography.headlineSmall,
                    color = TextPrimary,
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(24.dp))

                // Mensonge button
                Button(
                    onClick = { viewModel.submitVerdict(isLie = true) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = RedLie,
                        contentColor = TextPrimary
                    )
                ) {
                    Text(
                        text = "🔴  MENSONGE",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Vérité button
                Button(
                    onClick = { viewModel.submitVerdict(isLie = false) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = GreenTruth,
                        contentColor = NoirDeep
                    )
                ) {
                    Text(
                        text = "🟢  VÉRITÉ",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            VerdictPhase.REVEAL -> {
                // -- Reveal: Stamp + Score --
                VerdictStamp(
                    isLie = state.challenge?.isLie ?: false,
                    isCorrect = state.isCorrectVerdict
                )

                Spacer(modifier = Modifier.height(16.dp))

                // -- Verdict explanation: WHY it's a lie or truth --
                state.challenge?.let { challenge ->
                    if (challenge.verdictExplanation.isNotBlank()) {
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .border(
                                    width = 2.dp,
                                    color = if (state.isCorrectVerdict) GreenTruth else RedLie,
                                    shape = RoundedCornerShape(12.dp)
                                ),
                            shape = RoundedCornerShape(12.dp),
                            colors = CardDefaults.cardColors(containerColor = Surface)
                        ) {
                            Column(modifier = Modifier.padding(16.dp)) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Lightbulb,
                                        contentDescription = "Explication",
                                        tint = Gold,
                                        modifier = Modifier.size(20.dp)
                                    )
                                    Text(
                                        text = if (state.isCorrectVerdict) "Bien vu !" else "Explication",
                                        style = MaterialTheme.typography.titleSmall,
                                        color = if (state.isCorrectVerdict) GreenTruth else Gold,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                                Spacer(modifier = Modifier.height(8.dp))
                                Text(
                                    text = if (state.isCorrectVerdict)
                                        "Votre instinct était bon. ${challenge.verdictExplanation}"
                                    else
                                        "Vous vous êtes trompé. ${challenge.verdictExplanation}",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = TextPrimary,
                                    lineHeight = MaterialTheme.typography.bodySmall.lineHeight
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Score
                ScoreCounter(
                    targetScore = state.intuitionScore,
                    label = "Score d'intuition"
                )

                Spacer(modifier = Modifier.height(16.dp))

                // XP
                XpCounter(
                    xp = state.xpEarned,
                    doubled = state.xpDoubled
                )

                // Daily case multiplier badge
                if (state.dailyMultiplier > 1) {
                    Spacer(modifier = Modifier.height(8.dp))
                    val mulColor = if (state.dailyMultiplier >= 3) FlameRed else FlameOrange
                    Card(
                        modifier = Modifier
                            .border(1.dp, mulColor, RoundedCornerShape(8.dp)),
                        shape = RoundedCornerShape(8.dp),
                        colors = CardDefaults.cardColors(containerColor = mulColor.copy(alpha = 0.15f))
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Lightbulb,
                                contentDescription = null,
                                tint = mulColor,
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "×${state.dailyMultiplier} Cas du Jour",
                                style = MaterialTheme.typography.labelLarge,
                                fontWeight = FontWeight.Bold,
                                color = mulColor
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Credibility & stats
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = Surface)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(
                            text = "Bilan de l'analyse",
                            style = MaterialTheme.typography.titleSmall,
                            color = Gold
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceEvenly
                        ) {
                            StatItem(
                                value = "${state.totalPoints}",
                                label = "Points",
                                color = Gold
                            )
                            StatItem(
                                value = "${state.credibility}%",
                                label = "Crédibilité",
                                color = when {
                                    state.credibility > 60 -> GreenTruth
                                    state.credibility > 30 -> Color(0xFFE67E22)
                                    else -> RedLie
                                }
                            )
                            StatItem(
                                value = "${state.uselessClicks}",
                                label = "Clics inutiles",
                                color = if (state.uselessClicks == 0) GreenTruth else RedLie
                            )
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        // Credibility bar
                        LinearProgressIndicator(
                            progress = { state.credibility / 100f },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(8.dp)
                                .clip(RoundedCornerShape(4.dp)),
                            color = when {
                                state.credibility > 60 -> GreenTruth
                                state.credibility > 30 -> Color(0xFFE67E22)
                                else -> RedLie
                            },
                            trackColor = NoirDeep
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Evaluated tags with accuracy
                if (state.evaluatedTags.isNotEmpty()) {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        colors = CardDefaults.cardColors(containerColor = Surface)
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Text(
                                text = "Vos détections",
                                style = MaterialTheme.typography.titleSmall,
                                color = Gold
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            state.evaluatedTags.forEach { tag ->
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(vertical = 4.dp),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = "${tag.type.emoji} ${tag.type.labelRes}",
                                        style = MaterialTheme.typography.bodySmall,
                                        color = TextPrimary
                                    )
                                    Row(
                                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        // Points badge
                                        if (tag.pointsEarned > 0) {
                                            Text(
                                                text = "+${tag.pointsEarned}",
                                                style = MaterialTheme.typography.labelSmall,
                                                color = Gold,
                                                fontWeight = FontWeight.Bold
                                            )
                                        }
                                        // Accuracy badge
                                        Text(
                                            text = when (tag.accuracy) {
                                                TagAccuracy.PERFECT -> "🟢 Parfait"
                                                TagAccuracy.ANTICIPATION -> "🟠 Anticipation"
                                                TagAccuracy.USELESS -> "⚫ Inutile"
                                                TagAccuracy.MISSED -> "🔴 Manqué"
                                                null -> when (tag.isCorrect) {
                                                    true -> "✓ Juste"
                                                    false -> "✗ Faux"
                                                    null -> "—"
                                                }
                                            },
                                            style = MaterialTheme.typography.bodySmall,
                                            color = when (tag.accuracy) {
                                                TagAccuracy.PERFECT -> GreenTruth
                                                TagAccuracy.ANTICIPATION -> Color(0xFFE67E22)
                                                TagAccuracy.USELESS -> TextSecondary
                                                TagAccuracy.MISSED -> RedLie
                                                null -> when (tag.isCorrect) {
                                                    true -> GreenTruth
                                                    false -> RedLie
                                                    null -> TextSecondary
                                                }
                                            }
                                        )
                                    }
                                }
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(32.dp))

                // Double XP ad button
                if (!state.xpDoubled && state.isDoubleXpAdReady) {
                    OutlinedButton(
                        onClick = {
                            (context as? Activity)?.let { activity ->
                                adManager.showDoubleXpAd(
                                    activity = activity,
                                    onRewarded = { viewModel.doubleXp() },
                                    onDismissed = { }
                                )
                            }
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(48.dp),
                        shape = RoundedCornerShape(12.dp),
                        border = ButtonDefaults.outlinedButtonBorder(enabled = true),
                        colors = ButtonDefaults.outlinedButtonColors(
                            contentColor = Gold
                        )
                    ) {
                        Text(
                            text = "🎬  Doubler les XP (pub)",
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.Medium
                        )
                    }

                    Spacer(modifier = Modifier.height(16.dp))
                }

                // Action buttons
                // Replay button (only if there are truth tags to review)
                if (state.replaySegments.isNotEmpty()) {
                    Button(
                        onClick = { viewModel.goToReplay() },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(48.dp),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Gold,
                            contentColor = NoirDeep
                        )
                    ) {
                        Icon(Icons.Default.Replay, contentDescription = "Analyse détaillée", modifier = Modifier.size(20.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Analyse détaillée", fontWeight = FontWeight.Bold)
                    }

                    Spacer(modifier = Modifier.height(12.dp))
                }

                Button(
                    onClick = onGoToLesson,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (state.replaySegments.isEmpty()) Gold else Surface,
                        contentColor = if (state.replaySegments.isEmpty()) NoirDeep else TextPrimary
                    )
                ) {
                    Icon(Icons.Default.School, contentDescription = "Voir la leçon", modifier = Modifier.size(20.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Voir la leçon", fontWeight = FontWeight.Bold)
                }

                Spacer(modifier = Modifier.height(12.dp))

                OutlinedButton(
                    onClick = onGoHome,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = TextPrimary)
                ) {
                    Icon(Icons.Default.VideoLibrary, contentDescription = "Nouvelle analyse", modifier = Modifier.size(20.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Nouvelle analyse")
                }
            }

            VerdictPhase.REPLAY -> {
                // -- Pedagogical Replay --
                Text(
                    text = "ANALYSE DÉTAILLÉE",
                    style = MaterialTheme.typography.displaySmall,
                    color = Gold,
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = "Chaque indice de la vidéo, dans l'ordre chronologique",
                    style = MaterialTheme.typography.bodySmall,
                    color = TextSecondary,
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(24.dp))

                // Show each replay segment as a card
                state.replaySegments.forEachIndexed { index, segment ->
                    ReplaySegmentCard(
                        index = index + 1,
                        segment = segment
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                }

                // Show useless clicks summary if any
                if (state.uselessClicks > 0) {
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .border(
                                width = 1.dp,
                                color = TextSecondary.copy(alpha = 0.3f),
                                shape = RoundedCornerShape(12.dp)
                            ),
                        shape = RoundedCornerShape(12.dp),
                        colors = CardDefaults.cardColors(containerColor = Surface)
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Text(
                                text = "⚫ Clics hors cible: ${state.uselessClicks}",
                                style = MaterialTheme.typography.titleSmall,
                                color = TextSecondary,
                                fontWeight = FontWeight.Bold
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "Pénalité: -${state.uselessClicks * 10} points · -${state.uselessClicks * 15} crédibilité",
                                style = MaterialTheme.typography.bodySmall,
                                color = RedLie
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "Conseil: Observez attentivement avant de cliquer. Chaque clic aléatoire réduit votre score.",
                                style = MaterialTheme.typography.bodySmall,
                                color = TextSecondary
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))
                }

                Spacer(modifier = Modifier.height(24.dp))

                // Continue to lesson
                Button(
                    onClick = onGoToLesson,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Gold,
                        contentColor = NoirDeep
                    )
                ) {
                    Icon(Icons.Default.School, contentDescription = "Voir la leçon", modifier = Modifier.size(20.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Voir la leçon", fontWeight = FontWeight.Bold)
                }

                Spacer(modifier = Modifier.height(12.dp))

                OutlinedButton(
                    onClick = onGoHome,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = TextPrimary)
                ) {
                    Icon(Icons.Default.VideoLibrary, contentDescription = "Nouvelle analyse", modifier = Modifier.size(20.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Nouvelle analyse")
                }
            }

            VerdictPhase.LESSON -> {
                LaunchedEffect(Unit) {
                    onGoToLesson()
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))
    }
}

@Composable
private fun StatItem(value: String, label: String, color: Color) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            text = value,
            style = MaterialTheme.typography.titleLarge,
            color = color,
            fontWeight = FontWeight.Bold
        )
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall,
            color = TextSecondary
        )
    }
}

@Composable
private fun ReplaySegmentCard(index: Int, segment: ReplaySegment) {
    val borderColor = when (segment.accuracy) {
        TagAccuracy.PERFECT -> GreenTruth
        TagAccuracy.ANTICIPATION -> Color(0xFFE67E22)
        TagAccuracy.MISSED -> RedLie
        TagAccuracy.USELESS -> TextSecondary
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .border(
                width = 2.dp,
                color = borderColor,
                shape = RoundedCornerShape(12.dp)
            ),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Surface)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            // Header: accuracy badge + type
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = segment.accuracy.emoji,
                        style = MaterialTheme.typography.titleMedium
                    )
                    Column {
                        Text(
                            text = "${segment.truthTag.type.emoji} ${segment.truthTag.type.labelRes}",
                            style = MaterialTheme.typography.titleSmall,
                            color = TextPrimary,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "à ${formatTimestamp(segment.truthTag.timestampMs)}",
                            style = MaterialTheme.typography.labelSmall,
                            color = TextSecondary
                        )
                    }
                }

                // Points or status
                Column(horizontalAlignment = Alignment.End) {
                    Text(
                        text = segment.accuracy.label,
                        style = MaterialTheme.typography.labelSmall,
                        color = borderColor,
                        fontWeight = FontWeight.Bold
                    )
                    if (segment.pointsEarned > 0) {
                        Text(
                            text = "+${segment.pointsEarned} pts",
                            style = MaterialTheme.typography.labelSmall,
                            color = Gold,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Timing info
            if (segment.playerTag != null) {
                val delta = segment.playerTag.timestampMs - segment.truthTag.timestampMs
                val deltaText = when {
                    delta > 0 -> "+${delta}ms après"
                    delta < 0 -> "${-delta}ms avant"
                    else -> "pile au moment"
                }
                Text(
                    text = "Votre clic: ${formatTimestamp(segment.playerTag.timestampMs)} ($deltaText)",
                    style = MaterialTheme.typography.bodySmall,
                    color = TextSecondary
                )
                Spacer(modifier = Modifier.height(4.dp))
            } else {
                Text(
                    text = "❌ Vous n'avez pas détecté cet indice",
                    style = MaterialTheme.typography.bodySmall,
                    color = RedLie
                )
                Spacer(modifier = Modifier.height(4.dp))
            }

            // Explanation
            if (segment.explanation.isNotBlank()) {
                HorizontalDivider(
                    color = borderColor.copy(alpha = 0.3f),
                    modifier = Modifier.padding(vertical = 4.dp)
                )
                Text(
                    text = "💡 ${segment.explanation}",
                    style = MaterialTheme.typography.bodySmall,
                    color = TextPrimary
                )
            }
        }
    }
}

private fun formatTimestamp(ms: Long): String {
    val totalSeconds = ms / 1000
    val minutes = totalSeconds / 60
    val seconds = totalSeconds % 60
    return "${minutes}:${seconds.toString().padStart(2, '0')}"
}
