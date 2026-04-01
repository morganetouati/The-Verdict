package com.theverdict.app.ui.screens.tutorial

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
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shadow
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.theverdict.app.ui.theme.*
import kotlinx.coroutines.launch

private data class TutorialPage(
    val emoji: String,
    val title: String,
    val lines: List<String>
)

private val pages = listOf(
    TutorialPage(
        emoji = "⚖️",
        title = "Bienvenue, Juge !",
        lines = listOf(
            "Vous êtes un juge d'instruction.",
            "Votre mission : interroger les suspects et trouver qui ment.",
            "Chaque affaire présente plusieurs suspects. Un seul (ou parfois plusieurs) dit la vérité… ou ment !",
            "Analysez les indices, écoutez les déclarations, et rendez votre verdict."
        )
    ),
    TutorialPage(
        emoji = "🔍",
        title = "Interrogez les suspects",
        lines = listOf(
            "Pendant l'interrogatoire, un avatar représente le suspect.",
            "Cliquez sur les différentes zones du corps (front, yeux, bouche, mains…) pour chercher des indices.",
            "✅ Si un indice est trouvé → la zone devient verte et l'indice est révélé.",
            "❌ Si rien n'est trouvé → la zone clignote en rouge.",
            "Le bouton « Analyser l'attitude » détecte les indices comportementaux (contradictions, hésitations…)."
        )
    ),
    TutorialPage(
        emoji = "🎯",
        title = "Rendez votre verdict",
        lines = listOf(
            "Après avoir interrogé les suspects, choisissez qui vous pensez être le menteur.",
            "Dans les thèmes avancés, des cas spéciaux apparaissent :",
            "• Aucun menteur — Tout le monde dit la vérité",
            "• Deux menteurs — Deux suspects mentent",
            "• Tous mentent — Personne ne dit la vérité",
            "Certains thèmes ajoutent un chronomètre — décidez vite !"
        )
    ),
    TutorialPage(
        emoji = "📊",
        title = "Votre réputation",
        lines = listOf(
            "Chaque verdict correct vous fait gagner des points de réputation.",
            "Chaque erreur en fait perdre (plus durement dans les thèmes difficiles).",
            "Votre rang évolue avec votre réputation :",
            "📖 Débutant (0-20) → ⚖️ Juge (21-40) → 🏛️ Bon Juge (41-60) → 🎯 Expert (61-80) → 👑 Légende (81-100)",
            "Débloquez de nouveaux thèmes en progressant !",
            "Si votre réputation tombe à 0 → Game Over. Atteignez 80 affaires résolues pour la victoire !"
        )
    )
)

@Composable
fun TutorialScreen(
    onFinish: () -> Unit
) {
    val pagerState = rememberPagerState(pageCount = { pages.size })
    val scope = rememberCoroutineScope()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(DarkBackground, Color(0xFF111111), DarkSurface, DarkBackground)
                )
            )
            .padding(24.dp)
    ) {
        Spacer(Modifier.height(48.dp))

        HorizontalPager(
            state = pagerState,
            modifier = Modifier.weight(1f)
        ) { pageIndex ->
            val page = pages[pageIndex]
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 8.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                // Emoji in medallion
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier
                        .size(80.dp)
                        .clip(CircleShape)
                        .background(GoldPrimary.copy(alpha = 0.08f))
                        .border(1.dp, GoldDark.copy(alpha = 0.3f), CircleShape)
                ) {
                    Text(
                        text = page.emoji,
                        fontSize = 40.sp
                    )
                }

                Spacer(Modifier.height(24.dp))

                Text(
                    text = page.title,
                    style = MaterialTheme.typography.headlineLarge.copy(
                        fontWeight = FontWeight.Bold,
                        shadow = Shadow(color = GoldPrimary.copy(alpha = 0.3f), offset = Offset(0f, 2f), blurRadius = 8f)
                    ),
                    color = GoldPrimary,
                    textAlign = TextAlign.Center
                )

                Spacer(Modifier.height(24.dp))

                page.lines.forEach { line ->
                    Text(
                        text = line,
                        style = MaterialTheme.typography.bodyLarge,
                        color = TextWhite,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.padding(vertical = 4.dp)
                    )
                }
            }
        }

        // Page indicators
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Center
        ) {
            repeat(pages.size) { index ->
                val isActive = index == pagerState.currentPage
                Box(
                    modifier = Modifier
                        .padding(horizontal = 4.dp)
                        .size(if (isActive) 10.dp else 8.dp)
                        .then(
                            if (isActive) Modifier.drawBehind {
                                drawCircle(
                                    color = GoldPrimary.copy(alpha = 0.3f),
                                    radius = size.minDimension * 0.9f
                                )
                            } else Modifier
                        )
                        .clip(CircleShape)
                        .background(
                            if (isActive) GoldPrimary
                            else DarkSurfaceVariant
                        )
                )
            }
        }

        Spacer(Modifier.height(24.dp))

        // Navigation buttons
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            if (pagerState.currentPage > 0) {
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .height(48.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .border(1.dp, GoldDark.copy(alpha = 0.5f), RoundedCornerShape(12.dp))
                        .background(Color.Transparent)
                        .clickable {
                            scope.launch { pagerState.animateScrollToPage(pagerState.currentPage - 1) }
                        },
                    contentAlignment = Alignment.Center
                ) {
                    Text("PRÉCÉDENT", color = GoldLight, style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold))
                }
                Spacer(Modifier.width(12.dp))
            } else {
                Spacer(Modifier.weight(1f))
                Spacer(Modifier.width(12.dp))
            }

            if (pagerState.currentPage < pages.size - 1) {
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .height(48.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(Brush.horizontalGradient(listOf(GoldDark, GoldPrimary, GoldLight)))
                        .clickable {
                            scope.launch { pagerState.animateScrollToPage(pagerState.currentPage + 1) }
                        },
                    contentAlignment = Alignment.Center
                ) {
                    Text("SUIVANT", color = DarkBackground, style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.ExtraBold))
                }
            } else {
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .height(52.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(Brush.horizontalGradient(listOf(GoldDark, GoldPrimary, GoldLight)))
                        .clickable { onFinish() },
                    contentAlignment = Alignment.Center
                ) {
                    Text("COMMENCER 🎯", color = DarkBackground, style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.ExtraBold))
                }
            }
        }

        Spacer(Modifier.height(16.dp))
    }
}
