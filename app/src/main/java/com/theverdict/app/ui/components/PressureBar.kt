package com.theverdict.app.ui.components

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
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
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.theverdict.app.ui.theme.*

@Composable
fun PressureBar(
    pressure: Int,
    maxPressure: Int = 100,
    modifier: Modifier = Modifier
) {
    val fraction by animateFloatAsState(
        targetValue = pressure.toFloat() / maxPressure,
        animationSpec = tween(400),
        label = "pressure"
    )

    val isCritical = pressure >= 80
    val isWarning = pressure in 50..79

    val barColor = when {
        isCritical -> PressureCritical
        isWarning  -> PressureWarning
        else       -> PressureNormal
    }
    val barColorLight = when {
        isCritical -> PressureCriticalLight
        isWarning  -> PressureWarningLight
        else       -> PressureNormalLight
    }

    val label = when {
        isCritical -> "⚠️ Interrogatoire compromis !"
        isWarning  -> "🧠 Pression élevée"
        else       -> "🧠 Pression"
    }

    // Pulse animation in critical state
    val inf = rememberInfiniteTransition(label = "pressurePulse")
    val pulse by inf.animateFloat(
        initialValue = 1f,
        targetValue = if (isCritical) 1.04f else 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(500, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulse"
    )

    Column(
        modifier = modifier
            .semantics { contentDescription = "Pression : $pressure sur $maxPressure" }
            .graphicsLayer { scaleX = pulse; scaleY = pulse }
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = label,
                style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.SemiBold),
                color = barColor
            )
            Spacer(Modifier.weight(1f))
            Text(
                text = "$pressure / $maxPressure",
                style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Bold),
                color = barColor
            )
        }
        Spacer(Modifier.height(4.dp))
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(8.dp)
                .clip(RoundedCornerShape(4.dp))
                .drawBehind {
                    drawRoundRect(
                        color = barColor.copy(alpha = 0.15f),
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
                    .background(Brush.horizontalGradient(listOf(barColor, barColorLight, barColor)))
            )
        }

        // Critical warning banner
        if (isCritical) {
            Spacer(Modifier.height(6.dp))
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(8.dp))
                    .background(PressureCritical.copy(alpha = 0.15f))
                    .padding(horizontal = 12.dp, vertical = 6.dp)
            ) {
                Text(
                    text = "Perte de réputation · Indice perdu · Risque d'erreur",
                    style = MaterialTheme.typography.labelSmall,
                    color = PressureCriticalLight
                )
            }
        }
    }
}
