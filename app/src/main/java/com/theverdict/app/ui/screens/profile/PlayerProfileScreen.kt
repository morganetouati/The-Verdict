package com.theverdict.app.ui.screens.profile

import androidx.compose.foundation.Canvas
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
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.compositeOver
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.theverdict.app.data.repository.PlayerRepository
import com.theverdict.app.domain.model.PlayerProfile
import com.theverdict.app.ui.theme.*
import kotlinx.coroutines.launch

data class JudgeCostume(
    val name: String,
    val robeColor: Color,
    val collarColor: Color,
    val accentColor: Color
)

val judgeCostumes = listOf(
    JudgeCostume("Classique", Color(0xFF1A1A1A), Color(0xFFFFFFFF), Color(0xFFD4A24C)),
    JudgeCostume("Royal", Color(0xFF1A0033), Color(0xFFE0C0FF), Color(0xFF9C27B0)),
    JudgeCostume("Écarlate", Color(0xFF330000), Color(0xFFFFCCCC), Color(0xFFE53935)),
    JudgeCostume("Émeraude", Color(0xFF001A0D), Color(0xFFC8E6C9), Color(0xFF4CAF50)),
    JudgeCostume("Doré", Color(0xFF2A1F00), Color(0xFFFFF8E1), Color(0xFFFFD700)),
    JudgeCostume("Glacier", Color(0xFF001033), Color(0xFFBBDEFB), Color(0xFF42A5F5))
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PlayerProfileScreen(
    playerRepository: PlayerRepository,
    onBack: () -> Unit
) {
    val profile by playerRepository.profile.collectAsState(initial = PlayerProfile())
    val scope = rememberCoroutineScope()
    val dim = LocalDimensions.current

    var pseudo by remember(profile.pseudo) { mutableStateOf(profile.pseudo) }
    var selectedCostume by remember(profile.costumeIndex) { mutableIntStateOf(profile.costumeIndex) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(DarkBackground, DarkSurface, DarkBackground)
                )
            )
    ) {
        TopAppBar(
            title = { Text("Profil du Juge", color = GoldPrimary) },
            navigationIcon = {
                IconButton(onClick = onBack) {
                    Icon(Icons.AutoMirrored.Filled.ArrowBack, "Retour", tint = TextWhite)
                }
            },
            colors = TopAppBarDefaults.topAppBarColors(containerColor = DarkBackground)
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(dim.paddingLarge),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Judge avatar preview
            val costume = judgeCostumes.getOrElse(selectedCostume) { judgeCostumes[0] }
            Box(
                modifier = Modifier
                    .size(140.dp)
                    .clip(CircleShape)
                    .background(DarkCard)
                    .border(2.dp, costume.accentColor, CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Canvas(modifier = Modifier.size(120.dp)) {
                    drawJudgeAvatar(costume)
                }
            }

            Spacer(Modifier.height(dim.paddingMedium))

            Text(
                text = profile.displayName,
                style = MaterialTheme.typography.headlineLarge,
                color = TextWhite,
                textAlign = TextAlign.Center
            )
            Text(
                text = profile.rank.displayName,
                style = MaterialTheme.typography.bodyMedium,
                color = GoldPrimary
            )

            Spacer(Modifier.height(dim.paddingLarge))

            // Pseudo input
            Text(
                text = "Votre pseudo",
                style = MaterialTheme.typography.titleMedium,
                color = TextWhite,
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(Modifier.height(dim.paddingSmall))

            OutlinedTextField(
                value = pseudo,
                onValueChange = { if (it.length <= 20) pseudo = it },
                placeholder = { Text("Entrez votre pseudo", color = TextDimmed) },
                singleLine = true,
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = GoldPrimary,
                    unfocusedBorderColor = CardBorder,
                    focusedTextColor = TextWhite,
                    unfocusedTextColor = TextWhite,
                    cursorColor = GoldPrimary
                )
            )

            Spacer(Modifier.height(dim.paddingLarge))

            // Costume selection
            Text(
                text = "Tenue du Juge",
                style = MaterialTheme.typography.titleMedium,
                color = TextWhite,
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(Modifier.height(dim.paddingSmall))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                judgeCostumes.take(3).forEachIndexed { index, c ->
                    CostumeCard(
                        costume = c,
                        isSelected = selectedCostume == index,
                        onClick = { selectedCostume = index },
                        modifier = Modifier.weight(1f)
                    )
                }
            }
            Spacer(Modifier.height(8.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                judgeCostumes.drop(3).forEachIndexed { index, c ->
                    CostumeCard(
                        costume = c,
                        isSelected = selectedCostume == index + 3,
                        onClick = { selectedCostume = index + 3 },
                        modifier = Modifier.weight(1f)
                    )
                }
            }

            Spacer(Modifier.height(dim.paddingLarge))

            // Save button
            Button(
                onClick = {
                    scope.launch {
                        val updated = profile.copy(
                            pseudo = pseudo.trim(),
                            costumeIndex = selectedCostume
                        )
                        playerRepository.updateProfileDirect(updated)
                        onBack()
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(dim.buttonHeight),
                colors = ButtonDefaults.buttonColors(containerColor = GoldPrimary),
                shape = RoundedCornerShape(16.dp)
            ) {
                Icon(Icons.Default.Check, contentDescription = null, tint = DarkBackground)
                Spacer(Modifier.width(8.dp))
                Text("ENREGISTRER", style = MaterialTheme.typography.titleLarge, color = DarkBackground)
            }

            Spacer(Modifier.height(dim.paddingLarge))

            // Stats summary
            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                color = DarkCard
            ) {
                Column(modifier = Modifier.padding(dim.paddingMedium)) {
                    Text("Statistiques", style = MaterialTheme.typography.titleMedium, color = GoldPrimary)
                    Spacer(Modifier.height(dim.paddingSmall))
                    StatRow("Affaires traitées", "${profile.casesPlayed}")
                    StatRow("Verdicts corrects", "${profile.correctVerdicts}")
                    StatRow("Verdicts erronés", "${profile.wrongVerdicts}")
                    StatRow("Taux de réussite", "${profile.successRate}%")
                    StatRow("Réputation", "${profile.reputation}/100")
                }
            }
        }
    }
}

@Composable
private fun CostumeCard(
    costume: JudgeCostume,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier
            .clip(RoundedCornerShape(12.dp))
            .border(
                width = if (isSelected) 2.dp else 1.dp,
                color = if (isSelected) costume.accentColor else CardBorder,
                shape = RoundedCornerShape(12.dp)
            )
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(12.dp),
        color = if (isSelected) costume.robeColor.copy(alpha = 0.3f) else DarkCard
    ) {
        Column(
            modifier = Modifier.padding(8.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Canvas(modifier = Modifier.size(48.dp)) {
                drawJudgeAvatar(costume)
            }
            Spacer(Modifier.height(4.dp))
            Text(
                text = costume.name,
                style = MaterialTheme.typography.bodySmall,
                color = if (isSelected) costume.accentColor else TextGray,
                textAlign = TextAlign.Center
            )
        }
    }
}

@Composable
private fun StatRow(label: String, value: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 2.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(text = label, style = MaterialTheme.typography.bodyMedium, color = TextGray)
        Text(text = value, style = MaterialTheme.typography.bodyMedium, color = TextWhite)
    }
}

private fun DrawScope.drawJudgeAvatar(costume: JudgeCostume) {
    val w = size.width
    val h = size.height
    val cx = w / 2f
    val skinColor = Color(0xFFE0AC69)
    val skinShadow = Color(0xFFCC9050)

    // Robe body with gradient
    drawOval(
        brush = Brush.verticalGradient(
            colors = listOf(costume.robeColor, Color.Black.copy(alpha = 0.2f).compositeOver(costume.robeColor)),
            startY = h * 0.55f, endY = h * 1.0f
        ),
        topLeft = Offset(cx - w * 0.38f, h * 0.55f),
        size = Size(w * 0.76f, h * 0.5f)
    )
    // Robe highlight
    drawOval(
        brush = Brush.verticalGradient(
            colors = listOf(Color.White.copy(alpha = 0.08f), Color.Transparent),
            startY = h * 0.55f, endY = h * 0.70f
        ),
        topLeft = Offset(cx - w * 0.3f, h * 0.56f),
        size = Size(w * 0.6f, h * 0.15f)
    )

    // Collar
    drawOval(costume.collarColor, Offset(cx - w * 0.18f, h * 0.48f), Size(w * 0.36f, h * 0.14f))
    drawOval(
        brush = Brush.verticalGradient(listOf(Color.Transparent, costume.robeColor.copy(alpha = 0.3f))),
        topLeft = Offset(cx - w * 0.14f, h * 0.52f), size = Size(w * 0.28f, h * 0.08f)
    )

    // Neck
    drawRect(skinColor, Offset(cx - w * 0.06f, h * 0.42f), Size(w * 0.12f, h * 0.12f))
    drawRect(
        brush = Brush.verticalGradient(listOf(skinShadow, Color.Transparent), startY = h * 0.42f, endY = h * 0.47f),
        topLeft = Offset(cx - w * 0.06f, h * 0.42f), size = Size(w * 0.12f, h * 0.05f)
    )

    // Ears
    val headR = w * 0.20f
    val earW = headR * 0.22f
    val earH = headR * 0.3f
    drawOval(skinColor, Offset(cx - headR - earW * 0.3f, h * 0.33f), Size(earW, earH))
    drawOval(skinShadow, Offset(cx - headR - earW * 0.1f, h * 0.34f), Size(earW * 0.5f, earH * 0.6f))
    drawOval(skinColor, Offset(cx + headR - earW * 0.7f, h * 0.33f), Size(earW, earH))
    drawOval(skinShadow, Offset(cx + headR - earW * 0.4f, h * 0.34f), Size(earW * 0.5f, earH * 0.6f))

    // Head
    drawCircle(skinColor, headR, Offset(cx, h * 0.35f))
    drawCircle(
        brush = Brush.radialGradient(
            colors = listOf(Color.White.copy(alpha = 0.12f), Color.Transparent),
            center = Offset(cx - headR * 0.25f, h * 0.28f), radius = headR * 0.7f
        ),
        radius = headR, center = Offset(cx, h * 0.35f)
    )
    drawCircle(
        brush = Brush.radialGradient(
            colors = listOf(Color.Transparent, Color.Black.copy(alpha = 0.1f)),
            center = Offset(cx, h * 0.45f), radius = headR * 0.9f
        ),
        radius = headR, center = Offset(cx, h * 0.35f)
    )
    drawCircle(Color.Black.copy(alpha = 0.15f), headR, Offset(cx, h * 0.35f), style = Stroke(w * 0.005f))

    // Judge wig — cascading curls
    drawArc(Color(0xFFEEEEEE), 180f, 180f, true, Offset(cx - w * 0.28f, h * 0.12f), Size(w * 0.56f, w * 0.38f))
    drawArc(Color(0xFFCCCCCC), 180f, 180f, true, Offset(cx - w * 0.26f, h * 0.20f), Size(w * 0.52f, w * 0.18f))
    drawArc(Color.White, 200f, 80f, true, Offset(cx - w * 0.16f, h * 0.13f), Size(w * 0.32f, w * 0.16f))
    // Side curls
    drawCircle(Color(0xFFEEEEEE), w * 0.07f, Offset(cx - w * 0.25f, h * 0.38f))
    drawCircle(Color(0xFFDDDDDD), w * 0.065f, Offset(cx - w * 0.23f, h * 0.46f))
    drawCircle(Color(0xFFEEEEEE), w * 0.058f, Offset(cx - w * 0.21f, h * 0.53f))
    drawCircle(Color(0xFFEEEEEE), w * 0.07f, Offset(cx + w * 0.25f, h * 0.38f))
    drawCircle(Color(0xFFDDDDDD), w * 0.065f, Offset(cx + w * 0.23f, h * 0.46f))
    drawCircle(Color(0xFFEEEEEE), w * 0.058f, Offset(cx + w * 0.21f, h * 0.53f))

    // Eyes — larger with highlights
    val eyeY = h * 0.33f
    val eyeSp = w * 0.08f
    val jEyeW = w * 0.08f
    val jEyeH = w * 0.07f
    drawOval(Color.White, Offset(cx - eyeSp - jEyeW / 2, eyeY - jEyeH / 2), Size(jEyeW, jEyeH))
    drawOval(Color.White, Offset(cx + eyeSp - jEyeW / 2, eyeY - jEyeH / 2), Size(jEyeW, jEyeH))
    drawCircle(Color(0xFF5D4037), w * 0.025f, Offset(cx - eyeSp, eyeY))
    drawCircle(Color(0xFF5D4037), w * 0.025f, Offset(cx + eyeSp, eyeY))
    drawCircle(Color(0xFF1A1A1A), w * 0.015f, Offset(cx - eyeSp, eyeY))
    drawCircle(Color(0xFF1A1A1A), w * 0.015f, Offset(cx + eyeSp, eyeY))
    drawCircle(Color.White, w * 0.008f, Offset(cx - eyeSp + w * 0.01f, eyeY - w * 0.01f))
    drawCircle(Color.White, w * 0.008f, Offset(cx + eyeSp + w * 0.01f, eyeY - w * 0.01f))

    // Nose
    drawCircle(skinShadow, w * 0.013f, Offset(cx, h * 0.375f))

    // Mouth — subtle smile
    drawArc(Color(0xFF2C1B0E), 0f, 180f, false,
        Offset(cx - w * 0.04f, h * 0.40f), Size(w * 0.08f, w * 0.04f))

    // Cheek blush
    drawCircle(Color(0xFFFF9999).copy(alpha = 0.08f), w * 0.03f, Offset(cx - w * 0.12f, h * 0.38f))
    drawCircle(Color(0xFFFF9999).copy(alpha = 0.08f), w * 0.03f, Offset(cx + w * 0.12f, h * 0.38f))

    // Gavel accent
    drawCircle(costume.accentColor, w * 0.04f, Offset(cx + w * 0.30f, h * 0.65f))
    drawRect(costume.accentColor, Offset(cx + w * 0.28f, h * 0.65f), Size(w * 0.10f, w * 0.03f))
}
