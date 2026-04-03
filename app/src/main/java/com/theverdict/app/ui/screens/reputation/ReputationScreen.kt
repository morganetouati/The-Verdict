package com.theverdict.app.ui.screens.reputation

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shadow
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.theverdict.app.data.repository.PlayerRepository
import com.theverdict.app.domain.model.PlayerProfile
import com.theverdict.app.ui.components.RankBadge
import com.theverdict.app.ui.components.ReputationBar
import com.theverdict.app.ui.theme.*
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ReputationScreen(
    playerRepository: PlayerRepository,
    onBack: () -> Unit
) {
    val profile by playerRepository.profile.collectAsState(initial = PlayerProfile())

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        "Réputation",
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                        color = GoldLight
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, "Retour", tint = GoldPrimary)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.Transparent)
            )
        },
        containerColor = Color.Transparent
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        colors = listOf(DarkBackground, Color(0xFF111111), DarkSurface, DarkBackground)
                    )
                )
                .padding(padding)
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(Modifier.height(16.dp))

            RankBadge(rank = profile.rank)

            Spacer(Modifier.height(24.dp))

            // Animated success rate circle
            val rateTarget = profile.successRate.toFloat()
            val animatedRate = remember { Animatable(0f) }
            LaunchedEffect(rateTarget) {
                animatedRate.animateTo(rateTarget, tween(1200))
            }

            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier.size(120.dp)
            ) {
                Canvas(modifier = Modifier.size(120.dp)) {
                    val strokeW = 8.dp.toPx()
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
                    // Animated progress ring
                    if (animatedRate.value > 0f) {
                        drawArc(
                            brush = Brush.sweepGradient(listOf(GoldDark, GoldPrimary, GoldLight)),
                            startAngle = -90f,
                            sweepAngle = 360f * animatedRate.value / 100f,
                            useCenter = false,
                            topLeft = arcOffset,
                            size = arcSize,
                            style = Stroke(strokeW, cap = StrokeCap.Round)
                        )
                    }
                }
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = "${animatedRate.value.toInt()}%",
                        style = MaterialTheme.typography.headlineMedium.copy(
                            fontWeight = FontWeight.Bold,
                            shadow = Shadow(color = GoldPrimary.copy(alpha = 0.4f), offset = Offset(0f, 2f), blurRadius = 8f)
                        ),
                        color = GoldPrimary
                    )
                    Text(
                        text = "Réussite",
                        style = MaterialTheme.typography.bodySmall,
                        color = TextGray
                    )
                }
            }

            Spacer(Modifier.height(16.dp))

            // Animated reputation bar
            val repTarget = profile.reputation.toFloat()
            val animatedRep = remember { Animatable(0f) }
            LaunchedEffect(repTarget) {
                animatedRep.animateTo(repTarget, tween(1000))
            }

            ReputationBar(
                reputation = animatedRep.value.toInt(),
                rank = profile.rank,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(Modifier.height(40.dp))

            // Stats cards with gold border
            val statsAlpha = remember { List(5) { Animatable(0f) } }
            val statsOffsetY = remember { List(5) { Animatable(20f) } }

            LaunchedEffect(Unit) {
                statsAlpha.forEachIndexed { index, anim ->
                    launch {
                        delay(index * 80L)
                        anim.animateTo(1f, tween(400))
                    }
                }
                statsOffsetY.forEachIndexed { index, anim ->
                    launch {
                        delay(index * 80L)
                        anim.animateTo(0f, tween(400))
                    }
                }
            }

            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .border(
                        width = 1.dp,
                        brush = Brush.verticalGradient(listOf(GoldDark.copy(alpha = 0.4f), GoldPrimary.copy(alpha = 0.2f))),
                        shape = RoundedCornerShape(16.dp)
                    ),
                colors = CardDefaults.cardColors(containerColor = DarkCard),
                shape = RoundedCornerShape(16.dp)
            ) {
                Column(modifier = Modifier.padding(20.dp)) {
                    Text(
                        text = "Statistiques",
                        style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                        color = GoldPrimary
                    )
                    Spacer(Modifier.height(16.dp))

                    val statLabels = listOf(
                        "Affaires traitées" to "${profile.casesPlayed}",
                        "Verdicts corrects" to "${profile.correctVerdicts}",
                        "Verdicts erronés" to "${profile.wrongVerdicts}",
                        "Taux de réussite" to "${profile.successRate}%",
                        "Progression" to "${profile.completedCaseIds.size}/80"
                    )
                    statLabels.forEachIndexed { index, (label, value) ->
                        StatRow(
                            label = label,
                            value = value,
                            modifier = Modifier.graphicsLayer {
                                alpha = statsAlpha[index].value
                                translationY = statsOffsetY[index].value
                            }
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun StatRow(label: String, value: String, modifier: Modifier = Modifier) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(text = label, style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Medium), color = TextGray)
        Text(text = value, style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.SemiBold), color = GoldLight)
    }
}
