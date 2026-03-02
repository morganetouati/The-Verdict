package com.theverdict.app.domain.model

/**
 * Types of micro-expressions and behavioral cues the player can detect.
 */
enum class MicroExpressionType(
    val emoji: String,
    val labelRes: String,
    val descriptionKey: String
) {
    LIP_COMPRESS("👄", "Lèvres pincées", "lesson_lip_compress"),
    EYE_BLOCK("👀", "Blocage oculaire", "lesson_eye_block"),
    SELF_TOUCH("🖐️", "Auto-contact", "lesson_neck_scratch"),
    MICRO_CONTEMPT("😏", "Micro-Mépris", "lesson_micro_contempt"),
    HEAD_INCONGRUENCE("🔄", "Incongruence Tête", "lesson_head_incongruence")
}

/**
 * Lesson data for each micro-expression.
 */
data class MicroExpressionLesson(
    val type: MicroExpressionType,
    val title: String,
    val description: String,
    val detailExplanation: String
)

/**
 * Pre-built lessons for the 5 core micro-expressions.
 */
object MicroExpressionLessons {
    val all = listOf(
        MicroExpressionLesson(
            type = MicroExpressionType.MICRO_CONTEMPT,
            title = "Le Micro-Mépris",
            description = "Un coin de la lèvre se relève brièvement d'un seul côté.",
            detailExplanation = "C'est la seule expression faciale asymétrique. Elle indique que la personne se sent supérieure ou qu'elle ne croit pas à ce qu'elle vient de dire. Si un 'accusé' fait cela en affirmant son innocence, c'est un drapeau rouge."
        ),
        MicroExpressionLesson(
            type = MicroExpressionType.EYE_BLOCK,
            title = "Le Blocage Oculaire",
            description = "La personne ferme les yeux plus longtemps qu'un simple clignement.",
            detailExplanation = "C'est un mécanisme de défense archaïque : on ferme les yeux pour 'effacer' une réalité qui nous dérange ou pour se protéger d'une question difficile. Un menteur l'utilise souvent quand il doit inventer un détail complexe."
        ),
        MicroExpressionLesson(
            type = MicroExpressionType.LIP_COMPRESS,
            title = "La Compression des Lèvres",
            description = "Les lèvres disparaissent ou se pincent pour former une ligne droite.",
            detailExplanation = "Cela indique que la personne retient quelque chose (une émotion ou une information). C'est le signe universel du 'je ne veux pas dire la vérité' ou du stress intense."
        ),
        MicroExpressionLesson(
            type = MicroExpressionType.SELF_TOUCH,
            title = "Le Grattage du Cou",
            description = "La personne se gratte le côté du cou, juste sous l'oreille.",
            detailExplanation = "C'est un geste d'auto-apaisement. En touchant cette zone, on tente de ralentir le rythme cardiaque via le nerf vague. Si ce geste arrive juste après une question clé, le suspect est en plein inconfort."
        ),
        MicroExpressionLesson(
            type = MicroExpressionType.HEAD_INCONGRUENCE,
            title = "L'Incongruence Tête-Message",
            description = "La personne dit 'Oui' mais sa tête fait un micro-mouvement de gauche à droite.",
            detailExplanation = "Le corps dit la vérité avant que le cerveau ne puisse la masquer. C'est l'indice le plus facile à détecter pour un débutant et c'est extrêmement satisfaisant."
        )
    )
}
