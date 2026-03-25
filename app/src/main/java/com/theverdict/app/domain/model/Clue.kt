package com.theverdict.app.domain.model

import kotlinx.serialization.Serializable

@Serializable
enum class Clue(val label: String, val icon: String) {
    REGARDE_AILLEURS("Regarde ailleurs", "visibility_off"),
    TRANSPIRE("Transpire", "water_drop"),
    HESITE("Hésite", "more_horiz"),
    TROP_CALME("Trop calme", "self_improvement"),
    SE_CONTREDIT("Se contredit", "sync_problem"),
    PARLE_VITE("Parle trop vite", "speed"),
    NERVEUX("Nerveux", "anxiety"),
    CONFIANT("Confiant", "sentiment_satisfied"),
    EVITE_REGARD("Évite le regard", "person_off"),
    MAINS_TREMBLENT("Mains qui tremblent", "back_hand"),
    SOURIT_TROP("Sourit trop", "mood"),
    VOIX_CHANGE("Voix qui change", "record_voice_over"),
    BRAS_CROISES("Bras croisés", "person"),
    REPOND_VITE("Répond trop vite", "bolt"),
    DETAIL_SUSPECT("Détail suspect", "search"),
    HISTOIRE_FLOUE("Histoire floue", "blur_on")
}
