package com.theverdict.app.ui.screens.suspects

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.Gavel
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.theverdict.app.data.repository.CaseRepository
import com.theverdict.app.domain.model.CaseTheme
import com.theverdict.app.domain.model.Suspect
import com.theverdict.app.ui.components.SuspectAvatar
import com.theverdict.app.ui.theme.*
import com.theverdict.app.ui.util.LocalHapticManager
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SuspectsListScreen(
    caseRepository: CaseRepository,
    themeIndex: Int,
    caseIndex: Int,
    onInterrogate: (suspectId: Int) -> Unit,
    onGoToVerdict: () -> Unit,
    onBack: () -> Unit
) {
    val theme = CaseTheme.entries[themeIndex]
    val case = caseRepository.getCase(theme, caseIndex)
    val haptic = LocalHapticManager.current
    val interrogatedIds = remember { mutableStateListOf<Int>() }

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
                Text(
                    "Les Suspects",
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                    color = GoldLight
                )
            },
            navigationIcon = {
                IconButton(onClick = onBack) {
                    Icon(Icons.AutoMirrored.Filled.ArrowBack, "Retour", tint = GoldPrimary)
                }
            },
            colors = TopAppBarDefaults.topAppBarColors(
                containerColor = Color.Transparent,
                titleContentColor = GoldLight
            )
        )

        if (case != null) {
            LazyColumn(
                modifier = Modifier
                    .weight(1f)
                    .padding(horizontal = 24.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                itemsIndexed(case.suspects) { index, suspect ->
                    val itemAlpha = remember { Animatable(0f) }
                    val itemOffsetX = remember { Animatable(40f) }
                    LaunchedEffect(Unit) {
                        delay(index * 100L)
                        launch { itemAlpha.animateTo(1f, tween(350)) }
                        itemOffsetX.animateTo(0f, tween(350))
                    }
                    SuspectCard(
                        suspect = suspect,
                        isInterrogated = suspect.id in interrogatedIds,
                        onClick = {
                            haptic.lightTap()
                            if (suspect.id !in interrogatedIds) interrogatedIds.add(suspect.id)
                            onInterrogate(suspect.id)
                        },
                        modifier = Modifier.graphicsLayer {
                            alpha = itemAlpha.value
                            translationX = itemOffsetX.value
                        }
                    )
                }
            }

            // Gold gradient verdict button
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(24.dp)
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
                        .clickable { onGoToVerdict() },
                    contentAlignment = Alignment.Center
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.Gavel, contentDescription = null, tint = DarkBackground, modifier = Modifier.size(22.dp))
                        Spacer(Modifier.width(8.dp))
                        Text(
                            "RENDRE LE VERDICT",
                            style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.ExtraBold, letterSpacing = 1.sp),
                            color = DarkBackground
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun SuspectCard(
    suspect: Suspect,
    isInterrogated: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier
            .fillMaxWidth()
            .drawBehind {
                // Shadow under card
                drawRoundRect(
                    color = Color.Black.copy(alpha = 0.35f),
                    cornerRadius = CornerRadius(16.dp.toPx()),
                    topLeft = Offset(2.dp.toPx(), 4.dp.toPx()),
                    size = Size(size.width - 2.dp.toPx(), size.height)
                )
            }
            .border(
                width = 1.dp,
                brush = Brush.horizontalGradient(listOf(GoldDark.copy(alpha = 0.3f), GoldPrimary.copy(alpha = 0.15f))),
                shape = RoundedCornerShape(16.dp)
            )
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(16.dp),
        color = DarkCard
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Avatar with optional interrogated badge
            Box {
                SuspectAvatar(
                    config = suspect.avatar,
                    clues = suspect.indices,
                    size = 80.dp
                )
                if (isInterrogated) {
                    Box(
                        modifier = Modifier
                            .align(Alignment.BottomEnd)
                            .size(22.dp)
                            .clip(CircleShape)
                            .background(VerdictCorrect),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            Icons.Default.Check,
                            contentDescription = "Interrogé",
                            tint = Color.White,
                            modifier = Modifier.size(14.dp)
                        )
                    }
                }
            }
            Spacer(Modifier.width(16.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = suspect.nom,
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.SemiBold),
                    color = TextWhite
                )
                Spacer(Modifier.height(4.dp))
                Text(
                    text = if (isInterrogated) "Déjà interrogé" else "Appuyer pour interroger",
                    style = MaterialTheme.typography.bodySmall,
                    color = if (isInterrogated) VerdictCorrect.copy(alpha = 0.7f) else GoldLight.copy(alpha = 0.6f)
                )
            }
            Icon(Icons.Default.ChevronRight, contentDescription = null, tint = GoldPrimary.copy(alpha = 0.5f))
        }
    }
}
