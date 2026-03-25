package com.theverdict.app.ui.screens.verdict

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
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
import androidx.compose.ui.unit.dp
import com.theverdict.app.data.repository.CaseRepository
import com.theverdict.app.data.repository.PlayerRepository
import com.theverdict.app.domain.model.CaseTheme
import com.theverdict.app.domain.model.PlayerProfile
import com.theverdict.app.domain.model.Suspect
import com.theverdict.app.ui.components.SuspectAvatar
import com.theverdict.app.ui.components.TimerBar
import com.theverdict.app.ui.theme.*
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
    var nobodySelected by remember { mutableIntStateOf(0) } // 0=not selected, 1=nobody, 2=everyone

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
            .background(DarkBackground)
    ) {
        TopAppBar(
            title = {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.Gavel, contentDescription = null, tint = GoldPrimary)
                    Spacer(Modifier.width(8.dp))
                    Text("Votre Verdict")
                }
            },
            colors = TopAppBarDefaults.topAppBarColors(
                containerColor = DarkBackground,
                titleContentColor = TextWhite
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
                    style = MaterialTheme.typography.headlineMedium,
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

                // Confirm button
                val hasSelection = selectedIds.isNotEmpty() || nobodySelected == 1
                Button(
                    onClick = {
                        scope.launch {
                            val liarIds = if (nobodySelected == 1) emptyList() else selectedIds.toList()
                            val result = playerRepository.applyVerdict(profile, case, liarIds)
                            onResult(result.isCorrect, result.pointsChange)
                        }
                    },
                    enabled = hasSelection,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = GoldPrimary,
                        disabledContainerColor = DarkSurfaceVariant
                    ),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Text(
                        "CONFIRMER LE VERDICT",
                        style = MaterialTheme.typography.titleLarge,
                        color = if (hasSelection) DarkBackground else TextDimmed
                    )
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
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .then(
                if (isSelected) Modifier.border(2.dp, VerdictWrong, RoundedCornerShape(16.dp))
                else Modifier
            ),
        shape = RoundedCornerShape(16.dp),
        color = if (isSelected) VerdictWrong.copy(alpha = 0.1f) else DarkCard
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            SuspectAvatar(config = suspect.avatar, clues = suspect.indices, size = 48.dp)
            Spacer(Modifier.width(12.dp))
            Text(
                text = suspect.nom,
                style = MaterialTheme.typography.titleMedium,
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
                else Modifier
            ),
        shape = RoundedCornerShape(16.dp),
        color = if (isSelected) GoldPrimary.copy(alpha = 0.1f) else DarkSurfaceVariant
    ) {
        Text(
            text = text,
            style = MaterialTheme.typography.titleMedium,
            color = if (isSelected) GoldPrimary else TextGray,
            modifier = Modifier.padding(16.dp)
        )
    }
}
