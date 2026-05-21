package com.theverdict.app.ui.components

import androidx.compose.animation.AnimatedVisibility
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
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.animation.slideInVertically
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
import androidx.compose.runtime.key
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
    initialDiscoveredClues: List<Clue> = emptyList(),
    hintClue: Clue? = null,
    pressureLevel: Int = 0,
    onClueDiscovered: (Clue) -> Unit = {},
    modifier: Modifier = Modifier
) {
    val zoneResults = remember {
        val map = mutableStateMapOf<AvatarZone, ZoneResult>()
        if (initialDiscoveredClues.isNotEmpty()) {
            AvatarZone.entries.forEach { zone ->
                if (zone.relatedClues.any { it in initialDiscoveredClues }) {
                    map[zone] = ZoneResult.FOUND
                }
            }
        }
        map
    }
    val discoveredClues = remember {
        val map = mutableStateMapOf<Clue, Boolean>()
        initialDiscoveredClues.forEach { map[it] = true }
        map
    }
    var flashingZone by remember { mutableStateOf<AvatarZone?>(null) }
    val haptic = LocalHapticManager.current
    val scope = rememberCoroutineScope()

    // Tap flash: gold glow on the zone immediately when tapped
    var tapFlashZone by remember { mutableStateOf<AvatarZone?>(null) }
    val tapFlashAlpha = remember { Animatable(0f) }

    // Popup: shows discovered clue label briefly above the touched zone
    var popupZone by remember { mutableStateOf<AvatarZone?>(null) }
    var popupText by remember { mutableStateOf("") }
    var popupVisible by remember { mutableStateOf(false) }

    LaunchedEffect(flashingZone) {
        if (flashingZone != null) {
            delay(600)
            flashingZone = null
        }
    }

    // Reveal zone when hint clue is provided
    LaunchedEffect(hintClue) {
        if (hintClue == null) return@LaunchedEffect
        val zone = AvatarZone.entries.find { hintClue in it.relatedClues }
            ?: return@LaunchedEffect
        if (zoneResults[zone] == null) {
            haptic.successPulse()
            zoneResults[zone] = ZoneResult.FOUND
            zone.relatedClues.filter { it in suspectClues }.forEach { clue ->
                if (!discoveredClues.containsKey(clue)) {
                    discoveredClues[clue] = true
                    onClueDiscovered(clue)
                }
            }
        }
    }

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier
    ) {
        // Zone pulse animation for unexplored zones
        val zonePulseInf = rememberInfiniteTransition(label = "zonePulse")
        val zonePulseAlpha by zonePulseInf.animateFloat(
            initialValue = 0.07f,
            targetValue = 0.30f,
            animationSpec = infiniteRepeatable(
                animation = tween(1100, easing = LinearEasing),
                repeatMode = RepeatMode.Reverse
            ),
            label = "zonePulse"
        )

        // Zone exploration counter — 3 zones
        val allZones = AvatarZone.entries
        val exploredCount = allZones.count { zoneResults.containsKey(it) }
        Text(
            text = "🔎 $exploredCount/${allZones.size} zones fouillées",
            style = MaterialTheme.typography.bodySmall,
            color = GoldLight.copy(alpha = 0.7f)
        )
        Spacer(Modifier.height(6.dp))

        // Avatar with clickable zones — TopStart alignment so offset() starts from (0,0)
        Box(
            modifier = Modifier.size(size)
        ) {
            // Background avatar
            SuspectAvatar(
                config = config,
                clues = discoveredClues.keys.toList(),
                size = size,
                pressureLevel = pressureLevel
            )

            // Clickable zone overlays — invisible by default, glow on result
            AvatarZone.entries.forEach { zone ->
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
                                // Tap flash: instant gold burst when tapped
                                if (tapFlashZone == zone && tapFlashAlpha.value > 0f) {
                                    drawRoundRect(
                                        color = GoldPrimary.copy(alpha = tapFlashAlpha.value),
                                        cornerRadius = CornerRadius(8.dp.toPx())
                                    )
                                }
                                // Pulsing gold border on unexplored zones so player sees them
                                if (result == null) {
                                    drawRoundRect(
                                        color = GoldPrimary.copy(alpha = zonePulseAlpha),
                                        cornerRadius = CornerRadius(8.dp.toPx()),
                                        style = Stroke(width = 2.dp.toPx())
                                    )
                                }
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
                                // Immediate gold flash regardless of outcome
                                scope.launch {
                                    tapFlashZone = zone
                                    tapFlashAlpha.snapTo(0.65f)
                                    tapFlashAlpha.animateTo(0f, tween(450))
                                    tapFlashZone = null
                                }
                                val matchingClues = zone.relatedClues.filter { it in suspectClues }
                                if (matchingClues.isNotEmpty()) {
                                    haptic.successPulse()
                                    zoneResults[zone] = ZoneResult.FOUND
                                    matchingClues.forEach { clue ->
                                        discoveredClues[clue] = true
                                        onClueDiscovered(clue)
                                    }
                                    // Show popup with first discovered clue label
                                    popupZone = zone
                                    popupText = matchingClues.first().label
                                    popupVisible = true
                                    scope.launch {
                                        delay(1800)
                                        popupVisible = false
                                        delay(400)
                                        popupZone = null
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

            // Animated popup showing discovered clue label above the touched zone
            if (popupZone != null) {
                val pZone = popupZone!!
                val popupX = ((pZone.left + pZone.right) / 2f * size.value - 72f).dp
                val popupY = (pZone.top * size.value - 44f).coerceAtLeast(0f).dp
                Column(modifier = Modifier.offset(x = popupX, y = popupY)) {
                    AnimatedVisibility(
                        visible = popupVisible,
                        enter = scaleIn(tween(180), initialScale = 0.6f) + fadeIn(tween(180)),
                        exit = fadeOut(tween(300))
                    ) {
                        Surface(
                            shape = RoundedCornerShape(20.dp),
                            color = GoldDark.copy(alpha = 0.95f)
                        ) {
                            Text(
                                text = "✨ $popupText",
                                color = DarkBackground,
                                style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                                modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                            )
                        }
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
                    key(clue) {
                        var visible by remember { mutableStateOf(false) }
                        LaunchedEffect(Unit) { visible = true }
                        AnimatedVisibility(
                            visible = visible,
                            enter = fadeIn(tween(250)) + slideInVertically(tween(250)) { it / 2 }
                        ) {
                            ClueChip(clue = clue)
                        }
                    }
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
        AvatarZone.JOUES -> {
            // Sweat droplet icon
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
    }
}
