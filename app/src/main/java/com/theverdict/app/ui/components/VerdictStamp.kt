package com.theverdict.app.ui.components

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.draw.scale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
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

    LaunchedEffect(Unit) {
        alpha.animateTo(1f, tween(200))
        scale.animateTo(1f, spring(dampingRatio = Spring.DampingRatioMediumBouncy, stiffness = Spring.StiffnessMedium))
        rotation.animateTo(if (isCorrect) -8f else 8f, spring(dampingRatio = Spring.DampingRatioMediumBouncy))
    }

    val text = if (isCorrect) "BON\nVERDICT" else "MAUVAIS\nVERDICT"
    val color = if (isCorrect) VerdictCorrect else VerdictWrong

    Box(
        modifier = modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = text,
            modifier = Modifier
                .scale(scale.value)
                .rotate(rotation.value)
                .alpha(alpha.value),
            style = MaterialTheme.typography.displayLarge.copy(
                fontSize = 42.sp,
                fontWeight = FontWeight.Black,
                letterSpacing = 4.sp
            ),
            color = color,
            textAlign = TextAlign.Center
        )
    }
}
