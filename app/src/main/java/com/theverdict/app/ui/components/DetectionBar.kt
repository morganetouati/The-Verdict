package com.theverdict.app.ui.components

import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import android.os.VibratorManager
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import com.theverdict.app.domain.model.MicroExpressionType
import com.theverdict.app.ui.theme.*
import kotlinx.coroutines.delay

/**
 * Barre de détection — 5 boutons pour tagger des micro-expressions
 * pendant la lecture vidéo. Feedback haptique + cooldown 500ms par bouton
 * + cooldown global 300ms entre tout clic.
 * Tous les types : Lèvres pincées, Blocage oculaire, Auto-contact,
 * Micro-mépris, Incongruence tête-message.
 */
@Composable
fun DetectionBar(
    onDetection: (MicroExpressionType) -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true
) {
    val detectionButtons = MicroExpressionType.entries
    // R5 — Global cooldown across all 5 buttons (300ms)
    var globalCooldown by remember { mutableStateOf(false) }

    LaunchedEffect(globalCooldown) {
        if (globalCooldown) {
            delay(300)
            globalCooldown = false
        }
    }

    Row(
        modifier = modifier
            .fillMaxWidth()
            .background(OverlayDark, RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp))
            .padding(horizontal = 8.dp, vertical = 10.dp),
        horizontalArrangement = Arrangement.SpaceEvenly,
        verticalAlignment = Alignment.CenterVertically
    ) {
        detectionButtons.forEach { type ->
            DetectionButton(
                type = type,
                enabled = enabled && !globalCooldown,
                onClick = {
                    globalCooldown = true
                    onDetection(type)
                }
            )
        }
    }
}

@Composable
private fun DetectionButton(
    type: MicroExpressionType,
    enabled: Boolean,
    onClick: () -> Unit
) {
    val context = LocalContext.current
    var isOnCooldown by remember { mutableStateOf(false) }
    var isPressed by remember { mutableStateOf(false) }

    val scale by animateFloatAsState(
        targetValue = if (isPressed) 1.3f else 1f,
        animationSpec = tween(100), label = "scale"
    )
    val bgColor by animateColorAsState(
        targetValue = if (isPressed) Gold else SurfaceLight,
        animationSpec = tween(100), label = "bgColor"
    )

    LaunchedEffect(isPressed) {
        if (isPressed) {
            delay(200)
            isPressed = false
        }
    }

    LaunchedEffect(isOnCooldown) {
        if (isOnCooldown) {
            delay(500)
            isOnCooldown = false
        }
    }

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .semantics { contentDescription = "Détecter ${type.labelRes}" }
            .alpha(if (enabled && !isOnCooldown) 1f else 0.4f)
            .clickable(
                enabled = enabled && !isOnCooldown,
                interactionSource = remember { MutableInteractionSource() },
                indication = null
            ) {
                isPressed = true
                isOnCooldown = true

                // Haptic feedback
                try {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                        val vm = context.getSystemService(VibratorManager::class.java)
                        vm?.defaultVibrator?.vibrate(
                            VibrationEffect.createOneShot(30, VibrationEffect.DEFAULT_AMPLITUDE)
                        )
                    } else {
                        @Suppress("DEPRECATION")
                        val vibrator = context.getSystemService(Vibrator::class.java)
                        vibrator?.vibrate(
                            VibrationEffect.createOneShot(30, VibrationEffect.DEFAULT_AMPLITUDE)
                        )
                    }
                } catch (_: Exception) { }

                onClick()
            }
    ) {
        Box(
            modifier = Modifier
                .size(52.dp)
                .scale(scale)
                .background(bgColor, CircleShape)
                .border(2.dp, Gold.copy(alpha = 0.5f), CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = type.emoji,
                fontSize = 22.sp
            )
        }
        Spacer(modifier = Modifier.height(3.dp))
        Text(
            text = type.labelRes,
            style = MaterialTheme.typography.labelSmall,
            color = TextSecondary,
            textAlign = TextAlign.Center,
            fontSize = 9.sp,
            maxLines = 1
        )
    }
}
