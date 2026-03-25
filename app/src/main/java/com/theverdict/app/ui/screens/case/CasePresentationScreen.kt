package com.theverdict.app.ui.screens.case

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
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
import androidx.compose.ui.unit.dp
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

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(DarkBackground)
    ) {
        TopAppBar(
            title = { Text("Affaire ${caseIndex + 1}/10") },
            navigationIcon = {
                IconButton(onClick = onBack) {
                    Icon(Icons.AutoMirrored.Filled.ArrowBack, "Retour", tint = TextWhite)
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
                    .padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Theme badge
                Text(
                    text = "${theme.emoji} ${theme.displayName}",
                    style = MaterialTheme.typography.titleMedium,
                    color = GoldPrimary
                )
                Spacer(Modifier.height(8.dp))

                // Difficulty
                Text(
                    text = "Difficulté : ${"⭐".repeat(case.difficulte)}",
                    style = MaterialTheme.typography.bodyMedium,
                    color = TextGray
                )

                Spacer(Modifier.height(24.dp))

                // Case title
                Text(
                    text = case.titre,
                    style = MaterialTheme.typography.displayMedium,
                    color = TextWhite
                )

                Spacer(Modifier.height(24.dp))

                // Case description
                Surface(
                    modifier = Modifier.fillMaxWidth(),
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
                    style = MaterialTheme.typography.bodyMedium,
                    color = TextGray
                )

                Spacer(Modifier.weight(1f))

                // CTA
                Button(
                    onClick = onSeeSuspects,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = GoldPrimary),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Text("VOIR LES SUSPECTS", style = MaterialTheme.typography.titleLarge, color = DarkBackground)
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
