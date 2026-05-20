package com.theverdict.app.data.repository

import com.theverdict.app.data.local.CaseLoader
import com.theverdict.app.domain.model.AvatarZone
import com.theverdict.app.domain.model.Case
import com.theverdict.app.domain.model.CaseTheme
import com.theverdict.app.domain.model.Clue
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.time.LocalDate

data class DailyCaseInfo(val case: Case, val themeIndex: Int, val caseIndex: Int)

class CaseRepository(private val caseLoader: CaseLoader) {

    private val casesCache = mutableMapOf<CaseTheme, List<Case>>()

    // Daily case mode flag (session-scoped, not persisted)
    var isDailyMode: Boolean = false

    // Session-scoped clue persistence
    private val discoveredCluesBySuspect = mutableMapOf<Int, MutableList<Clue>>()

    // Session-scoped interrogated suspects tracking — StateFlow for reactive UI sync
    private val _interrogatedSuspectIds = MutableStateFlow<Set<Int>>(emptySet())
    val interrogatedSuspectIds: StateFlow<Set<Int>> = _interrogatedSuspectIds.asStateFlow()

    // Pressure system (0–100)
    private val _pressureLevel = MutableStateFlow(0)
    val pressureLevel: StateFlow<Int> = _pressureLevel.asStateFlow()
    private val maxPressure = 100

    fun addDiscoveredClue(suspectId: Int, clue: Clue) {
        val list = discoveredCluesBySuspect.getOrPut(suspectId) { mutableListOf() }
        if (clue !in list) list.add(clue)
    }

    fun getDiscoveredClues(suspectId: Int): List<Clue> =
        discoveredCluesBySuspect[suspectId]?.toList() ?: emptyList()

    fun getDiscoveredClueCount(suspectId: Int): Int =
        discoveredCluesBySuspect[suspectId]?.size ?: 0

    /** Returns how many distinct zones have at least one discovered clue for this suspect. */
    fun getDiscoveredZoneCount(suspectId: Int): Int {
        val clues = discoveredCluesBySuspect[suspectId] ?: return 0
        return AvatarZone.entries.count { zone -> zone.relatedClues.any { it in clues } }
    }

    fun markSuspectInterrogated(suspectId: Int) {
        _interrogatedSuspectIds.value = _interrogatedSuspectIds.value + suspectId
    }

    fun isInterrogated(suspectId: Int): Boolean = suspectId in _interrogatedSuspectIds.value

    fun getInterrogatedSuspectIds(): Set<Int> = _interrogatedSuspectIds.value

    fun addPressure(amount: Int) {
        _pressureLevel.value = (_pressureLevel.value + amount).coerceAtMost(maxPressure)
    }

    fun resetPressure() {
        _pressureLevel.value = 0
    }

    fun clearDiscoveredClues() {
        discoveredCluesBySuspect.clear()
        _interrogatedSuspectIds.value = emptySet()
        resetPressure()
    }

    fun getCasesForTheme(theme: CaseTheme): List<Case> {
        return casesCache.getOrPut(theme) { caseLoader.loadCasesForTheme(theme) }
    }

    fun getCase(theme: CaseTheme, index: Int): Case? {
        val cases = getCasesForTheme(theme)
        return cases.getOrNull(index)
    }

    fun getCaseById(caseId: Int): Case? {
        for (theme in CaseTheme.entries) {
            val found = getCasesForTheme(theme).find { it.id == caseId }
            if (found != null) return found
        }
        return null
    }

    fun getRandomCase(completedIds: Set<Int>): Case? {
        val allCases = CaseTheme.entries.flatMap { getCasesForTheme(it) }
        val remaining = allCases.filter { it.id !in completedIds }
        return remaining.randomOrNull() ?: allCases.randomOrNull()
    }

    fun getDailyCase(): DailyCaseInfo? {
        val today = LocalDate.now().toEpochDay()
        val allWithIndex = CaseTheme.entries.flatMapIndexed { themeIndex, theme ->
            getCasesForTheme(theme).mapIndexed { caseIndex, case ->
                Triple(case, themeIndex, caseIndex)
            }
        }
        if (allWithIndex.isEmpty()) return null
        val (case, themeIndex, caseIndex) = allWithIndex[(today % allWithIndex.size).toInt()]
        return DailyCaseInfo(case, themeIndex, caseIndex)
    }

    fun getTotalCaseCount(): Int = CaseTheme.entries.sumOf { getCasesForTheme(it).size }
}
