package com.theverdict.app.domain.model

import kotlinx.serialization.Serializable

@Serializable
data class AvatarConfig(
    val skinTone: Int = 0,
    val hairStyle: Int = 0,
    val hairColor: Int = 0,
    val accessory: Int = 0,
    val expression: Int = 0
)
