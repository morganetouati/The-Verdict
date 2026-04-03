package com.theverdict.app.ui.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.theverdict.app.domain.model.AvatarConfig
import com.theverdict.app.domain.model.AvatarZone
import com.theverdict.app.domain.model.Clue
import com.theverdict.app.ui.theme.*
import com.theverdict.app.ui.util.LocalHapticManager
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

enum class ZoneResult { FOUND, NOT_FOUND }

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun InteractiveAvatar(
    config: AvatarConfig,
    suspectClues: List<Clue>,
    size: Dp = 300.dp,
    onClueDiscovered: (Clue) -> Unit = {},
    modifier: Modifier = Modifier
) {
    val zoneResults = remember { mutableStateMapOf<AvatarZone, ZoneResult>() }
    val discoveredClues = remember { mutableStateMapOf<Clue, Boolean>() }
    var flashingZone by remember { mutableStateOf<AvatarZone?>(null) }
    val haptic = LocalHapticManager.current
    val scope = rememberCoroutineScope()

    LaunchedEffect(flashingZone) {
        if (flashingZone != null) {
            delay(600)
            flashingZone = null
        }
    }

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier
    ) {
        // Avatar with clickable zones
        Box(
            modifier = Modifier.size(size),
            contentAlignment = Alignment.Center
        ) {
            // Background avatar
            SuspectAvatar(
                config = config,
                clues = discoveredClues.keys.toList(),
                size = size
            )

            // Clickable zone overlays — invisible by default, glow on result
            AvatarZone.entries
                .filter { it != AvatarZone.ATTITUDE && it.left != it.right }
                .forEach { zone ->
                    val result = zoneResults[zone]
                    val isFlashing = flashingZone == zone

                    // Pulse animation for found zones
                    val pulseAnim = remember { Animatable(1f) }
                    LaunchedEffect(result) {
                        if (result == ZoneResult.FOUND) {
                            pulseAnim.animateTo(1.08f, spring(stiffness = Spring.StiffnessHigh))
                            pulseAnim.animateTo(1f, spring(dampingRatio = Spring.DampingRatioMediumBouncy))
                        }
                    }

                    val glowAlpha by animateFloatAsState(
                        targetValue = when {
                            result == ZoneResult.FOUND -> 0.35f
                            isFlashing -> 0.4f
                            else -> 0f
                        },
                        animationSpec = tween(300),
                        label = "glowAlpha"
                    )

                    val glowColor = when {
                        result == ZoneResult.FOUND -> VerdictCorrect
                        isFlashing -> VerdictWrong
                        else -> Color.Transparent
                    }

                    Box(
                        modifier = Modifier
                            .offset(
                                x = (zone.left * size.value).dp,
                                y = (zone.top * size.value).dp
                            )
                            .size(
                                width = ((zone.right - zone.left) * size.value).dp,
                                height = ((zone.bottom - zone.top) * size.value).dp
                            )
                            .graphicsLayer {
                                scaleX = pulseAnim.value
                                scaleY = pulseAnim.value
                            }
                            .clip(RoundedCornerShape(8.dp))
                            .drawBehind {
                                if (glowAlpha > 0f) {
                                    drawRoundRect(
                                        brush = Brush.radialGradient(
                                            colors = listOf(
                                                glowColor.copy(alpha = glowAlpha),
                                                glowColor.copy(alpha = glowAlpha * 0.3f),
                                                Color.Transparent
                                            ),
                                            center = Offset(this.size.width / 2f, this.size.height / 2f),
                                            radius = maxOf(this.size.width, this.size.height) * 0.7f
                                        ),
                                        cornerRadius = CornerRadius(8.dp.toPx())
                                    )
                                }
                            }
                            .clickable(enabled = result == null) {
                                val matchingClues = zone.relatedClues.filter { it in suspectClues }
                                if (matchingClues.isNotEmpty()) {
                                    haptic.successPulse()
                                    zoneResults[zone] = ZoneResult.FOUND
                                    matchingClues.forEach { clue ->
                                        discoveredClues[clue] = true
                                        onClueDiscovered(clue)
                                    }
                                } else {
                                    haptic.errorBuzz()
                                    zoneResults[zone] = ZoneResult.NOT_FOUND
                                    flashingZone = zone
                                }
                            },
                        contentAlignment = Alignment.Center
                    ) {
                        when (result) {
                            ZoneResult.FOUND -> {
                                // Monochrome icon for found zone
                                Canvas(modifier = Modifier.size(22.dp)) {
                                    drawZoneIcon(zone, VerdictCorrect)
                                }
                            }
                            ZoneResult.NOT_FOUND -> if (isFlashing) {
                                Icon(
                                    Icons.Default.Close,
                                    contentDescription = "Rien ici",
                                    tint = VerdictWrong,
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                            null -> {
                                // Invisible — no hint icon
                            }
                        }
                    }
                }
        }

        Spacer(Modifier.height(12.dp))

        // ─── ATTITUDE button — bigger, gold, with scan animation ───
        val attitudeZone = AvatarZone.ATTITUDE
        val attitudeResult = zoneResults[attitudeZone]
        val isAttitudeFlashing = flashingZone == attitudeZone

        val inf = rememberInfiniteTransition(label = "attitudeScan")
        val scanProgress by inf.animateFloat(
            initialValue = 0f,
            targetValue = 1f,
            animationSpec = infiniteRepeatable(
                animation = tween(2000, easing = LinearEasing),
                repeatMode = RepeatMode.Restart
            ),
            label = "scanLine"
        )

        val attitudeInteraction = remember { MutableInteractionSource() }
        val attitudePressed by attitudeInteraction.collectIsPressedAsState()
        val attitudeScale by animateFloatAsState(
            targetValue = if (attitudePressed) 0.95f else 1f,
            animationSpec = tween(100),
            label = "attScale"
        )

        val attitudeBgBrush = when {
            attitudeResult == ZoneResult.FOUND -> Brush.horizontalGradient(
                listOf(VerdictCorrect.copy(alpha = 0.2f), VerdictCorrect.copy(alpha = 0.1f))
            )
            isAttitudeFlashing -> Brush.horizontalGradient(
                listOf(VerdictWrong.copy(alpha = 0.3f), VerdictWrong.copy(alpha = 0.15f))
            )
            else -> Brush.horizontalGradient(listOf(GoldDark, GoldPrimary, GoldLight))
        }

        val attitudeTextColor = when {
            attitudeResult == ZoneResult.FOUND -> VerdictCorrect
            isAttitudeFlashing -> VerdictWrong
            else -> DarkBackground
        }

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp)
                .graphicsLayer { scaleX = attitudeScale; scaleY = attitudeScale }
                .drawBehind {
                    if (attitudeResult == null) {
                        drawRoundRect(
                            brush = Brush.verticalGradient(listOf(Color(0x40D4A24C), Color.Transparent)),
                            cornerRadius = CornerRadius(16.dp.toPx()),
                            topLeft = Offset(0f, 3.dp.toPx()),
                            size = Size(this.size.width, this.size.height + 3.dp.toPx())
                        )
                    }
                }
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp)
                    .clip(RoundedCornerShape(16.dp))
                    .background(attitudeBgBrush)
                    .then(
                        if (attitudeResult != null) Modifier.border(
                            1.dp,
                            if (attitudeResult == ZoneResult.FOUND) VerdictCorrect.copy(alpha = 0.4f) else VerdictWrong.copy(alpha = 0.3f),
                            RoundedCornerShape(16.dp)
                        ) else Modifier
                    )
                    .drawBehind {
                        // Animated scan line (only when not yet clicked)
                        if (attitudeResult == null) {
                            val lineY = scanProgress * this.size.height
                            drawLine(
                                brush = Brush.horizontalGradient(
                                    listOf(
                                        Color.Transparent,
                                        Color.White.copy(alpha = 0.25f),
                                        Color.White.copy(alpha = 0.4f),
                                        Color.White.copy(alpha = 0.25f),
                                        Color.Transparent
                                    )
                                ),
                                start = Offset(0f, lineY),
                                end = Offset(this.size.width, lineY),
                                strokeWidth = 2.dp.toPx()
                            )
                        }
                    }
                    .clickable(
                        interactionSource = attitudeInteraction,
                        indication = null,
                        enabled = attitudeResult == null
                    ) {
                        val matchingClues = attitudeZone.relatedClues.filter { it in suspectClues }
                        if (matchingClues.isNotEmpty()) {
                            haptic.successPulse()
                            zoneResults[attitudeZone] = ZoneResult.FOUND
                            matchingClues.forEach { clue ->
                                discoveredClues[clue] = true
                                onClueDiscovered(clue)
                            }
                        } else {
                            haptic.errorBuzz()
                            zoneResults[attitudeZone] = ZoneResult.NOT_FOUND
                            flashingZone = attitudeZone
                        }
                    },
                contentAlignment = Alignment.Center
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        Icons.Default.Search,
                        contentDescription = null,
                        tint = attitudeTextColor,
                        modifier = Modifier.size(22.dp)
                    )
                    Spacer(Modifier.width(10.dp))
                    Text(
                        text = when {
                            attitudeResult == ZoneResult.FOUND -> "ATTITUDE SUSPECTE DÉTECTÉE ✓"
                            attitudeResult == ZoneResult.NOT_FOUND -> "ATTITUDE NORMALE ✗"
                            else -> "ANALYSER L'ATTITUDE"
                        },
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.ExtraBold,
                            letterSpacing = 1.sp
                        ),
                        color = attitudeTextColor
                    )
                }
            }
        }

        // Discovered clues section
        val found = discoveredClues.keys.toList()
        if (found.isNotEmpty()) {
            Spacer(Modifier.height(16.dp))

            Text(
                text = "Indices découverts",
                style = MaterialTheme.typography.titleMedium,
                color = GoldPrimary,
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(Modifier.height(8.dp))

            FlowRow(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                found.forEach { clue ->
                    ClueChip(clue = clue)
                }
            }
        }
    }
}

/** Draws a styled monochrome icon for each zone type */
private fun DrawScope.drawZoneIcon(zone: AvatarZone, color: Color) {
    val w = size.width
    val h = size.height
    val sw = w * 0.1f
    when (zone) {
        AvatarZone.FRONT -> {
            // Droplet icon
            val path = Path().apply {
                moveTo(w * 0.5f, h * 0.1f)
                cubicTo(w * 0.5f, h * 0.1f, w * 0.15f, h * 0.55f, w * 0.15f, h * 0.65f)
                cubicTo(w * 0.15f, h * 0.85f, w * 0.35f, h * 0.95f, w * 0.5f, h * 0.95f)
                cubicTo(w * 0.65f, h * 0.95f, w * 0.85f, h * 0.85f, w * 0.85f, h * 0.65f)
                cubicTo(w * 0.85f, h * 0.55f, w * 0.5f, h * 0.1f, w * 0.5f, h * 0.1f)
                close()
            }
            drawPath(path, color)
        }
        AvatarZone.YEUX -> {
            // Eye icon
            val eyePath = Path().apply {
                moveTo(w * 0.05f, h * 0.5f)
                cubicTo(w * 0.2f, h * 0.15f, w * 0.8f, h * 0.15f, w * 0.95f, h * 0.5f)
                cubicTo(w * 0.8f, h * 0.85f, w * 0.2f, h * 0.85f, w * 0.05f, h * 0.5f)
                close()
            }
            drawPath(eyePath, color.copy(alpha = 0.3f))
            drawPath(eyePath, color, style = Stroke(sw * 0.8f))
            drawCircle(color, w * 0.15f, Offset(w * 0.5f, h * 0.5f))
        }
        AvatarZone.SOURCILS -> {
            // Stress/tension lines
            drawLine(color, Offset(w * 0.15f, h * 0.35f), Offset(w * 0.4f, h * 0.55f), sw, StrokeCap.Round)
            drawLine(color, Offset(w * 0.6f, h * 0.55f), Offset(w * 0.85f, h * 0.35f), sw, StrokeCap.Round)
            drawLine(color, Offset(w * 0.3f, h * 0.2f), Offset(w * 0.7f, h * 0.2f), sw * 0.6f, StrokeCap.Round)
        }
        AvatarZone.BOUCHE -> {
            // Lips icon
            val lipsPath = Path().apply {
                moveTo(w * 0.1f, h * 0.5f)
                cubicTo(w * 0.25f, h * 0.2f, w * 0.45f, h * 0.25f, w * 0.5f, h * 0.4f)
                cubicTo(w * 0.55f, h * 0.25f, w * 0.75f, h * 0.2f, w * 0.9f, h * 0.5f)
                cubicTo(w * 0.75f, h * 0.85f, w * 0.25f, h * 0.85f, w * 0.1f, h * 0.5f)
                close()
            }
            drawPath(lipsPath, color)
        }
        AvatarZone.MAINS -> {
            // Hand/tremble icon  
            drawLine(color, Offset(w * 0.2f, h * 0.3f), Offset(w * 0.35f, h * 0.7f), sw, StrokeCap.Round)
            drawLine(color, Offset(w * 0.4f, h * 0.2f), Offset(w * 0.5f, h * 0.65f), sw, StrokeCap.Round)
            drawLine(color, Offset(w * 0.6f, h * 0.2f), Offset(w * 0.65f, h * 0.65f), sw, StrokeCap.Round)
            drawLine(color, Offset(w * 0.8f, h * 0.35f), Offset(w * 0.78f, h * 0.65f), sw, StrokeCap.Round)
            drawArc(color, 0f, 180f, false, Offset(w * 0.2f, h * 0.55f), Size(w * 0.65f, h * 0.35f), style = Stroke(sw))
        }
        AvatarZone.BRAS -> {
            // Crossed arms icon
            drawLine(color, Offset(w * 0.15f, h * 0.2f), Offset(w * 0.85f, h * 0.8f), sw * 1.2f, StrokeCap.Round)
            drawLine(color, Offset(w * 0.85f, h * 0.2f), Offset(w * 0.15f, h * 0.8f), sw * 1.2f, StrokeCap.Round)
        }
        AvatarZone.CORPS -> {
            // Body/posture icon
            drawCircle(color, w * 0.12f, Offset(w * 0.5f, h * 0.15f))
            drawLine(color, Offset(w * 0.5f, h * 0.28f), Offset(w * 0.5f, h * 0.65f), sw * 1.2f, StrokeCap.Round)
            drawLine(color, Offset(w * 0.25f, h * 0.4f), Offset(w * 0.75f, h * 0.4f), sw, StrokeCap.Round)
            drawLine(color, Offset(w * 0.5f, h * 0.65f), Offset(w * 0.3f, h * 0.95f), sw, StrokeCap.Round)
            drawLine(color, Offset(w * 0.5f, h * 0.65f), Offset(w * 0.7f, h * 0.95f), sw, StrokeCap.Round)
        }
        AvatarZone.ATTITUDE -> { /* Rendered as button, not zone */ }
    }
}
