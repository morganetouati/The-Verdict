package com.theverdict.app.data.repository

import com.theverdict.app.data.local.CaseLoader
import com.theverdict.app.domain.model.Case
import com.theverdict.app.domain.model.CaseTheme

class CaseRepository(private val caseLoader: CaseLoader) {

    private val casesCache = mutableMapOf<CaseTheme, List<Case>>()

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

    fun getTotalCaseCount(): Int = CaseTheme.entries.sumOf { getCasesForTheme(it).size }
}
