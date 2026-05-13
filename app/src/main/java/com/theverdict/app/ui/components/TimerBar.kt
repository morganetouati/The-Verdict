package com.theverdict.app.ui.components

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.contentDescription
import com.theverdict.app.ui.theme.*

@Composable
fun TimerBar(
    remainingSeconds: Int,
    totalSeconds: Int = 90,
    modifier: Modifier = Modifier
) {
    val fraction by animateFloatAsState(
        targetValue = remainingSeconds.toFloat() / totalSeconds,
        animationSpec = tween(300),
        label = "timer"
    )

    val color = when {
        remainingSeconds <= 15 -> TimerCritical
        remainingSeconds <= 30 -> TimerWarning
        else -> TimerNormal
    }

    val colorLight = when {
        remainingSeconds <= 15 -> TimerCriticalLight
        remainingSeconds <= 30 -> TimerWarningLight
        else -> TimerNormalLight
    }

    Column(modifier = modifier.semantics { contentDescription = "Temps restant : ${remainingSeconds} secondes" }) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "⏱ Temps restant",
                style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.SemiBold),
                color = TextGray
            )
            Spacer(Modifier.weight(1f))
            Text(
                text = "${remainingSeconds}s",
                style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Bold),
                color = color
            )
        }
        Spacer(Modifier.height(4.dp))
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(8.dp)
                .clip(RoundedCornerShape(4.dp))
                .drawBehind {
                    // Subtle glow behind the bar
                    drawRoundRect(
                        color = color.copy(alpha = 0.15f),
                        size = size.copy(height = size.height + 4.dp.toPx()),
                        cornerRadius = androidx.compose.ui.geometry.CornerRadius(4.dp.toPx())
                    )
                }
                .background(DarkSurfaceVariant)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth(fraction)
                    .height(8.dp)
                    .clip(RoundedCornerShape(4.dp))
                    .background(Brush.horizontalGradient(listOf(color, colorLight, color)))
            )
        }
    }
}
