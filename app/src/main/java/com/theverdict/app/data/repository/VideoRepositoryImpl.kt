package com.theverdict.app.data.repository

import com.theverdict.app.data.local.PreferencesManager
import com.theverdict.app.domain.model.*
import com.theverdict.app.domain.repository.VideoRepository

/**
 * Local repository with public domain archive videos for MVP (solo mode).
 *
 * All videos are sourced from the public domain (Internet Archive, Prelinger Archives)
 * — zero copyright issues, zero hosting costs (streaming from archive.org).
 * Aesthetic: "Archives Secrètes / Cold Case" — grainy VHS, old courtroom footage, 70s-80s interrogations.
 *
 * In v2, this will fetch from Supabase.
 */
class VideoRepositoryImpl(private val prefs: PreferencesManager? = null) : VideoRepository {

    // ==========================================================================
    // VIDÉOS VÉRIFIÉES — URLs testées et confirmées fonctionnelles
    // Sources: Pexels (licence libre, 0 coût, streaming direct)
    // Format: SD 640x360, ~0.5-1.8 Mo chacune — ultra léger pour mobile
    // ==========================================================================

    private val sampleVideos = listOf(

        // ── NIVEAU FACILE ──────────────────────────────────────────────

        VideoChallenge(
            id = "archive_01",
            title = "L'Affaire du Coffre-Fort",
            // Pexels 5438974 — Femme en entretien, visage expressif (1.2 Mo)
            videoUrl = "https://videos.pexels.com/video-files/5438974/5438974-sd_640_360_25fps.mp4",
            durationMs = 30000,
            isLie = true,
            difficulty = Difficulty.EASY,
            storyPrompt = "« Monsieur le Juge, je n'ai jamais mis les pieds dans cette banque. »",
            archiveContext = "Interrogatoire filmé — L'accusée nie toute implication dans le cambriolage.",
            sourceAttribution = "Pexels (Licence libre)",
            sourceUrl = "https://www.pexels.com/video/5438974/",
            verdictExplanation = "MENSONGE — L'accusée présente un regard fixe anormal, typique du menteur qui sur-joue la sincérité. Ses gestes d'auto-apaisement (grattage du cou) trahissent un stress interne, et l'incongruence entre ses mots ('je jure') et le mouvement négatif de sa tête confirme la tromperie. Son micro-mépris révèle qu'elle pense duper son interlocuteur.",
            truthTags = listOf(
                TruthTag(
                    MicroExpressionType.EYE_BLOCK, 5000,
                    "Regard fixe anormal — Le menteur sur-joue la sincérité en fixant son interlocuteur sans cligner."
                ),
                TruthTag(
                    MicroExpressionType.SELF_TOUCH, 12000,
                    "Grattage du cou — Geste d'auto-apaisement face à un mensonge précis sur son alibi."
                ),
                TruthTag(
                    MicroExpressionType.MICRO_CONTEMPT, 19000,
                    "Micro-mépris — Le coin de la lèvre gauche remonte : elle pense qu'elle va s'en sortir."
                ),
                TruthTag(
                    MicroExpressionType.HEAD_INCONGRUENCE, 26000,
                    "Incongruence — Dit 'je jure que c'est la vérité' mais sa tête fait un léger 'Non'."
                )
            )
        ),

        VideoChallenge(
            id = "archive_02",
            title = "Le Témoin Silencieux",
            // Pexels 5439068 — Homme en entretien, calme (0.6 Mo)
            videoUrl = "https://videos.pexels.com/video-files/5439068/5439068-sd_640_360_25fps.mp4",
            durationMs = 30000,
            isLie = false,
            difficulty = Difficulty.EASY,
            storyPrompt = "« Je vous dis exactement ce que j'ai vu cette nuit-là. »",
            archiveContext = "Déposition filmée — Un témoin décrit calmement les événements de la nuit du crime.",
            sourceAttribution = "Pexels (Licence libre)",
            sourceUrl = "https://www.pexels.com/video/5439068/",
            verdictExplanation = "VÉRITÉ — Le témoin présente un comportement cohérent et naturel. Aucun signe de stress disproportionné, pas de gestes d'auto-apaisement, et son récit est fluide sans hésitations suspectes. L'absence totale de micro-expressions de tromperie confirme la sincérité de sa déposition.",
            truthTags = listOf() // Pas de signes de tromperie — il dit la vérité
        ),

        // ── NIVEAU MOYEN ──────────────────────────────────────────────

        VideoChallenge(
            id = "archive_03",
            title = "Le Double Alibi",
            // Pexels 5330650 — Femme parlant à un thérapeute (1.7 Mo)
            videoUrl = "https://videos.pexels.com/video-files/5330650/5330650-sd_640_360_25fps.mp4",
            durationMs = 30000,
            isLie = true,
            difficulty = Difficulty.MEDIUM,
            storyPrompt = "« J'étais au restaurant avec mon mari. Demandez-lui. »",
            archiveContext = "Interrogatoire de police — La suspecte présente un alibi détaillé.",
            sourceAttribution = "Pexels (Licence libre)",
            sourceUrl = "https://www.pexels.com/video/5330650/",
            verdictExplanation = "MENSONGE — La suspecte présente un alibi trop détaillé, signe classique de fabrication. Ses lèvres pincées trahissent la rétention d'information, tandis que ses blocages oculaires prolongés indiquent la construction visuelle d'un faux souvenir. L'incongruence finale — dire 'j'étais là-bas' en hochant 'non' — est la preuve la plus accablante.",
            truthTags = listOf(
                TruthTag(
                    MicroExpressionType.LIP_COMPRESS, 4000,
                    "Lèvres pincées — Elle retient de l'information en présentant son alibi."
                ),
                TruthTag(
                    MicroExpressionType.SELF_TOUCH, 10000,
                    "Auto-contact au visage — Stress en donnant des détails inventés sur le restaurant."
                ),
                TruthTag(
                    MicroExpressionType.EYE_BLOCK, 17000,
                    "Blocage oculaire prolongé — Elle 'construit' visuellement un souvenir fictif."
                ),
                TruthTag(
                    MicroExpressionType.HEAD_INCONGRUENCE, 24000,
                    "Incongruence tête-message — Affirme sa présence au restaurant mais hoche la tête 'non'."
                )
            )
        ),

        VideoChallenge(
            id = "archive_04",
            title = "La Confession du Gardien",
            // Pexels 5438885 — Homme en entretien (1.0 Mo)
            videoUrl = "https://videos.pexels.com/video-files/5438885/5438885-sd_640_360_25fps.mp4",
            durationMs = 30000,
            isLie = false,
            difficulty = Difficulty.MEDIUM,
            storyPrompt = "« J'ai tout vu depuis ma guérite. Je n'ai rien à cacher. »",
            archiveContext = "Témoignage filmé — Le gardien de nuit raconte ce qu'il a observé avec cohérence.",
            sourceAttribution = "Pexels (Licence libre)",
            sourceUrl = "https://www.pexels.com/video/5438885/",
            verdictExplanation = "VÉRITÉ — Le gardien livre un témoignage cohérent et structuré. Son langage corporel est ouvert et détendu, ses yeux bougent naturellement (rappel de souvenirs réels, pas de construction). Son rythme de parole est constant, sans les accélérations ou pauses typiques du mensonge.",
            truthTags = listOf() // Vérité — témoignage cohérent
        ),

        VideoChallenge(
            id = "archive_07",
            title = "La Veuve Éplorée",
            // Pexels 5438934 — Jeune femme en entretien (1.8 Mo)
            videoUrl = "https://videos.pexels.com/video-files/5438934/5438934-sd_640_360_25fps.mp4",
            durationMs = 30000,
            isLie = true,
            difficulty = Difficulty.MEDIUM,
            storyPrompt = "« Mon mari me manque terriblement. C'était l'homme de ma vie. »",
            archiveContext = "Déposition filmée — La veuve d'un riche industriel pleure... mais est-ce sincère ?",
            sourceAttribution = "Pexels (Licence libre)",
            sourceUrl = "https://www.pexels.com/video/5438934/",
            verdictExplanation = "MENSONGE — La veuve simule le chagrin mais ses micro-expressions la trahissent. Le micro-mépris (sourire de satisfaction sous les larmes) est un signal fort d'émotion feinte. Elle compresse les lèvres pour retenir un sourire quand elle parle de son 'amour', et ses blocages oculaires prolongés sont un mécanisme pour maintenir la façade émotionnelle.",
            truthTags = listOf(
                TruthTag(
                    MicroExpressionType.MICRO_CONTEMPT, 6000,
                    "Micro-mépris — Un bref sourire de satisfaction sous les larmes."
                ),
                TruthTag(
                    MicroExpressionType.LIP_COMPRESS, 15000,
                    "Compression des lèvres — Elle retient un sourire en parlant de son 'amour'."
                ),
                TruthTag(
                    MicroExpressionType.EYE_BLOCK, 22000,
                    "Blocage oculaire — Ses yeux se ferment trop longtemps pour être naturel."
                )
            )
        ),

        // ── NIVEAU DIFFICILE ─────────────────────────────────────────

        VideoChallenge(
            id = "archive_05",
            title = "L'Héritier Suspect",
            // Pexels 5442623 — Personne en entretien formel (1.2 Mo)
            videoUrl = "https://videos.pexels.com/video-files/5442623/5442623-sd_640_360_25fps.mp4",
            durationMs = 30000,
            isLie = true,
            difficulty = Difficulty.HARD,
            storyPrompt = "« Mon oncle était un homme bon. Sa mort m'a dévasté. »",
            archiveContext = "Audition filmée — L'héritier principal est interrogé sur la mort suspecte de son oncle.",
            sourceAttribution = "Pexels (Licence libre)",
            sourceUrl = "https://www.pexels.com/video/5442623/",
            verdictExplanation = "MENSONGE — L'héritier affiche un micro-mépris révélateur quand il parle de la 'bonté' de son oncle. Ses blocages oculaires prolongés montrent une déconnexion émotionnelle — il ne ressent pas la dévastation qu'il décrit. La compression des lèvres avant de mentionner l'héritage trahit la vraie motivation, et l'incongruence finale ('je l'aimais' + secouement de tête) est la signature classique du mensonge.",
            truthTags = listOf(
                TruthTag(
                    MicroExpressionType.MICRO_CONTEMPT, 3000,
                    "Micro-mépris fugace — Un sourire asymétrique en parlant de la 'bonté' de son oncle."
                ),
                TruthTag(
                    MicroExpressionType.EYE_BLOCK, 9000,
                    "Blocage oculaire — Fermeture prolongée en décrivant sa 'dévastation'."
                ),
                TruthTag(
                    MicroExpressionType.LIP_COMPRESS, 14000,
                    "Compression des lèvres — Retient ses vraies émotions avant de parler de l'héritage."
                ),
                TruthTag(
                    MicroExpressionType.SELF_TOUCH, 20000,
                    "Main au cou — Auto-apaisement en expliquant ses mouvements la nuit du décès."
                ),
                TruthTag(
                    MicroExpressionType.HEAD_INCONGRUENCE, 27000,
                    "Incongruence — Dit 'je l'aimais profondément' en secouant imperceptiblement la tête."
                )
            )
        ),

        VideoChallenge(
            id = "archive_06",
            title = "Le Procureur de l'Ombre",
            // Pexels 5439078 — Évaluation de CV en entretien (1.2 Mo)
            videoUrl = "https://videos.pexels.com/video-files/5439078/5439078-sd_640_360_25fps.mp4",
            durationMs = 30000,
            isLie = true,
            difficulty = Difficulty.HARD,
            storyPrompt = "« Je n'ai aucun lien avec cette organisation. »",
            archiveContext = "Commission d'enquête — Un homme d'affaires nie toute connexion avec un réseau criminel.",
            sourceAttribution = "Pexels (Licence libre)",
            sourceUrl = "https://www.pexels.com/video/5439078/",
            verdictExplanation = "MENSONGE — L'homme d'affaires bloque immédiatement ses yeux dès la première question, signe d'un réflexe de protection. Le grattage derrière l'oreille signale un stress intense, les lèvres pincées indiquent la rétention d'informations compromettantes, et son micro-mépris montre qu'il se croit supérieur à l'enquêteur. La double incongruence finale — dire 'absolument pas' en acquiesçant — est irréfutable.",
            truthTags = listOf(
                TruthTag(
                    MicroExpressionType.EYE_BLOCK, 2000,
                    "Blocage oculaire immédiat — Se ferme dès la première question compromettante."
                ),
                TruthTag(
                    MicroExpressionType.SELF_TOUCH, 7000,
                    "Grattage derrière l'oreille — Stress intense en niant les associations."
                ),
                TruthTag(
                    MicroExpressionType.LIP_COMPRESS, 13000,
                    "Lèvres pincées — Retient des noms et des détails compromettants."
                ),
                TruthTag(
                    MicroExpressionType.MICRO_CONTEMPT, 18000,
                    "Micro-mépris — Se sent supérieur à l'enquêteur."
                ),
                TruthTag(
                    MicroExpressionType.HEAD_INCONGRUENCE, 25000,
                    "Double incongruence — Dit 'absolument pas' en acquiesçant légèrement."
                )
            )
        ),

        VideoChallenge(
            id = "archive_08",
            title = "L'Inspecteur Intègre",
            // Pexels 5977260 — Discussion en café (0.6 Mo)
            videoUrl = "https://videos.pexels.com/video-files/5977260/5977260-sd_640_360_25fps.mp4",
            durationMs = 30000,
            isLie = false,
            difficulty = Difficulty.HARD,
            storyPrompt = "« J'ai mené cette enquête selon les règles. Chaque preuve est documentée. »",
            archiveContext = "Rapport de police filmé — L'inspecteur présente méthodiquement son enquête.",
            sourceAttribution = "Pexels (Licence libre)",
            sourceUrl = "https://www.pexels.com/video/5977260/",
            verdictExplanation = "VÉRITÉ — L'inspecteur présente son rapport avec méthode et assurance. Son langage corporel est ouvert et cohérent avec ses propos. Il maintient un contact visuel naturel (sans fixation anormale), ses gestes sont illustratifs (accompagnent le discours) plutôt que d'auto-apaisement, et son débit est régulier. Aucune fuite comportementale détectée.",
            truthTags = listOf() // Vérité — rapport honnête et méthodique
        )
    )

    private val playedIds = mutableSetOf<String>()

    override suspend fun getRandomChallenge(): VideoChallenge {
        // Load persisted played IDs on first call
        if (playedIds.isEmpty() && prefs != null) {
            playedIds.addAll(prefs.getPlayedIds())
        }

        val unplayed = sampleVideos.filter { it.id !in playedIds }
        val video = if (unplayed.isNotEmpty()) unplayed.random() else {
            playedIds.clear()
            prefs?.clearPlayedIds()
            sampleVideos.random()
        }
        playedIds.add(video.id)
        prefs?.addPlayedId(video.id)
        return video
    }

    override suspend fun getChallengeById(id: String): VideoChallenge? {
        return sampleVideos.find { it.id == id }
    }

    override fun getAllChallenges(): List<VideoChallenge> = sampleVideos
}
