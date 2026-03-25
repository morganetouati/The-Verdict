package com.theverdict.app.domain.model

import kotlinx.serialization.Serializable

@Serializable
data class Case(
    val id: Int,
    val titre: String,
    val texte: String,
    val theme: CaseTheme,
    val difficulte: Int,
    val type: CaseType = CaseType.NORMAL,
    val suspects: List<Suspect>,
    val coupableIds: List<Int>
)
