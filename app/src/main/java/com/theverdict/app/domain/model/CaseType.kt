package com.theverdict.app.domain.model

import kotlinx.serialization.Serializable

@Serializable
enum class CaseType {
    NORMAL,
    AUCUN_MENTEUR,
    DEUX_MENTEURS,
    TOUS_MENTENT,
    PERSONNE_MENT
}
