package com.theverdict.app.ui.components

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.theverdict.app.ui.theme.*
import kotlinx.coroutines.delay

/**
 * Tampon "MENSONGE" ou "VÉRITÉ" — animation de slam
 * (scale de 3x→1x + rotation + fade in)
 */
@Composable
fun VerdictStamp(
    isLie: Boolean,
    isCorrect: Boolean,
    modifier: Modifier = Modifier
) {
    var started by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        delay(200)
        started = true
    }

    val scale by animateFloatAsState(
        targetValue = if (started) 1f else 3f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessMedium
        ),
        label = "stampScale"
    )

    val alpha by animateFloatAsState(
        targetValue = if (started) 1f else 0f,
        animationSpec = tween(300),
        label = "stampAlpha"
    )

    val rotation by animateFloatAsState(
        targetValue = if (started) -8f else -30f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessMedium
        ),
        label = "stampRotation"
    )

    val stampColor = if (isLie) RedLie else GreenTruth
    val stampText = if (isLie) "MENSONGE" else "VÉRITÉ"

    Box(
        modifier = modifier
            .scale(scale)
            .alpha(alpha)
            .rotate(rotation),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .border(4.dp, stampColor, RoundedCornerShape(8.dp))
                .padding(horizontal = 32.dp, vertical = 16.dp)
        ) {
            Text(
                text = stampText,
                fontFamily = playfairDisplay,
                fontWeight = FontWeight.Bold,
                fontSize = 36.sp,
                color = stampColor,
                textAlign = TextAlign.Center,
                letterSpacing = 4.sp
            )
            if (isCorrect) {
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "✓ Bon verdict",
                    style = MaterialTheme.typography.bodySmall,
                    color = GreenTruth,
                    textAlign = TextAlign.Center
                )
            } else {
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "✗ Mauvais verdict",
                    style = MaterialTheme.typography.bodySmall,
                    color = RedLie,
                    textAlign = TextAlign.Center
                )
            }
        }
    }
}
