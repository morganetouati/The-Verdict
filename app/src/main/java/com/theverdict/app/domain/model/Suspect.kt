package com.theverdict.app.domain.model

import kotlinx.serialization.Serializable

@Serializable
data class Suspect(
    val id: Int,
    val nom: String,
    val avatar: AvatarConfig = AvatarConfig(),
    val phrase: String,
    val indices: List<Clue> = emptyList()
)
