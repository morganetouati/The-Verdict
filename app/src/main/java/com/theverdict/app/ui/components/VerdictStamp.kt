package com.theverdict.app.ui.components

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.draw.scale
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.theverdict.app.ui.theme.VerdictCorrect
import com.theverdict.app.ui.theme.VerdictWrong

@Composable
fun VerdictStamp(
    isCorrect: Boolean,
    modifier: Modifier = Modifier
) {
    val scale = remember { Animatable(3f) }
    val rotation = remember { Animatable(-15f) }
    val alpha = remember { Animatable(0f) }
    val flashAlpha = remember { Animatable(0f) }

    LaunchedEffect(Unit) {
        alpha.animateTo(1f, tween(200))
        scale.animateTo(1f, spring(dampingRatio = Spring.DampingRatioMediumBouncy, stiffness = Spring.StiffnessMedium))
        rotation.animateTo(if (isCorrect) -8f else 8f, spring(dampingRatio = Spring.DampingRatioMediumBouncy))
        // White flash on impact
        flashAlpha.animateTo(0.35f, tween(60))
        flashAlpha.animateTo(0f, tween(250))
    }

    val text = if (isCorrect) "BON\nVERDICT" else "MAUVAIS\nVERDICT"
    val color = if (isCorrect) VerdictCorrect else VerdictWrong

    Box(
        modifier = modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Box(
            modifier = Modifier
                .scale(scale.value)
                .rotate(rotation.value)
                .alpha(alpha.value)
                .drawBehind {
                    val strokeOuter = 4.dp.toPx()
                    val strokeInner = 2.dp.toPx()
                    val pad = 8.dp.toPx()
                    val cr = CornerRadius(12.dp.toPx())
                    // Outer border
                    drawRoundRect(
                        color = color,
                        topLeft = Offset(0f, 0f),
                        size = Size(size.width, size.height),
                        cornerRadius = cr,
                        style = Stroke(strokeOuter)
                    )
                    // Inner border
                    drawRoundRect(
                        color = color.copy(alpha = 0.6f),
                        topLeft = Offset(pad, pad),
                        size = Size(size.width - pad * 2, size.height - pad * 2),
                        cornerRadius = CornerRadius(8.dp.toPx()),
                        style = Stroke(strokeInner)
                    )
                }
                .padding(20.dp),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = text,
                style = MaterialTheme.typography.displayLarge.copy(
                    fontSize = 42.sp,
                    fontWeight = FontWeight.Black,
                    letterSpacing = 4.sp
                ),
                color = color,
                textAlign = TextAlign.Center
            )
        }
        // White flash overlay on impact
        if (flashAlpha.value > 0f) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .alpha(flashAlpha.value)
                    .background(Color.White)
            )
        }
    }
}
