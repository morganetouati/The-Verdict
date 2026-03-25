package com.theverdict.app.data.local

import android.content.Context
import com.theverdict.app.domain.model.Case
import com.theverdict.app.domain.model.CaseTheme
import kotlinx.serialization.json.Json

class CaseLoader(private val context: Context) {

    private val json = Json { ignoreUnknownKeys = true }

    private val themeFiles = mapOf(
        CaseTheme.ECOLE to "cases/theme_1_ecole.json",
        CaseTheme.TRAVAIL to "cases/theme_2_travail.json",
        CaseTheme.FAMILLE to "cases/theme_3_famille.json",
        CaseTheme.POLICE to "cases/theme_4_police.json",
        CaseTheme.TRIBUNAL to "cases/theme_5_tribunal.json",
        CaseTheme.ENQUETE to "cases/theme_6_enquete.json",
        CaseTheme.ESPIONNAGE to "cases/theme_7_espionnage.json",
        CaseTheme.ELITE to "cases/theme_8_elite.json"
    )

    fun loadCasesForTheme(theme: CaseTheme): List<Case> {
        val fileName = themeFiles[theme] ?: return emptyList()
        val jsonString = context.assets.open(fileName).bufferedReader().use { it.readText() }
        return json.decodeFromString<List<Case>>(jsonString)
    }

    fun loadAllCases(): Map<CaseTheme, List<Case>> {
        return CaseTheme.entries.associateWith { loadCasesForTheme(it) }
    }
}
