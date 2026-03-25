package com.theverdict.app.ui.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.theverdict.app.domain.model.AvatarConfig
import com.theverdict.app.domain.model.AvatarZone
import com.theverdict.app.domain.model.Clue
import com.theverdict.app.ui.theme.*
import kotlinx.coroutines.delay

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
    // Track zones that are flashing red (temporarily)
    var flashingZone by remember { mutableStateOf<AvatarZone?>(null) }

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
            // Background avatar (no clue visuals — player must discover them)
            SuspectAvatar(
                config = config,
                clues = emptyList(), // Hide visual clue hints until discovered
                size = size
            )

            // Clickable zone overlays (only body zones, not ATTITUDE)
            AvatarZone.entries
                .filter { it != AvatarZone.ATTITUDE && it.left != it.right }
                .forEach { zone ->
                    val result = zoneResults[zone]
                    val isFlashing = flashingZone == zone

                    val zoneColor by animateColorAsState(
                        targetValue = when {
                            result == ZoneResult.FOUND -> VerdictCorrect.copy(alpha = 0.3f)
                            isFlashing -> VerdictWrong.copy(alpha = 0.4f)
                            else -> Color.Transparent
                        },
                        animationSpec = tween(300),
                        label = "zoneColor"
                    )

                    val borderColor = when {
                        result == ZoneResult.FOUND -> VerdictCorrect
                        isFlashing -> VerdictWrong
                        else -> TextDimmed.copy(alpha = 0.5f)
                    }

                    val zoneSizePx = with(LocalDensity.current) { size.toPx() }

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
                            .clip(RoundedCornerShape(8.dp))
                            .background(zoneColor)
                            .then(
                                if (result == null) {
                                    Modifier.border(
                                        width = 1.dp,
                                        color = borderColor,
                                        shape = RoundedCornerShape(8.dp)
                                    )
                                } else {
                                    Modifier.border(
                                        width = 2.dp,
                                        color = borderColor,
                                        shape = RoundedCornerShape(8.dp)
                                    )
                                }
                            )
                            .clickable(enabled = result == null) {
                                val matchingClues = zone.relatedClues.filter { it in suspectClues }
                                if (matchingClues.isNotEmpty()) {
                                    zoneResults[zone] = ZoneResult.FOUND
                                    matchingClues.forEach { clue ->
                                        discoveredClues[clue] = true
                                        onClueDiscovered(clue)
                                    }
                                } else {
                                    zoneResults[zone] = ZoneResult.NOT_FOUND
                                    flashingZone = zone
                                }
                            },
                        contentAlignment = Alignment.Center
                    ) {
                        when (result) {
                            ZoneResult.FOUND -> Icon(
                                Icons.Default.Check,
                                contentDescription = "Indice trouvé",
                                tint = VerdictCorrect,
                                modifier = Modifier.size(20.dp)
                            )
                            ZoneResult.NOT_FOUND -> if (isFlashing) {
                                Icon(
                                    Icons.Default.Close,
                                    contentDescription = "Rien ici",
                                    tint = VerdictWrong,
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                            null -> {
                                // Subtle hint — zone label
                                Text(
                                    text = zone.emoji,
                                    style = MaterialTheme.typography.bodySmall,
                                    color = TextDimmed.copy(alpha = 0.6f)
                                )
                            }
                        }
                    }
                }
        }

        Spacer(Modifier.height(12.dp))

        // ATTITUDE button (for non-physical clues)
        val attitudeZone = AvatarZone.ATTITUDE
        val attitudeResult = zoneResults[attitudeZone]
        val isAttitudeFlashing = flashingZone == attitudeZone

        val attitudeBg by animateColorAsState(
            targetValue = when {
                attitudeResult == ZoneResult.FOUND -> VerdictCorrect.copy(alpha = 0.2f)
                isAttitudeFlashing -> VerdictWrong.copy(alpha = 0.3f)
                else -> DarkSurfaceVariant
            },
            animationSpec = tween(300),
            label = "attitudeBg"
        )

        Surface(
            modifier = Modifier
                .clickable(enabled = attitudeResult == null) {
                    val matchingClues = attitudeZone.relatedClues.filter { it in suspectClues }
                    if (matchingClues.isNotEmpty()) {
                        zoneResults[attitudeZone] = ZoneResult.FOUND
                        matchingClues.forEach { clue ->
                            discoveredClues[clue] = true
                            onClueDiscovered(clue)
                        }
                    } else {
                        zoneResults[attitudeZone] = ZoneResult.NOT_FOUND
                        flashingZone = attitudeZone
                    }
                },
            shape = RoundedCornerShape(12.dp),
            color = attitudeBg
        ) {
            Row(
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 10.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    Icons.Default.Search,
                    contentDescription = null,
                    tint = when {
                        attitudeResult == ZoneResult.FOUND -> VerdictCorrect
                        isAttitudeFlashing -> VerdictWrong
                        else -> GoldPrimary
                    },
                    modifier = Modifier.size(20.dp)
                )
                Spacer(Modifier.width(8.dp))
                Text(
                    text = if (attitudeResult == ZoneResult.FOUND) "Attitude suspecte détectée ✓"
                    else if (attitudeResult == ZoneResult.NOT_FOUND) "Attitude normale ✗"
                    else "Analyser l'attitude",
                    style = MaterialTheme.typography.labelLarge,
                    color = when {
                        attitudeResult == ZoneResult.FOUND -> VerdictCorrect
                        attitudeResult == ZoneResult.NOT_FOUND -> TextDimmed
                        else -> TextWhite
                    }
                )
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
