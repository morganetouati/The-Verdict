package com.theverdict.app.ui.screens.profile

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.LocalFireDepartment
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.theverdict.app.domain.model.PlayerLevel
import com.theverdict.app.ui.screens.home.HomeViewModel
import com.theverdict.app.ui.theme.*

private val AVATAR_OPTIONS = listOf(
    "🕵️", "👤", "🦊", "🐺", "🦉", "🎭",
    "👁️", "🧠", "🔍", "💀", "⚖️", "🗝️",
    "🐱", "🦅", "🐍", "👑", "🎩", "🌙"
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(
    viewModel: HomeViewModel,
    onBack: () -> Unit
) {
    val state by viewModel.uiState.collectAsState()
    val profile = state.profile

    var isEditingName by remember { mutableStateOf(false) }
    var editedName by remember { mutableStateOf(profile.displayName) }
    var showAvatarPicker by remember { mutableStateOf(false) }

    // Sync editedName when profile loads
    LaunchedEffect(profile.displayName) {
        editedName = profile.displayName
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Profil",
                        style = MaterialTheme.typography.headlineSmall,
                        color = Gold
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Retour",
                            tint = Gold
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Surface
                )
            )
        },
        containerColor = NoirDeep
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .verticalScroll(rememberScrollState())
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // -- Avatar (clickable to change) --
            Box(
                modifier = Modifier
                    .size(96.dp)
                    .background(
                        Brush.linearGradient(listOf(Gold, GoldDark)),
                        CircleShape
                    )
                    .clickable { showAvatarPicker = !showAvatarPicker },
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = profile.avatarUrl.ifBlank { "🕵️" },
                    style = MaterialTheme.typography.displayMedium
                )
            }

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = "Touchez pour changer",
                style = MaterialTheme.typography.labelSmall,
                color = TextSecondary
            )

            // -- Avatar picker grid --
            if (showAvatarPicker) {
                Spacer(modifier = Modifier.height(12.dp))
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = Surface)
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Text(
                            text = "Choisir un avatar",
                            style = MaterialTheme.typography.titleSmall,
                            color = Gold,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        LazyVerticalGrid(
                            columns = GridCells.Fixed(6),
                            modifier = Modifier.height(120.dp),
                            horizontalArrangement = Arrangement.spacedBy(4.dp),
                            verticalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            items(AVATAR_OPTIONS) { emoji ->
                                val isSelected = emoji == profile.avatarUrl
                                Box(
                                    modifier = Modifier
                                        .size(48.dp)
                                        .clip(CircleShape)
                                        .background(
                                            if (isSelected) Gold.copy(alpha = 0.3f)
                                            else SurfaceLight,
                                            CircleShape
                                        )
                                        .then(
                                            if (isSelected) Modifier.border(2.dp, Gold, CircleShape)
                                            else Modifier
                                        )
                                        .clickable {
                                            viewModel.updateAvatar(emoji)
                                            showAvatarPicker = false
                                        },
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = emoji,
                                        style = MaterialTheme.typography.titleLarge
                                    )
                                }
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // -- Editable pseudo --
            if (isEditingName) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    OutlinedTextField(
                        value = editedName,
                        onValueChange = { if (it.length <= 20) editedName = it },
                        singleLine = true,
                        modifier = Modifier.width(200.dp),
                        textStyle = MaterialTheme.typography.titleMedium.copy(color = TextPrimary),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = Gold,
                            unfocusedBorderColor = TextSecondary,
                            cursorColor = Gold
                        ),
                        placeholder = {
                            Text("Votre pseudo", color = TextSecondary)
                        }
                    )
                    IconButton(
                        onClick = {
                            viewModel.updateDisplayName(editedName)
                            isEditingName = false
                        }
                    ) {
                        Icon(
                            imageVector = Icons.Default.Check,
                            contentDescription = "Valider",
                            tint = GreenTruth
                        )
                    }
                }
            } else {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.clickable { isEditingName = true }
                ) {
                    Text(
                        text = profile.displayName,
                        style = MaterialTheme.typography.headlineMedium,
                        color = TextPrimary
                    )
                    Icon(
                        imageVector = Icons.Default.Edit,
                        contentDescription = "Modifier le pseudo",
                        tint = TextSecondary,
                        modifier = Modifier.size(18.dp)
                    )
                }
            }

            Text(
                text = profile.level.name,
                style = MaterialTheme.typography.titleMedium,
                color = Gold
            )

            Spacer(modifier = Modifier.height(8.dp))

            // XP progress
            val currentLevel = profile.level
            val nextLevel = PlayerLevel.entries.let { levels ->
                val idx = levels.indexOf(currentLevel)
                if (idx < levels.size - 1) levels[idx + 1] else null
            }

            if (nextLevel != null) {
                val xpProgress = ((profile.totalXp - currentLevel.minXp).toFloat() /
                        (nextLevel.minXp - currentLevel.minXp)).coerceIn(0f, 1f)

                LinearProgressIndicator(
                    progress = { xpProgress },
                    modifier = Modifier
                        .fillMaxWidth(0.6f)
                        .height(8.dp)
                        .clip(RoundedCornerShape(4.dp)),
                    color = Gold,
                    trackColor = SurfaceLight,
                )

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = "${profile.totalXp} / ${nextLevel.minXp} XP",
                    style = MaterialTheme.typography.labelSmall,
                    color = TextSecondary
                )
            } else {
                Text(
                    text = "${profile.totalXp} XP — Rang maximum !",
                    style = MaterialTheme.typography.labelMedium,
                    color = Gold
                )
            }

            Spacer(modifier = Modifier.height(32.dp))

            // -- Stats grid --
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                ProfileStat(
                    icon = Icons.Default.EmojiEvents,
                    value = "${profile.gamesPlayed}",
                    label = "Parties"
                )
                ProfileStat(
                    icon = Icons.Default.Star,
                    value = "${profile.correctVerdicts}",
                    label = "Justes"
                )
                ProfileStat(
                    icon = Icons.Default.LocalFireDepartment,
                    value = "${profile.currentStreak}",
                    label = "Série"
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            // -- Detailed stats card --
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = Surface)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "Statistiques",
                        style = MaterialTheme.typography.titleMedium,
                        color = Gold,
                        fontWeight = FontWeight.Bold
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    StatRow("Parties jouées", "${profile.gamesPlayed}")
                    StatRow("Verdicts corrects", "${profile.correctVerdicts}")
                    StatRow(
                        "Taux de réussite",
                        if (profile.gamesPlayed > 0)
                            "${(profile.correctVerdicts * 100 / profile.gamesPlayed)}%"
                        else "—"
                    )
                    StatRow("Série actuelle", "${profile.currentStreak}")
                    StatRow("Meilleure série", "${profile.bestStreak}")
                    StatRow("XP total", "${profile.totalXp}")
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // -- Ranks overview --
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = Surface)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "Rangs",
                        style = MaterialTheme.typography.titleMedium,
                        color = Gold,
                        fontWeight = FontWeight.Bold
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    PlayerLevel.entries.forEach { level ->
                        val isCurrentOrPast = profile.totalXp >= level.minXp
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 4.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = if (level == profile.level) "▸ " else "  ",
                                style = MaterialTheme.typography.bodyMedium,
                                color = Gold
                            )
                            Text(
                                text = level.name,
                                style = MaterialTheme.typography.bodyMedium,
                                color = if (isCurrentOrPast) Gold else TextSecondary,
                                fontWeight = if (level == profile.level) FontWeight.Bold else FontWeight.Normal
                            )
                            Spacer(modifier = Modifier.weight(1f))
                            Text(
                                text = "${level.minXp} XP",
                                style = MaterialTheme.typography.labelSmall,
                                color = TextSecondary
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

@Composable
private fun ProfileStat(
    icon: ImageVector,
    value: String,
    label: String
) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Icon(
            imageVector = icon,
            contentDescription = label,
            tint = Gold,
            modifier = Modifier.size(28.dp)
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = value,
            style = MaterialTheme.typography.labelLarge,
            color = TextPrimary
        )
        Text(
            text = label,
            style = MaterialTheme.typography.bodySmall,
            color = TextSecondary
        )
    }
}

@Composable
private fun StatRow(label: String, value: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodyMedium,
            color = TextSecondary
        )
        Text(
            text = value,
            style = MaterialTheme.typography.labelMedium,
            color = TextPrimary
        )
    }
}
