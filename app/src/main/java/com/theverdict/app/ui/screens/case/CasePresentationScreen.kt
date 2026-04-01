package com.theverdict.app.ui.screens.case

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
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
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.theverdict.app.data.repository.CaseRepository
import com.theverdict.app.domain.model.CaseTheme
import com.theverdict.app.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CasePresentationScreen(
    caseRepository: CaseRepository,
    themeIndex: Int,
    caseIndex: Int,
    onSeeSuspects: () -> Unit,
    onBack: () -> Unit
) {
    val theme = CaseTheme.entries[themeIndex]
    val case = caseRepository.getCase(theme, caseIndex)
    val dim = LocalDimensions.current

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
                    "Affaire ${caseIndex + 1}/10",
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
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(dim.paddingLarge),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Theme badge pill
                Surface(
                    shape = RoundedCornerShape(20.dp),
                    color = GoldPrimary.copy(alpha = 0.10f)
                ) {
                    Text(
                        text = "${theme.emoji} ${theme.displayName}",
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.SemiBold),
                        color = GoldPrimary,
                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 6.dp)
                    )
                }
                Spacer(Modifier.height(8.dp))

                // Difficulty
                Text(
                    text = "Difficulté : ${"⭐".repeat(case.difficulte)}",
                    style = MaterialTheme.typography.bodyMedium,
                    color = TextGray
                )

                Spacer(Modifier.height(24.dp))

                // Case title with shadow
                Text(
                    text = case.titre,
                    style = MaterialTheme.typography.displayMedium.copy(
                        fontWeight = FontWeight.Bold,
                        shadow = Shadow(color = GoldPrimary.copy(alpha = 0.3f), offset = Offset(0f, 3f), blurRadius = 12f)
                    ),
                    color = TextWhite,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(Modifier.height(24.dp))

                // Case description
                Surface(
                    modifier = Modifier
                        .fillMaxWidth()
                        .border(
                            width = 1.dp,
                            brush = Brush.verticalGradient(listOf(GoldDark.copy(alpha = 0.5f), GoldPrimary.copy(alpha = 0.2f))),
                            shape = RoundedCornerShape(16.dp)
                        ),
                    shape = RoundedCornerShape(16.dp),
                    color = DarkCard
                ) {
                    Text(
                        text = case.texte,
                        style = MaterialTheme.typography.bodyLarge,
                        color = TextGray,
                        modifier = Modifier.padding(20.dp)
                    )
                }

                Spacer(Modifier.height(16.dp))

                Text(
                    text = "${case.suspects.size} suspect(s) à interroger",
                    style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.SemiBold),
                    color = GoldLight
                )

                Spacer(Modifier.weight(1f))

                // CTA — gold gradient button
                val interactionSource = remember { MutableInteractionSource() }
                val isPressed by interactionSource.collectIsPressedAsState()
                val scale by animateFloatAsState(
                    targetValue = if (isPressed) 0.96f else 1f,
                    animationSpec = tween(100), label = "caseBtn"
                )

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .graphicsLayer { scaleX = scale; scaleY = scale }
                        .drawBehind {
                            drawRoundRect(
                                brush = Brush.verticalGradient(listOf(Color(0x40D4A24C), Color.Transparent)),
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
                            .background(Brush.horizontalGradient(listOf(GoldDark, GoldPrimary, GoldLight)))
                            .clickable(interactionSource = interactionSource, indication = null) { onSeeSuspects() },
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            "VOIR LES SUSPECTS",
                            style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.ExtraBold, letterSpacing = 1.sp),
                            color = DarkBackground
                        )
                    }
                }
            }
        } else {
            Text(
                text = "Affaire introuvable",
                style = MaterialTheme.typography.bodyLarge,
                color = VerdictWrong,
                modifier = Modifier.padding(24.dp)
            )
        }
    }
}
