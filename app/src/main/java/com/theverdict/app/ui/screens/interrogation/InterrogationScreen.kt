package com.theverdict.app.ui.screens.interrogation

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shadow
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.theverdict.app.data.repository.CaseRepository
import com.theverdict.app.data.repository.PlayerRepository
import com.theverdict.app.domain.model.CaseTheme
import com.theverdict.app.domain.model.Clue
import com.theverdict.app.domain.model.PlayerProfile
import com.theverdict.app.ui.components.InteractiveAvatar
import com.theverdict.app.ui.components.ErrorState
import com.theverdict.app.ui.components.PressureBar
import com.theverdict.app.ui.theme.*
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun InterrogationScreen(
    caseRepository: CaseRepository,
    playerRepository: PlayerRepository,
    themeIndex: Int,
    caseIndex: Int,
    suspectId: Int,
    onBack: () -> Unit
) {
    val theme = CaseTheme.entries[themeIndex]
    val case = caseRepository.getCase(theme, caseIndex)
    val suspect = case?.suspects?.find { it.id == suspectId }
    val dim = LocalDimensions.current
    val profile by playerRepository.profile.collectAsState(initial = PlayerProfile())
    val scope = rememberCoroutineScope()

    val initialDiscoveredClues = remember(suspectId) {
        caseRepository.getDiscoveredClues(suspectId)
    }
    var hintClue by remember { mutableStateOf<Clue?>(null) }
    var hintUsed by remember { mutableStateOf(false) }
    var showHintDialog by remember { mutableStateOf(false) }
    var showCaseSheet by remember { mutableStateOf(false) }
    var showPressureConsequenceDialog by remember { mutableStateOf(false) }
    val sheetState = rememberModalBottomSheetState()
    val pressure by caseRepository.pressureLevel.collectAsState()

    // Track clue count for hesitation detection
    var lastClueCount by remember { mutableIntStateOf(initialDiscoveredClues.size) }

    // Pressure: +10 first visit, +5 re-visit; then mark as interrogated
    LaunchedEffect(suspectId) {
        val isFirstVisit = !caseRepository.isInterrogated(suspectId)
        caseRepository.markSuspectInterrogated(suspectId)
        caseRepository.addPressure(if (isFirstVisit) 10 else 5)
    }

    // Hesitation timer: +10 every 30s without finding a new clue
    LaunchedEffect(suspectId) {
        while (true) {
            delay(30_000L)
            val currentCount = caseRepository.getDiscoveredClueCount(suspectId)
            if (currentCount == lastClueCount) {
                caseRepository.addPressure(10)
            }
            lastClueCount = currentCount
        }
    }

    // Consequences when pressure reaches max — reset immediately to prevent re-triggering
    LaunchedEffect(pressure) {
        if (pressure >= 100 && !showPressureConsequenceDialog) {
            caseRepository.resetPressure()
            showPressureConsequenceDialog = true
            // Apply consequences: -10 reputation
            val current = profile.copy(reputation = (profile.reputation - 10).coerceAtLeast(0))
            playerRepository.updateProfileDirect(current)
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(DarkBackground, DarkMid, DarkSurface, DarkBackground)
                )
            )
    ) {
        TopAppBar(
            title = {
                Text(
                    "Interrogatoire",
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                    color = GoldLight
                )
            },
            navigationIcon = {
                IconButton(onClick = onBack) {
                    Icon(Icons.AutoMirrored.Filled.ArrowBack, "Retour", tint = GoldPrimary)
                }
            },
            actions = {
                IconButton(onClick = { showCaseSheet = true }) {
                    Icon(Icons.Default.Info, contentDescription = "Voir l'affaire", tint = GoldPrimary)
                }
            },
            colors = TopAppBarDefaults.topAppBarColors(
                containerColor = Color.Transparent,
                titleContentColor = GoldLight
            )
        )

        if (suspect != null) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(24.dp)
                    .navigationBarsPadding(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = suspect.nom,
                    style = MaterialTheme.typography.headlineLarge.copy(
                        fontWeight = FontWeight.Bold,
                        shadow = Shadow(
                            color = GoldPrimary.copy(alpha = 0.3f),
                            offset = Offset(0f, 2f),
                            blurRadius = 8f
                        )
                    ),
                    color = TextWhite,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )

                Spacer(Modifier.height(8.dp))

                Text(
                    text = "Appuyez sur les zones pour chercher des indices",
                    style = MaterialTheme.typography.bodySmall,
                    color = GoldLight.copy(alpha = 0.7f)
                )

                Spacer(Modifier.height(16.dp))

                InteractiveAvatar(
                    config = suspect.avatar,
                    suspectClues = suspect.indices,
                    size = dim.avatarSize,
                    initialDiscoveredClues = initialDiscoveredClues,
                    hintClue = hintClue,
                    pressureLevel = pressure,
                    onClueDiscovered = { clue ->
                        caseRepository.addDiscoveredClue(suspectId, clue)
                        lastClueCount = caseRepository.getDiscoveredClueCount(suspectId)
                    },
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(Modifier.height(12.dp))

                PressureBar(
                    pressure = pressure,
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(Modifier.height(12.dp))

                // Hint button (one per interrogation session)
                if (!hintUsed) {
                    TextButton(
                        onClick = { showHintDialog = true },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            "💡 Révéler un indice (−5 réputation)",
                            color = GoldLight.copy(alpha = 0.6f),
                            style = MaterialTheme.typography.bodySmall
                        )
                    }
                    Spacer(Modifier.height(8.dp))
                }

                // Statement card
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
                    Column(modifier = Modifier.padding(20.dp)) {
                        Text(
                            text = "Déclaration",
                            style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Bold),
                            color = GoldPrimary
                        )
                        Spacer(Modifier.height(8.dp))
                        Text(
                            text = "« ${suspect.phrase} »",
                            style = MaterialTheme.typography.bodyLarge,
                            color = TextWhite,
                            maxLines = 8,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                }
            }
        } else {
            ErrorState(message = "Suspect introuvable", onBack = onBack)
        }
    }

    // Pressure max consequence dialog
    if (showPressureConsequenceDialog) {
        AlertDialog(
            onDismissRequest = { showPressureConsequenceDialog = false },
            title = { Text("⚠️ Interrogatoire compromis", color = PressureCritical) },
            text = {
                Text(
                    "La pression est trop forte !\n\n• −10 points de réputation\n• Risque d'indices manqués\n• Erreur de jugement possible",
                    color = TextGray
                )
            },
            confirmButton = {
                Button(onClick = { showPressureConsequenceDialog = false }) { Text("Continuer") }
            },
            containerColor = DarkCard
        )
    }

    // Hint confirmation dialog
    if (showHintDialog && suspect != null) {
        AlertDialog(
            onDismissRequest = { showHintDialog = false },
            title = { Text("💡 Révéler un indice", color = TextWhite) },
            text = { Text("Cela coûte 5 points de réputation. Continuer ?", color = TextGray) },
            confirmButton = {
                Button(onClick = {
                    showHintDialog = false
                    val discovered = caseRepository.getDiscoveredClues(suspectId).toSet()
                    val undiscovered = suspect.indices.filter { it !in discovered }
                    hintUsed = true
                    caseRepository.addPressure(20)
                    if (undiscovered.isNotEmpty()) {
                        hintClue = undiscovered.random()
                        scope.launch {
                            val updated = profile.copy(
                                reputation = (profile.reputation - 5).coerceAtLeast(0)
                            )
                            playerRepository.updateProfileDirect(updated)
                        }
                    }
                }) { Text("Confirmer") }
            },
            dismissButton = {
                TextButton(onClick = { showHintDialog = false }) { Text("Annuler") }
            },
            containerColor = DarkCard
        )
    }

    // Case summary bottom sheet
    if (showCaseSheet && case != null) {
        ModalBottomSheet(
            onDismissRequest = { showCaseSheet = false },
            sheetState = sheetState,
            containerColor = DarkCard
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(24.dp)
                    .verticalScroll(rememberScrollState())
                    .navigationBarsPadding()
            ) {
                Text(
                    text = "📋 Résumé de l'affaire",
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                    color = GoldPrimary
                )
                Spacer(Modifier.height(12.dp))
                Text(
                    text = case.texte,
                    style = MaterialTheme.typography.bodyMedium,
                    color = TextWhite
                )
                Spacer(Modifier.height(16.dp))
            }
        }
    }
}
