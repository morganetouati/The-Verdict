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
import androidx.compose.ui.graphics.drawscope.DrawScope
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

    // Body/Robe
    drawOval(
        color = costume.robeColor,
        topLeft = Offset(cx - w * 0.38f, h * 0.55f),
        size = Size(w * 0.76f, h * 0.5f)
    )

    // Collar
    drawOval(
        color = costume.collarColor,
        topLeft = Offset(cx - w * 0.15f, h * 0.50f),
        size = Size(w * 0.30f, h * 0.12f)
    )

    // Head (skin tone)
    drawCircle(
        color = Color(0xFFE0AC69),
        radius = w * 0.20f,
        center = Offset(cx, h * 0.35f)
    )

    // Judge wig
    drawArc(
        color = Color(0xFFDDDDDD),
        startAngle = 180f, sweepAngle = 180f, useCenter = true,
        topLeft = Offset(cx - w * 0.24f, h * 0.15f),
        size = Size(w * 0.48f, w * 0.30f)
    )
    // Wig curls
    drawCircle(Color(0xFFDDDDDD), w * 0.06f, Offset(cx - w * 0.22f, h * 0.38f))
    drawCircle(Color(0xFFDDDDDD), w * 0.06f, Offset(cx + w * 0.22f, h * 0.38f))

    // Eyes
    drawCircle(Color(0xFF2C1B0E), w * 0.03f, Offset(cx - w * 0.08f, h * 0.33f))
    drawCircle(Color(0xFF2C1B0E), w * 0.03f, Offset(cx + w * 0.08f, h * 0.33f))

    // Mouth
    drawLine(
        Color(0xFF2C1B0E),
        Offset(cx - w * 0.05f, h * 0.40f),
        Offset(cx + w * 0.05f, h * 0.40f),
        strokeWidth = w * 0.02f
    )

    // Gavel accent
    drawCircle(costume.accentColor, w * 0.04f, Offset(cx + w * 0.30f, h * 0.65f))
    drawRect(
        costume.accentColor,
        Offset(cx + w * 0.28f, h * 0.65f),
        Size(w * 0.10f, w * 0.03f)
    )
}
