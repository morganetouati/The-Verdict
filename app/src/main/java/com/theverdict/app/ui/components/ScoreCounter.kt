package com.theverdict.app.ui.components

import androidx.compose.animation.core.*
import androidx.compose.foundation.layout.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.theverdict.app.ui.theme.*
import kotlinx.coroutines.delay

/**
 * Compteur de score animé — s'incrémente de 0 au score final.
 */
@Composable
fun ScoreCounter(
    targetScore: Int,
    label: String,
    modifier: Modifier = Modifier,
    durationMs: Int = 1500,
    delayMs: Long = 0
) {
    var started by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        delay(delayMs)
        started = true
    }

    val animatedScore by animateIntAsState(
        targetValue = if (started) targetScore else 0,
        animationSpec = tween(durationMs, easing = FastOutSlowInEasing),
        label = "score"
    )

    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "$animatedScore",
            style = MaterialTheme.typography.displayLarge,
            color = Gold,
            textAlign = TextAlign.Center
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = label,
            style = MaterialTheme.typography.bodySmall,
            color = TextSecondary,
            textAlign = TextAlign.Center
        )
    }
}

/**
 * XP counter with "+" prefix for gains
 */
@Composable
fun XpCounter(
    xp: Int,
    doubled: Boolean = false,
    modifier: Modifier = Modifier
) {
    val displayXp = if (doubled) xp * 2 else xp
    var started by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        delay(500)
        started = true
    }

    val animatedXp by animateIntAsState(
        targetValue = if (started) displayXp else 0,
        animationSpec = tween(1200, easing = FastOutSlowInEasing),
        label = "xp"
    )

    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center
    ) {
        Text(
            text = "+${animatedXp} XP",
            style = MaterialTheme.typography.labelLarge,
            color = if (doubled) GreenTruth else Gold
        )
        if (doubled) {
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = "×2",
                style = MaterialTheme.typography.labelMedium,
                color = GreenTruth
            )
        }
    }
}
