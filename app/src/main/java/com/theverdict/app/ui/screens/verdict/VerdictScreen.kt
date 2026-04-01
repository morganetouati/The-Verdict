package com.theverdict.app.ui.screens.verdict

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Gavel
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateListOf
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
import androidx.compose.ui.graphics.Shadow
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.theverdict.app.data.repository.CaseRepository
import com.theverdict.app.data.repository.PlayerRepository
import com.theverdict.app.domain.model.CaseTheme
import com.theverdict.app.domain.model.PlayerProfile
import com.theverdict.app.domain.model.Suspect
import com.theverdict.app.ui.components.SuspectAvatar
import com.theverdict.app.ui.components.TimerBar
import com.theverdict.app.ui.theme.*
import com.theverdict.app.ui.util.LocalHapticManager
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun VerdictScreen(
    caseRepository: CaseRepository,
    playerRepository: PlayerRepository,
    themeIndex: Int,
    caseIndex: Int,
    onResult: (isCorrect: Boolean, pointsChange: Int) -> Unit
) {
    val theme = CaseTheme.entries[themeIndex]
    val case = caseRepository.getCase(theme, caseIndex)
    val profile by playerRepository.profile.collectAsState(initial = PlayerProfile())
    val scope = rememberCoroutineScope()
    val selectedIds = remember { mutableStateListOf<Int>() }
    var nobodySelected by remember { mutableIntStateOf(0) }
    val haptic = LocalHapticManager.current

    // Timer
    var remainingSeconds by remember { mutableIntStateOf(90) }
    val hasTimer = theme.hasTimer

    LaunchedEffect(hasTimer) {
        if (hasTimer) {
            while (remainingSeconds > 0) {
                delay(1000)
                remainingSeconds--
            }
            // Auto-submit empty verdict when time expires
            if (case != null) {
                scope.launch {
                    val result = playerRepository.applyVerdict(profile, case, emptyList())
                    onResult(result.isCorrect, result.pointsChange)
                }
            }
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(DarkBackground, Color(0xFF111111), DarkSurface, DarkBackground)
                )
            )
    ) {
        TopAppBar(
            title = {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.Gavel, contentDescription = null, tint = GoldPrimary)
                    Spacer(Modifier.width(8.dp))
                    Text(
                        "Votre Verdict",
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                        color = GoldLight
                    )
                }
            },
            colors = TopAppBarDefaults.topAppBarColors(
                containerColor = Color.Transparent,
                titleContentColor = GoldLight
            )
        )

        if (case != null) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(24.dp)
            ) {
                // Timer bar
                if (hasTimer) {
                    TimerBar(remainingSeconds = remainingSeconds)
                    Spacer(Modifier.height(16.dp))
                }

                Text(
                    text = "Qui ment ?",
                    style = MaterialTheme.typography.headlineMedium.copy(
                        fontWeight = FontWeight.Bold,
                        shadow = Shadow(color = GoldPrimary.copy(alpha = 0.3f), offset = Offset(0f, 2f), blurRadius = 8f)
                    ),
                    color = TextWhite
                )
                Spacer(Modifier.height(4.dp))
                Text(
                    text = if (theme.hasSpecialVerdicts) "Sélectionnez un ou plusieurs suspects, ou un choix spécial" else "Sélectionnez le suspect qui ment",
                    style = MaterialTheme.typography.bodyMedium,
                    color = TextGray
                )
                Spacer(Modifier.height(16.dp))

                // Suspect choices
                LazyColumn(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(case.suspects) { suspect ->
                        val isSelected = suspect.id in selectedIds
                        SuspectVerdictCard(
                            suspect = suspect,
                            isSelected = isSelected,
                            onClick = {
                                haptic.lightTap()
                                nobodySelected = 0
                                if (theme.hasSpecialVerdicts) {
                                    if (isSelected) selectedIds.remove(suspect.id)
                                    else selectedIds.add(suspect.id)
                                } else {
                                    selectedIds.clear()
                                    selectedIds.add(suspect.id)
                                }
                            }
                        )
                    }

                    // Special options for advanced themes
                    if (theme.hasSpecialVerdicts) {
                        item {
                            Spacer(Modifier.height(8.dp))
                            SpecialOption(
                                text = "👤 Personne ne ment",
                                isSelected = nobodySelected == 1,
                                onClick = {
                                    selectedIds.clear()
                                    nobodySelected = if (nobodySelected == 1) 0 else 1
                                }
                            )
                        }
                        item {
                            SpecialOption(
                                text = "👥 Tous mentent",
                                isSelected = nobodySelected == 2,
                                onClick = {
                                    selectedIds.clear()
                                    selectedIds.addAll(case.suspects.map { it.id })
                                    nobodySelected = if (nobodySelected == 2) 0 else 2
                                }
                            )
                        }
                    }
                }

                Spacer(Modifier.height(16.dp))

                // Confirm button — gold gradient when enabled
                val hasSelection = selectedIds.isNotEmpty() || nobodySelected == 1
                if (hasSelection) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .drawBehind {
                                drawRoundRect(
                                    brush = Brush.verticalGradient(listOf(Color(0x40D4A24C), Color.Transparent)),
                                    cornerRadius = CornerRadius(24.dp.toPx()),
                                    topLeft = Offset(0f, 4.dp.toPx()),
                                    size = Size(size.width, size.height + 4.dp.toPx())
                                )
                            }
                    ) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(56.dp)
                                .clip(RoundedCornerShape(24.dp))
                                .background(Brush.horizontalGradient(listOf(GoldDark, GoldPrimary, GoldLight)))
                                .clickable {
                                    haptic.medium()
                                    scope.launch {
                                        val liarIds = if (nobodySelected == 1) emptyList() else selectedIds.toList()
                                        val result = playerRepository.applyVerdict(profile, case, liarIds)
                                        onResult(result.isCorrect, result.pointsChange)
                                    }
                                },
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                "CONFIRMER LE VERDICT",
                                style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.ExtraBold, letterSpacing = 1.sp),
                                color = DarkBackground
                            )
                        }
                    }
                } else {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(56.dp)
                            .clip(RoundedCornerShape(24.dp))
                            .background(DarkSurfaceVariant),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            "CONFIRMER LE VERDICT",
                            style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                            color = TextDimmed
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun SuspectVerdictCard(
    suspect: Suspect,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    val bounceScale = remember { Animatable(1f) }
    val bgColor by animateColorAsState(
        targetValue = if (isSelected) VerdictWrong.copy(alpha = 0.1f) else DarkCard,
        animationSpec = spring(stiffness = Spring.StiffnessMediumLow),
        label = "bgColor"
    )

    LaunchedEffect(isSelected) {
        if (isSelected) {
            bounceScale.animateTo(1.04f, spring(stiffness = Spring.StiffnessHigh))
            bounceScale.animateTo(1f, spring(dampingRatio = Spring.DampingRatioMediumBouncy))
        }
    }

    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .graphicsLayer { scaleX = bounceScale.value; scaleY = bounceScale.value }
            .clickable(onClick = onClick)
            .then(
                if (isSelected) Modifier.border(2.dp, VerdictWrong, RoundedCornerShape(16.dp))
                else Modifier.border(
                    1.dp,
                    Brush.horizontalGradient(listOf(GoldDark.copy(alpha = 0.2f), GoldPrimary.copy(alpha = 0.1f))),
                    RoundedCornerShape(16.dp)
                )
            ),
        shape = RoundedCornerShape(16.dp),
        color = bgColor
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            SuspectAvatar(config = suspect.avatar, clues = suspect.indices, size = 48.dp)
            Spacer(Modifier.width(12.dp))
            Text(
                text = suspect.nom,
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.SemiBold),
                color = if (isSelected) VerdictWrong else TextWhite,
                modifier = Modifier.weight(1f)
            )
            if (isSelected) {
                Icon(Icons.Default.Check, contentDescription = null, tint = VerdictWrong)
            }
        }
    }
}

@Composable
private fun SpecialOption(
    text: String,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .then(
                if (isSelected) Modifier.border(2.dp, GoldPrimary, RoundedCornerShape(16.dp))
                else Modifier.border(
                    1.dp,
                    Brush.horizontalGradient(listOf(GoldDark.copy(alpha = 0.2f), GoldPrimary.copy(alpha = 0.1f))),
                    RoundedCornerShape(16.dp)
                )
            ),
        shape = RoundedCornerShape(16.dp),
        color = if (isSelected) GoldPrimary.copy(alpha = 0.1f) else DarkSurfaceVariant
    ) {
        Text(
            text = text,
            style = MaterialTheme.typography.titleMedium.copy(fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal),
            color = if (isSelected) GoldPrimary else TextGray,
            modifier = Modifier.padding(16.dp)
        )
    }
}
