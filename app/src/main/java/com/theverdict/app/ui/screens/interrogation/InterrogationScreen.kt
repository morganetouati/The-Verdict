package com.theverdict.app.ui.screens.interrogation

import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shadow
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.theverdict.app.data.repository.CaseRepository
import com.theverdict.app.domain.model.CaseTheme
import com.theverdict.app.ui.components.InteractiveAvatar
import com.theverdict.app.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun InterrogationScreen(
    caseRepository: CaseRepository,
    themeIndex: Int,
    caseIndex: Int,
    suspectId: Int,
    onBack: () -> Unit
) {
    val theme = CaseTheme.entries[themeIndex]
    val case = caseRepository.getCase(theme, caseIndex)
    val suspect = case?.suspects?.find { it.id == suspectId }
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
                    .padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Name with shadow
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
                    color = TextWhite
                )

                Spacer(Modifier.height(8.dp))

                Text(
                    text = "Cliquez sur les zones pour chercher des indices",
                    style = MaterialTheme.typography.bodySmall,
                    color = GoldLight.copy(alpha = 0.6f)
                )

                Spacer(Modifier.height(16.dp))

                // Interactive avatar with clickable zones
                InteractiveAvatar(
                    config = suspect.avatar,
                    suspectClues = suspect.indices,
                    size = dim.avatarSize,
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(Modifier.height(24.dp))

                // Statement with gold border
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
                            color = TextWhite
                        )
                    }
                }
            }
        }
    }
}
