package com.theverdict.app.ui.screens.menu

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.MenuBook
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Policy
import androidx.compose.material.icons.filled.WorkspacePremium
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
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
import androidx.compose.ui.graphics.Shadow
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.theverdict.app.data.repository.CaseRepository
import com.theverdict.app.data.repository.PlayerRepository
import com.theverdict.app.domain.model.CaseTheme
import com.theverdict.app.domain.model.PlayerProfile
import com.theverdict.app.ui.components.ParticleBackground
import com.theverdict.app.ui.components.RankBadge
import com.theverdict.app.ui.components.ReputationBar
import com.theverdict.app.ui.theme.*
import com.theverdict.app.ui.util.LocalHapticManager

@Composable
fun MainMenuScreen(
    playerRepository: PlayerRepository,
    caseRepository: CaseRepository,
    onPlay: (themeIndex: Int, caseIndex: Int) -> Unit,
    onReputation: () -> Unit,
    onTutorial: () -> Unit = {},
    onProfile: () -> Unit = {},
    onPrivacyPolicy: () -> Unit = {}
) {
    val profile by playerRepository.profile.collectAsState(initial = PlayerProfile())
    val dim = LocalDimensions.current
    val haptic = LocalHapticManager.current
    val goldGlow = GoldPrimary.copy(alpha = 0.35f)
    val goldGlowOuter = GoldPrimary.copy(alpha = 0.10f)

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(DarkBackground, Color(0xFF111111), DarkSurface, DarkBackground)
                )
            )
    ) {
        // Floating golden particles
        ParticleBackground()

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = dim.paddingLarge, vertical = dim.paddingMedium),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(Modifier.height(dim.topSpacing))

            // ─── Scales of Justice icon with golden glow ───
            val iconSizeDp = dim.titleSize.value * 3.2f
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .size(iconSizeDp.dp)
                    .drawBehind {
                        drawCircle(
                            brush = Brush.radialGradient(
                                colors = listOf(goldGlowOuter, Color.Transparent),
                                center = center,
                                radius = size.minDimension * 0.85f
                            )
                        )
                        drawCircle(
                            brush = Brush.radialGradient(
                                colors = listOf(goldGlow, Color.Transparent),
                                center = center,
                                radius = size.minDimension * 0.5f
                            )
                        )
                    }
            ) {
                Canvas(modifier = Modifier.size((iconSizeDp * 0.7f).dp)) {
                    val w = size.width
                    val h = size.height
                    val gold = Color(0xFFD4A24C)
                    val goldL = Color(0xFFE8C97A)
                    val sw = w * 0.028f

                    drawLine(gold, Offset(w * 0.5f, h * 0.12f), Offset(w * 0.5f, h * 0.82f), sw * 1.3f, StrokeCap.Round)
                    drawLine(gold, Offset(w * 0.3f, h * 0.82f), Offset(w * 0.7f, h * 0.82f), sw * 1.5f, StrokeCap.Round)
                    drawLine(goldL, Offset(w * 0.35f, h * 0.87f), Offset(w * 0.65f, h * 0.87f), sw, StrokeCap.Round)
                    drawCircle(goldL, w * 0.04f, Offset(w * 0.5f, h * 0.12f))
                    drawLine(gold, Offset(w * 0.5f, h * 0.15f), Offset(w * 0.18f, h * 0.30f), sw, StrokeCap.Round)
                    drawLine(gold, Offset(w * 0.5f, h * 0.15f), Offset(w * 0.82f, h * 0.25f), sw, StrokeCap.Round)
                    drawLine(gold, Offset(w * 0.18f, h * 0.30f), Offset(w * 0.10f, h * 0.52f), sw * 0.7f)
                    drawLine(gold, Offset(w * 0.18f, h * 0.30f), Offset(w * 0.26f, h * 0.52f), sw * 0.7f)
                    drawArc(goldL, 0f, 180f, false, Offset(w * 0.06f, h * 0.47f), Size(w * 0.24f, h * 0.14f), style = Stroke(sw))
                    drawLine(gold, Offset(w * 0.82f, h * 0.25f), Offset(w * 0.74f, h * 0.47f), sw * 0.7f)
                    drawLine(gold, Offset(w * 0.82f, h * 0.25f), Offset(w * 0.90f, h * 0.47f), sw * 0.7f)
                    drawArc(goldL, 0f, 180f, false, Offset(w * 0.70f, h * 0.42f), Size(w * 0.24f, h * 0.14f), style = Stroke(sw))
                }
            }

            Spacer(Modifier.height(dim.paddingSmall))

            // ─── Title with shadow + glow ───
            Text(
                text = "THE VERDICT",
                style = MaterialTheme.typography.displayLarge.copy(
                    fontSize = dim.titleSize * 1.1f,
                    letterSpacing = 4.sp,
                    fontWeight = FontWeight.ExtraBold,
                    shadow = Shadow(
                        color = GoldPrimary.copy(alpha = 0.6f),
                        offset = Offset(0f, 4f),
                        blurRadius = 16f
                    )
                ),
                color = GoldPrimary
            )

            Spacer(Modifier.height(4.dp))

            Text(
                text = "Rendez justice. Trouvez le menteur.",
                style = MaterialTheme.typography.bodyMedium.copy(
                    fontSize = dim.bodySize * 1.05f,
                    fontStyle = FontStyle.Italic,
                    letterSpacing = 1.sp,
                    shadow = Shadow(
                        color = GoldPrimary.copy(alpha = 0.25f),
                        offset = Offset(0f, 2f),
                        blurRadius = 8f
                    )
                ),
                color = GoldLight
            )

            Spacer(Modifier.height(dim.paddingMedium))

            // ─── Stats Card with rank, reputation + success rate ring ───
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .border(
                        width = 1.dp,
                        brush = Brush.verticalGradient(listOf(GoldDark.copy(alpha = 0.4f), GoldPrimary.copy(alpha = 0.15f))),
                        shape = RoundedCornerShape(16.dp)
                    )
                    .drawBehind {
                        drawRoundRect(
                            color = Color(0x18D4A24C),
                            cornerRadius = CornerRadius(16.dp.toPx()),
                            size = Size(size.width + 8.dp.toPx(), size.height + 8.dp.toPx()),
                            topLeft = Offset(-4.dp.toPx(), -4.dp.toPx())
                        )
                    },
                shape = RoundedCornerShape(16.dp),
                color = DarkCard.copy(alpha = 0.85f)
            ) {
                Row(
                    modifier = Modifier.padding(dim.paddingMedium),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Left: Rank + Reputation
                    Column(
                        modifier = Modifier.weight(1f),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        RankBadge(rank = profile.rank)
                        Spacer(Modifier.height(dim.paddingSmall))
                        ReputationBar(
                            reputation = profile.reputation,
                            rank = profile.rank,
                            modifier = Modifier.fillMaxWidth()
                        )
                        Spacer(Modifier.height(6.dp))
                        Text(
                            text = "${profile.completedCaseIds.size}/80 affaires",
                            style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Medium),
                            color = TextGray
                        )
                    }

                    Spacer(Modifier.width(16.dp))

                    // Right: Success rate ring
                    Box(
                        contentAlignment = Alignment.Center,
                        modifier = Modifier.size(64.dp)
                    ) {
                        val rate = profile.successRate.toFloat()
                        Canvas(modifier = Modifier.size(64.dp)) {
                            val strokeW = 5.dp.toPx()
                            val arcSize = Size(size.width - strokeW, size.height - strokeW)
                            val arcOffset = Offset(strokeW / 2, strokeW / 2)
                            // Background ring
                            drawArc(
                                color = DarkSurfaceVariant,
                                startAngle = -90f,
                                sweepAngle = 360f,
                                useCenter = false,
                                topLeft = arcOffset,
                                size = arcSize,
                                style = Stroke(strokeW, cap = StrokeCap.Round)
                            )
                            // Progress ring
                            drawArc(
                                brush = Brush.sweepGradient(listOf(GoldDark, GoldPrimary, GoldLight)),
                                startAngle = -90f,
                                sweepAngle = 360f * rate / 100f,
                                useCenter = false,
                                topLeft = arcOffset,
                                size = arcSize,
                                style = Stroke(strokeW, cap = StrokeCap.Round)
                            )
                        }
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(
                                text = "${profile.successRate}%",
                                style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Bold),
                                color = GoldPrimary
                            )
                        }
                    }
                }
            }

            Spacer(Modifier.height(dim.paddingLarge))

            // ─── Play button with gradient, shadow, press effect, judge icon ───
            val interactionSource = remember { MutableInteractionSource() }
            val isPressed by interactionSource.collectIsPressedAsState()
            val buttonScale by animateFloatAsState(
                targetValue = if (isPressed) 0.96f else 1f,
                animationSpec = tween(100),
                label = "btnScale"
            )

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .graphicsLayer {
                        scaleX = buttonScale
                        scaleY = buttonScale
                    }
                    .drawBehind {
                        drawRoundRect(
                            brush = Brush.verticalGradient(
                                listOf(Color(0x40D4A24C), Color.Transparent)
                            ),
                            cornerRadius = CornerRadius(24.dp.toPx()),
                            topLeft = Offset(0f, 4.dp.toPx()),
                            size = Size(size.width, size.height + 6.dp.toPx())
                        )
                    }
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(dim.buttonHeight + 4.dp)
                        .clip(RoundedCornerShape(24.dp))
                        .background(
                            Brush.horizontalGradient(
                                listOf(GoldDark, GoldPrimary, GoldLight)
                            )
                        )
                        .clickable(
                            interactionSource = interactionSource,
                            indication = null
                        ) {
                            haptic.medium()
                            onPlay(profile.currentThemeIndex, profile.currentCaseIndex)
                        },
                    contentAlignment = Alignment.Center
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Canvas(modifier = Modifier.size(24.dp)) {
                            val w = size.width; val h = size.height
                            val dk = Color(0xFF1A1A1A)
                            drawLine(dk, Offset(w * 0.5f, h * 0.2f), Offset(w * 0.5f, h * 0.72f), w * 0.09f, StrokeCap.Round)
                            drawRoundRect(dk, Offset(w * 0.18f, h * 0.02f), Size(w * 0.64f, h * 0.26f), CornerRadius(w * 0.06f))
                            drawLine(dk, Offset(w * 0.22f, h * 0.82f), Offset(w * 0.78f, h * 0.82f), w * 0.1f, StrokeCap.Round)
                        }
                        Spacer(Modifier.width(dim.paddingSmall))
                        Text(
                            "JOUER",
                            style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.ExtraBold, letterSpacing = 2.sp),
                            color = DarkBackground
                        )
                    }
                }
            }

            Spacer(Modifier.height(dim.paddingSmall))

            // ─── Secondary buttons — gold bordered with icons ───
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(dim.paddingSmall)
            ) {
                MenuButton(
                    icon = Icons.Default.WorkspacePremium,
                    label = "RÉPUTATION",
                    onClick = onReputation,
                    modifier = Modifier.weight(1f).height(dim.buttonHeightSmall)
                )
                MenuButton(
                    icon = Icons.Default.Person,
                    label = "PROFIL",
                    onClick = onProfile,
                    modifier = Modifier.weight(1f).height(dim.buttonHeightSmall)
                )
            }

            Spacer(Modifier.height(dim.paddingSmall))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(dim.paddingSmall)
            ) {
                MenuButton(
                    icon = Icons.AutoMirrored.Filled.MenuBook,
                    label = "TUTORIEL",
                    onClick = onTutorial,
                    modifier = Modifier.weight(1f).height(dim.buttonHeightSmall)
                )
                MenuButton(
                    icon = Icons.Default.Policy,
                    label = "MENTIONS",
                    onClick = onPrivacyPolicy,
                    modifier = Modifier.weight(1f).height(dim.buttonHeightSmall),
                    dimmed = true
                )
            }

            Spacer(Modifier.height(dim.paddingMedium))

            // ─── Theme selection ───
            Text(
                text = "Thèmes",
                style = MaterialTheme.typography.headlineMedium.copy(
                    fontSize = dim.subtitleSize,
                    fontWeight = FontWeight.Bold,
                    shadow = Shadow(
                        color = GoldPrimary.copy(alpha = 0.2f),
                        offset = Offset(0f, 2f),
                        blurRadius = 6f
                    )
                ),
                color = TextWhite,
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(Modifier.height(dim.paddingSmall))

            LazyColumn(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(dim.paddingSmall)
            ) {
                itemsIndexed(CaseTheme.entries.toList()) { index, theme ->
                    val isUnlocked = playerRepository.isThemeUnlocked(theme, profile)
                    val progress = profile.themeProgress[index] ?: 0

                    ThemeCard(
                        theme = theme,
                        isUnlocked = isUnlocked,
                        progress = progress,
                        isCurrent = index == profile.currentThemeIndex,
                        onClick = {
                            if (isUnlocked) onPlay(index, 0)
                        }
                    )
                }
            }
        }
    }
}

@Composable
private fun MenuButton(
    icon: ImageVector,
    label: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    dimmed: Boolean = false
) {
    val haptic = LocalHapticManager.current
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()
    val scale by animateFloatAsState(
        targetValue = if (isPressed) 0.93f else 1f,
        animationSpec = tween(100),
        label = "menuBtnScale"
    )

    Box(
        modifier = modifier
            .graphicsLayer { scaleX = scale; scaleY = scale }
            .clip(RoundedCornerShape(12.dp))
            .border(
                width = 1.dp,
                brush = Brush.horizontalGradient(
                    listOf(
                        GoldDark.copy(alpha = if (dimmed) 0.2f else 0.4f),
                        GoldPrimary.copy(alpha = if (dimmed) 0.1f else 0.25f)
                    )
                ),
                shape = RoundedCornerShape(12.dp)
            )
            .background(DarkSurfaceVariant.copy(alpha = 0.6f))
            .clickable(
                interactionSource = interactionSource,
                indication = null
            ) {
                haptic.lightTap()
                onClick()
            },
        contentAlignment = Alignment.Center
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            Icon(
                icon,
                contentDescription = null,
                tint = if (dimmed) TextDimmed else GoldLight,
                modifier = Modifier.size(16.dp)
            )
            Spacer(Modifier.width(6.dp))
            Text(
                label,
                style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.SemiBold),
                color = if (dimmed) TextGray else GoldLight
            )
        }
    }
}

@Composable
private fun ThemeCard(
    theme: CaseTheme,
    isUnlocked: Boolean,
    progress: Int,
    isCurrent: Boolean,
    onClick: () -> Unit
) {
    // Pulsing border for current theme
    val infiniteTransition = rememberInfiniteTransition(label = "currentTheme")
    val pulseAlpha by infiniteTransition.animateFloat(
        initialValue = 0.3f,
        targetValue = 0.7f,
        animationSpec = infiniteRepeatable(
            animation = tween(1200, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulse"
    )

    val borderModifier = when {
        isCurrent && isUnlocked -> Modifier.border(
            width = 1.5.dp,
            brush = Brush.horizontalGradient(listOf(GoldDark.copy(alpha = pulseAlpha), GoldPrimary.copy(alpha = pulseAlpha))),
            shape = RoundedCornerShape(14.dp)
        )
        isUnlocked -> Modifier.border(
            width = 1.dp,
            brush = Brush.horizontalGradient(listOf(GoldDark.copy(alpha = 0.4f), GoldPrimary.copy(alpha = 0.2f))),
            shape = RoundedCornerShape(14.dp)
        )
        else -> Modifier
    }

    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(14.dp))
            .then(borderModifier)
            .clickable(enabled = isUnlocked, onClick = onClick),
        shape = RoundedCornerShape(14.dp),
        color = if (isUnlocked) DarkCard else DarkSurfaceVariant.copy(alpha = 0.4f)
    ) {
        Column {
            Row(
                modifier = Modifier.padding(start = 16.dp, end = 16.dp, top = 14.dp, bottom = if (isUnlocked) 8.dp else 14.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = theme.emoji,
                    style = MaterialTheme.typography.headlineLarge,
                    modifier = Modifier.graphicsLayer {
                        alpha = if (isUnlocked) 1f else 0.4f
                    }
                )
                Spacer(Modifier.width(12.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = theme.displayName,
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.SemiBold),
                        color = if (isUnlocked) TextWhite else TextDimmed
                    )
                    Text(
                        text = if (isUnlocked) "$progress/10 affaires" else "\uD83D\uDD12 ${theme.casesRequiredToUnlock} affaires + ${theme.reputationRequiredToUnlock} rép.",
                        style = MaterialTheme.typography.bodySmall,
                        color = if (isUnlocked) TextGray else TextDimmed
                    )
                }
                if (!isUnlocked) {
                    Icon(Icons.Default.Lock, contentDescription = null, tint = TextDimmed, modifier = Modifier.size(18.dp))
                }
            }
            // Progress bar for unlocked themes
            if (isUnlocked) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(start = 16.dp, end = 16.dp, bottom = 12.dp)
                        .height(4.dp)
                        .clip(RoundedCornerShape(2.dp))
                        .background(DarkSurfaceVariant)
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth(fraction = progress / 10f)
                            .height(4.dp)
                            .clip(RoundedCornerShape(2.dp))
                            .background(
                                Brush.horizontalGradient(listOf(GoldDark, GoldPrimary, GoldLight))
                            )
                    )
                }
            }
        }
    }
}
