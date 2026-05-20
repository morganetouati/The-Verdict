package com.theverdict.app.domain.model

import kotlinx.serialization.Serializable

@Serializable
data class AvatarConfig(
    val skinTone: Int = 0,
    val hairStyle: Int = 0,
    val hairColor: Int = 0,
    val accessory: Int = 0,
    val expression: Int = 0,
    val gender: Int = 0,    // 0 = male, 1 = female
    val eyeColor: Int = 0   // 0=brown, 1=blue, 2=green, 3=gray, 4=hazel, 5=dark
)
