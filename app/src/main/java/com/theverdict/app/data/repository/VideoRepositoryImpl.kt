package com.theverdict.app.data.repository

import com.theverdict.app.data.local.PreferencesManager
import com.theverdict.app.domain.model.*
import com.theverdict.app.domain.repository.VideoRepository
import java.time.LocalDate

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
            videoUrl = "https://videos.pexels.com/video-files/5330656/5330656-sd_640_360_25fps.mp4",
            durationMs = 65000,
            isLie = true,
            difficulty = Difficulty.EASY,
            storyPrompt = "« Monsieur le Juge, je n'ai jamais mis les pieds dans cette banque. »",
            archiveContext = "Interrogatoire filmé — L'accusée nie toute implication dans le cambriolage.",
            sourceAttribution = "Pexels (Licence libre)",
            sourceUrl = "https://www.pexels.com/video/5330656/",
            verdictExplanation = "MENSONGE — L'accusée présente un regard fixe anormal, typique du menteur qui sur-joue la sincérité. Ses gestes d'auto-apaisement (grattage du cou) trahissent un stress interne, et l'incongruence entre ses mots ('je jure') et le mouvement négatif de sa tête confirme la tromperie. Son micro-mépris révèle qu'elle pense duper son interlocuteur.",
            hints = listOf(
                "Observez la fréquence de clignement des yeux pendant la déclaration.",
                "Remarquez les mouvements des mains vers le cou ou le visage.",
                "Surveillez les micro-expressions au coin des lèvres.",
                "Comparez les mouvements de tête avec les affirmations verbales.",
                "La fixité du regard peut trahir une sur-compensation."
            ),
            truthTags = listOf(
                TruthTag(
                    MicroExpressionType.EYE_BLOCK, 8000,
                    "Regard fixe anormal — Le menteur sur-joue la sincérité en fixant son interlocuteur sans cligner."
                ),
                TruthTag(
                    MicroExpressionType.SELF_TOUCH, 22000,
                    "Grattage du cou — Geste d'auto-apaisement face à un mensonge précis sur son alibi."
                ),
                TruthTag(
                    MicroExpressionType.MICRO_CONTEMPT, 38000,
                    "Micro-mépris — Le coin de la lèvre gauche remonte : elle pense qu'elle va s'en sortir."
                ),
                TruthTag(
                    MicroExpressionType.HEAD_INCONGRUENCE, 52000,
                    "Incongruence — Dit 'je jure que c'est la vérité' mais sa tête fait un léger 'Non'."
                )
            )
        ),

        VideoChallenge(
            id = "archive_02",
            title = "Le Témoin Silencieux",
            videoUrl = "https://videos.pexels.com/video-files/5682796/5682796-sd_640_360_25fps.mp4",
            durationMs = 59000,
            isLie = false,
            difficulty = Difficulty.EASY,
            storyPrompt = "« Je vous dis exactement ce que j'ai vu cette nuit-là. »",
            archiveContext = "Déposition filmée — Un témoin décrit calmement les événements de la nuit du crime.",
            sourceAttribution = "Pexels (Licence libre)",
            sourceUrl = "https://www.pexels.com/video/5682796/",
            verdictExplanation = "VÉRITÉ — Le témoin présente un comportement cohérent et naturel. Aucun signe de stress disproportionné, pas de gestes d'auto-apaisement, et son récit est fluide sans hésitations suspectes. L'absence totale de micro-expressions de tromperie confirme la sincérité de sa déposition.",
            hints = listOf(
                "Observez la posture générale du témoin pendant son récit.",
                "Notez la fluidité du discours — y a-t-il des hésitations ?",
                "Les gestes sont-ils illustratifs ou d'auto-apaisement ?",
                "Le contact visuel est-il naturel ou forcé ?",
                "Les émotions affichées sont-elles cohérentes avec les mots ?"
            ),
            truthTags = listOf() // Pas de signes de tromperie — il dit la vérité
        ),

        VideoChallenge(
            id = "archive_09",
            title = "La Signature Fantôme",
            videoUrl = "https://videos.pexels.com/video-files/5330653/5330653-sd_640_360_25fps.mp4",
            durationMs = 57000,
            isLie = true,
            difficulty = Difficulty.EASY,
            storyPrompt = "« Je n'ai jamais signé ce contrat. Cette signature n'est pas la mienne. »",
            archiveContext = "Litige commercial filmé — Un dirigeant conteste l'authenticité de sa propre signature.",
            sourceAttribution = "Pexels (Licence libre)",
            sourceUrl = "https://www.pexels.com/video/5330653/",
            verdictExplanation = "MENSONGE — Ses yeux fuient vers la gauche à chaque mention du contrat, signe classique de construction fictive. Le grattage derrière l'oreille révèle un stress intense, et ses lèvres pincées retiennent l'aveu qu'il s'apprête à faire.",
            hints = listOf(
                "Observe la direction du regard quand il parle du contrat.",
                "Un geste derrière l'oreille indique souvent un stress intense.",
                "Les lèvres peuvent se pincer juste avant un mensonge clé.",
                "Cherche une incongruence entre ses mots et ses mouvements de tête.",
                "La sur-précision dans le déni est souvent suspecte."
            ),
            truthTags = listOf(
                TruthTag(MicroExpressionType.EYE_BLOCK, 9000, "Regard fuyant à gauche — construction d'un souvenir fictif sur la signature."),
                TruthTag(MicroExpressionType.SELF_TOUCH, 24000, "Grattage derrière l'oreille — stress intense lors de la description du contrat."),
                TruthTag(MicroExpressionType.LIP_COMPRESS, 42000, "Lèvres pincées — retient des détails sur sa propre signature.")
            )
        ),

        VideoChallenge(
            id = "archive_10",
            title = "Le Comptable Irréprochable",
            videoUrl = "https://videos.pexels.com/video-files/5438944/5438944-sd_640_360_25fps.mp4",
            durationMs = 55000,
            isLie = false,
            difficulty = Difficulty.EASY,
            storyPrompt = "« Chaque ligne de ce bilan a été vérifiée trois fois. Je réponds de chaque chiffre. »",
            archiveContext = "Audit filmé — Le directeur financier présente les comptes annuels devant le conseil.",
            sourceAttribution = "Pexels (Licence libre)",
            sourceUrl = "https://www.pexels.com/video/5438944/",
            verdictExplanation = "VÉRITÉ — Sa posture est ouverte et assurée. Il maintient un contact visuel stable, ses gestes illustrent son discours (il 'montre' les chiffres), et son débit est constant. Aucun geste d'auto-apaisement détecté.",
            hints = listOf(
                "Le débit de parole est-il constant sur toute la durée ?",
                "Ses gestes illustrent-ils ce qu'il dit ou sont-ils défensifs ?",
                "Observe la posture — reste-t-elle ouverte tout au long ?",
                "Le contact visuel est-il naturel et régulier ?",
                "Cherche des gestes d'auto-apaisement — sont-ils absents ?"
            ),
            truthTags = listOf()
        ),

        VideoChallenge(
            id = "archive_11",
            title = "L'Alibi du Vendredi Soir",
            videoUrl = "https://videos.pexels.com/video-files/5330647/5330647-sd_640_360_25fps.mp4",
            durationMs = 51000,
            isLie = true,
            difficulty = Difficulty.EASY,
            storyPrompt = "« J'étais chez des amis toute la soirée. Ils peuvent tous en témoigner. »",
            archiveContext = "Interrogatoire préliminaire — Un suspect présente son alibi pour le soir du meurtre.",
            sourceAttribution = "Pexels (Licence libre)",
            sourceUrl = "https://www.pexels.com/video/5330647/",
            verdictExplanation = "MENSONGE — Il mentionne ses 'amis' en faisant un micro-mépris, révélant qu'il les utilise comme bouclier. Son regard se bloque systématiquement avant de citer un nom, signe de construction fictive. L'incongruence finale trahit son vrai état.",
            hints = listOf(
                "Un micro-mépris envers ses propres témoins est suspect.",
                "Observe la direction du regard avant chaque nom cité.",
                "Le blocage oculaire peut indiquer une construction de faux souvenir.",
                "La main au visage révèle du stress interne.",
                "Comparer la tête avec les certitudes qu'il exprime."
            ),
            truthTags = listOf(
                TruthTag(MicroExpressionType.MICRO_CONTEMPT, 11000, "Micro-mépris en citant ses 'amis' — il les utilise comme alibi fabriqué."),
                TruthTag(MicroExpressionType.EYE_BLOCK, 27000, "Blocage oculaire — construit mentalement les noms de ses prétendus témoins."),
                TruthTag(MicroExpressionType.HEAD_INCONGRUENCE, 48000, "Incongruence — affirme avec certitude mais sa tête dit 'non'.")
            )
        ),

        VideoChallenge(
            id = "archive_12",
            title = "La Directrice Honnête",
            videoUrl = "https://videos.pexels.com/video-files/5330650/5330650-sd_640_360_25fps.mp4",
            durationMs = 50000,
            isLie = false,
            difficulty = Difficulty.EASY,
            storyPrompt = "« J'ai pris cette décision seule, en pleine connaissance de cause. »",
            archiveContext = "Rapport de gestion filmé — La PDG assume personnellement une décision controversée.",
            sourceAttribution = "Pexels (Licence libre)",
            sourceUrl = "https://www.pexels.com/video/5330650/",
            verdictExplanation = "VÉRITÉ — Elle assume avec fermeté et cohérence. Sa posture droite, ses gestes largement ouverts et l'absence totale de gestes d'auto-apaisement confirment une conviction sincère. Elle dit exactement ce qu'elle pense.",
            hints = listOf(
                "Une personne qui assume regarde-t-elle directement en face ?",
                "Les gestes sont-ils ouverts ou protecteurs ?",
                "Cherche des micro-expressions de doute ou d'hésitation.",
                "Le rythme de parole est-il soutenu et régulier ?",
                "Une vraie prise de responsabilité s'accompagne-t-elle de stress excessif ?"
            ),
            truthTags = listOf()
        ),

        VideoChallenge(
            id = "archive_13",
            title = "Le Fournisseur Fantôme",
            videoUrl = "https://videos.pexels.com/video-files/5330652/5330652-sd_640_360_25fps.mp4",
            durationMs = 49000,
            isLie = true,
            difficulty = Difficulty.EASY,
            storyPrompt = "« Ce fournisseur existe bel et bien. J'ai tous les justificatifs. »",
            archiveContext = "Commission de contrôle fiscal — Un responsable des achats défend des factures suspectes.",
            sourceAttribution = "Pexels (Licence libre)",
            sourceUrl = "https://www.pexels.com/video/5330652/",
            verdictExplanation = "MENSONGE — Il surexplique l'existence du fournisseur, ce qui est un signe classique de fraude. Ses lèvres pincées retiennent les aveux, et son micro-mépris envers les auditeurs révèle qu'il pense les duper.",
            hints = listOf(
                "Trop d'explications non sollicitées est suspect.",
                "Observe les lèvres lors des affirmations les plus fortes.",
                "Un micro-mépris envers l'interlocuteur peut trahir une certitude de s'en sortir.",
                "Les gestes d'auto-apaisement augmentent avec les questions précises.",
                "Le contact visuel devient-il fixe ou fuyant sous pression ?"
            ),
            truthTags = listOf(
                TruthTag(MicroExpressionType.LIP_COMPRESS, 13000, "Lèvres pincées — retient les détails sur les factures fictives."),
                TruthTag(MicroExpressionType.MICRO_CONTEMPT, 31000, "Micro-mépris — se croit plus intelligent que les auditeurs."),
                TruthTag(MicroExpressionType.SELF_TOUCH, 46000, "Auto-contact — stress lors de la demande de preuves concrètes.")
            )
        ),

        VideoChallenge(
            id = "archive_14",
            title = "L'Enquêtrice Transparente",
            videoUrl = "https://videos.pexels.com/video-files/5438936/5438936-sd_640_360_25fps.mp4",
            durationMs = 47000,
            isLie = false,
            difficulty = Difficulty.EASY,
            storyPrompt = "« Mon rapport reflète exactement ce que j'ai observé sur le terrain. »",
            archiveContext = "Débrief d'enquête filmé — Une enquêtrice restitue ses observations sans filtre.",
            sourceAttribution = "Pexels (Licence libre)",
            sourceUrl = "https://www.pexels.com/video/5438936/",
            verdictExplanation = "VÉRITÉ — Ses mouvements oculaires vont vers la droite (rappel de souvenirs réels), ses gestes sont calibrés et illustratifs. Elle ne présente aucun signe de manipulation émotionnelle ou de construction fictive.",
            hints = listOf(
                "Les mouvements oculaires vers la droite indiquent souvent un rappel de mémoire réelle.",
                "Ses gestes illustrent-ils des faits concrets ?",
                "Le ton de voix est-il stable et factuel ?",
                "Cherche des ruptures dans la fluidité du discours.",
                "L'absence de gestes de protection est significative."
            ),
            truthTags = listOf()
        ),

        VideoChallenge(
            id = "archive_15",
            title = "Le Trésorier Nerveux",
            videoUrl = "https://videos.pexels.com/video-files/5438943/5438943-sd_640_360_25fps.mp4",
            durationMs = 44000,
            isLie = true,
            difficulty = Difficulty.EASY,
            storyPrompt = "« Les fonds de l'association sont intacts. Je peux vous montrer les relevés. »",
            archiveContext = "Assemblée générale filmée — Le trésorier d'une association répond aux questions sur les finances.",
            sourceAttribution = "Pexels (Licence libre)",
            sourceUrl = "https://www.pexels.com/video/5438943/",
            verdictExplanation = "MENSONGE — Il propose spontanément de 'montrer les relevés' — une tactique de diversion classique. Ses blocages oculaires surviennent précisément sur les mots 'intacts' et 'montrer', et son incongruence de tête est flagrante.",
            hints = listOf(
                "Offrir spontanément des preuves peut être une diversion.",
                "Observe quand exactement le blocage oculaire survient.",
                "Les mots clés déclenchent-ils des réactions physiques particulières ?",
                "La tête acquiesce-t-elle ou nie-t-elle les affirmations fortes ?",
                "Les épaules montent-elles légèrement lors du mensonge ?"
            ),
            truthTags = listOf(
                TruthTag(MicroExpressionType.EYE_BLOCK, 8000, "Blocage oculaire sur le mot 'intacts' — il ferme les yeux sur sa propre affirmation."),
                TruthTag(MicroExpressionType.LIP_COMPRESS, 26000, "Lèvres pincées avant de proposer les relevés — calcule sa stratégie."),
                TruthTag(MicroExpressionType.HEAD_INCONGRUENCE, 41000, "Incongruence — dit 'tout va bien' en secouant imperceptiblement la tête.")
            )
        ),

        VideoChallenge(
            id = "archive_16",
            title = "Le Notaire Serein",
            videoUrl = "https://videos.pexels.com/video-files/5438988/5438988-sd_640_360_25fps.mp4",
            durationMs = 45000,
            isLie = false,
            difficulty = Difficulty.EASY,
            storyPrompt = "« Cet acte a été signé librement et en toute connaissance des parties. »",
            archiveContext = "Déposition notariale filmée — Le notaire confirme les conditions de la signature d'un testament.",
            sourceAttribution = "Pexels (Licence libre)",
            sourceUrl = "https://www.pexels.com/video/5438988/",
            verdictExplanation = "VÉRITÉ — La sérénité professionnelle du notaire est authentique. Ses paupières ne clignotent pas anormalement, ses mains restent posées, et son débit est légèrement plus lent que la normale — signe d'une précision voulue, pas d'un mensonge construit.",
            hints = listOf(
                "Un débit lent et mesuré peut indiquer une précision professionnelle, pas un mensonge.",
                "Les mains posées sur la table trahissent-elles du stress ?",
                "Cherche des micro-expressions furtives entre les phrases.",
                "La respiration visible (épaules) est-elle régulière ?",
                "Compare le ton au moment des détails légaux précis."
            ),
            truthTags = listOf()
        ),

        // ── NIVEAU MOYEN ──────────────────────────────────────────────

        VideoChallenge(
            id = "archive_03",
            title = "Le Double Alibi",
            videoUrl = "https://videos.pexels.com/video-files/5438974/5438974-sd_640_360_25fps.mp4",
            durationMs = 36000,
            isLie = true,
            difficulty = Difficulty.MEDIUM,
            storyPrompt = "« J'étais au restaurant avec mon mari. Demandez-lui. »",
            archiveContext = "Interrogatoire de police — La suspecte présente un alibi détaillé.",
            sourceAttribution = "Pexels (Licence libre)",
            sourceUrl = "https://www.pexels.com/video/5438974/",
            verdictExplanation = "MENSONGE — La suspecte présente un alibi trop détaillé, signe classique de fabrication. Ses lèvres pincées trahissent la rétention d'information, tandis que ses blocages oculaires prolongés indiquent la construction visuelle d'un faux souvenir. L'incongruence finale — dire 'j'étais là-bas' en hochant 'non' — est la preuve la plus accablante.",
            hints = listOf(
                "Surveillez les lèvres — elles peuvent retenir une information.",
                "Les mains au visage signalent souvent un stress interne.",
                "Observez le regard quand elle décrit un souvenir précis.",
                "Comparez les hochements de tête avec les affirmations.",
                "Un alibi trop détaillé peut être un signe de fabrication."
            ),
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
            videoUrl = "https://videos.pexels.com/video-files/5442623/5442623-sd_640_360_25fps.mp4",
            durationMs = 37000,
            isLie = false,
            difficulty = Difficulty.MEDIUM,
            storyPrompt = "« J'ai tout vu depuis ma guérite. Je n'ai rien à cacher. »",
            archiveContext = "Témoignage filmé — Le gardien de nuit raconte ce qu'il a observé avec cohérence.",
            sourceAttribution = "Pexels (Licence libre)",
            sourceUrl = "https://www.pexels.com/video/5442623/",
            verdictExplanation = "VÉRITÉ — Le gardien livre un témoignage cohérent et structuré. Son langage corporel est ouvert et détendu, ses yeux bougent naturellement (rappel de souvenirs réels, pas de construction). Son rythme de parole est constant, sans les accélérations ou pauses typiques du mensonge.",
            hints = listOf(
                "La posture ouverte est-elle maintenue tout au long du témoignage ?",
                "Le rythme de parole reste-t-il constant ?",
                "Observez les mouvements oculaires — sont-ils naturels ?",
                "Les gestes illustrent-ils le discours ou trahissent-ils du stress ?",
                "L'émotion affichée correspond-elle au contenu verbal ?"
            ),
            truthTags = listOf() // Vérité — témoignage cohérent
        ),

        VideoChallenge(
            id = "archive_07",
            title = "La Veuve Éplorée",
            videoUrl = "https://videos.pexels.com/video-files/5330643/5330643-sd_640_360_25fps.mp4",
            durationMs = 37000,
            isLie = true,
            difficulty = Difficulty.MEDIUM,
            storyPrompt = "« Mon mari me manque terriblement. C'était l'homme de ma vie. »",
            archiveContext = "Déposition filmée — La veuve d'un riche industriel pleure... mais est-ce sincère ?",
            sourceAttribution = "Pexels (Licence libre)",
            sourceUrl = "https://www.pexels.com/video/5330643/",
            verdictExplanation = "MENSONGE — La veuve simule le chagrin mais ses micro-expressions la trahissent. Le micro-mépris (sourire de satisfaction sous les larmes) est un signal fort d'émotion feinte. Elle compresse les lèvres pour retenir un sourire quand elle parle de son 'amour', et ses blocages oculaires prolongés sont un mécanisme pour maintenir la façade émotionnelle.",
            hints = listOf(
                "Observez le coin des lèvres sous les larmes.",
                "Les yeux se ferment-ils naturellement ou trop longtemps ?",
                "Le chagrin affiché est-il constant ou intermittent ?",
                "Cherchez une micro-expression de satisfaction fugace.",
                "La compression des lèvres peut cacher une émotion contraire."
            ),
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

        VideoChallenge(
            id = "archive_17",
            title = "Le Partenaire Trahi",
            videoUrl = "https://videos.pexels.com/video-files/5330645/5330645-sd_640_360_25fps.mp4",
            durationMs = 39000,
            isLie = true,
            difficulty = Difficulty.MEDIUM,
            storyPrompt = "« Il m'a volé mes idées. Je suis la seule inventrice de ce brevet. »",
            archiveContext = "Litige de propriété intellectuelle — Une co-fondatrice revendique la paternité exclusive d'un brevet.",
            sourceAttribution = "Pexels (Licence libre)",
            sourceUrl = "https://www.pexels.com/video/5330645/",
            verdictExplanation = "MENSONGE — Elle pointe son partenaire avec un mépris disproportionné, trahissant une culpabilité projetée. Ses lèvres pincées retiennent les détails de leur collaboration réelle, et son blocage oculaire prolongé révèle qu'elle construit un récit révisé.",
            hints = listOf(
                "Un mépris disproportionné envers un absent peut révéler de la culpabilité projetée.",
                "Les lèvres pincées retiennent-elles des informations sur la collaboration ?",
                "Le blocage oculaire survient-il sur les affirmations d'exclusivité ?",
                "Observe les gestes d'auto-apaisement lors des détails techniques.",
                "La certitude affichée est-elle cohérente avec les micro-expressions ?"
            ),
            truthTags = listOf(
                TruthTag(MicroExpressionType.MICRO_CONTEMPT, 5000, "Mépris excessif envers son partenaire — cache sa propre contribution limitée."),
                TruthTag(MicroExpressionType.LIP_COMPRESS, 13000, "Lèvres pincées — retient les détails de leur travail commun."),
                TruthTag(MicroExpressionType.EYE_BLOCK, 21000, "Blocage oculaire prolongé — construit le récit de son invention 'exclusive'.")
            )
        ),

        VideoChallenge(
            id = "archive_18",
            title = "Le Chirurgien Précis",
            videoUrl = "https://videos.pexels.com/video-files/5438938/5438938-sd_640_360_25fps.mp4",
            durationMs = 42000,
            isLie = false,
            difficulty = Difficulty.MEDIUM,
            storyPrompt = "« J'ai suivi le protocole d'urgence à la lettre. Chaque geste est documenté. »",
            archiveContext = "Revue de dossier chirurgicale — Un chirurgien répond d'une opération d'urgence controversée.",
            sourceAttribution = "Pexels (Licence libre)",
            sourceUrl = "https://www.pexels.com/video/5438938/",
            verdictExplanation = "VÉRITÉ — Sa précision clinique est authentique. Il décrit les événements dans un ordre chronologique naturel (non récité), ses pauses réflexives précèdent chaque détail technique — signe d'un vrai souvenir, pas d'une construction.",
            hints = listOf(
                "Les pauses avant les détails techniques indiquent-elles un rappel réel ?",
                "La chronologie du récit est-elle naturelle ou trop parfaite ?",
                "Ses gestes illustrent-ils les actes médicaux qu'il décrit ?",
                "Cherche des ruptures dans la fluidité — trahissent-elles du stress ou de la réflexion ?",
                "Le ton professionnel est-il cohérent du début à la fin ?"
            ),
            truthTags = listOf()
        ),

        VideoChallenge(
            id = "archive_19",
            title = "L'Expert Biaisé",
            videoUrl = "https://videos.pexels.com/video-files/5438889/5438889-sd_640_360_25fps.mp4",
            durationMs = 40000,
            isLie = true,
            difficulty = Difficulty.MEDIUM,
            storyPrompt = "« Mon expertise est totalement indépendante. Je n'ai aucun lien avec ce cabinet. »",
            archiveContext = "Tribunal commercial — Un expert judiciaire affirme son indépendance vis-à-vis d'un cabinet d'audit.",
            sourceAttribution = "Pexels (Licence libre)",
            sourceUrl = "https://www.pexels.com/video/5438889/",
            verdictExplanation = "MENSONGE — Il cligne des yeux de manière excessive lors de l'affirmation d'indépendance — un signe de stress lié au mensonge direct. Son auto-contact répété et son micro-mépris envers le juge montrent qu'il se croit au-dessus de la vérification.",
            hints = listOf(
                "Un clignement excessif peut indiquer un stress lié au mensonge.",
                "L'auto-contact répété révèle-t-il une anxiété croissante ?",
                "Observe le mépris envers les questions sur son indépendance.",
                "Ses gestes se ferment-ils (bras croisés, mains jointes) lors des affirmations clés ?",
                "Le contact visuel se fragmente-t-il sous la pression directe ?"
            ),
            truthTags = listOf(
                TruthTag(MicroExpressionType.EYE_BLOCK, 4000, "Clignements excessifs — stress lors de la déclaration d'indépendance."),
                TruthTag(MicroExpressionType.SELF_TOUCH, 11000, "Auto-contact répété — anxiété croissante sous l'interrogatoire."),
                TruthTag(MicroExpressionType.MICRO_CONTEMPT, 19000, "Micro-mépris envers le juge — se pense intouchable.")
            )
        ),

        VideoChallenge(
            id = "archive_20",
            title = "La Secrétaire Fidèle",
            videoUrl = "https://videos.pexels.com/video-files/5438897/5438897-sd_640_360_25fps.mp4",
            durationMs = 40000,
            isLie = false,
            difficulty = Difficulty.MEDIUM,
            storyPrompt = "« Ce document, je l'ai classé moi-même. Il était là le lendemain matin. »",
            archiveContext = "Enquête interne filmée — Une secrétaire témoigne sur la chaîne de traitement d'un dossier sensible.",
            sourceAttribution = "Pexels (Licence libre)",
            sourceUrl = "https://www.pexels.com/video/5438897/",
            verdictExplanation = "VÉRITÉ — Elle décrit une action routinière avec la fluidité naturelle d'un souvenir réel. Ses mouvements oculaires vers la gauche (rappel visuel) sont cohérents avec la reconstruction d'un acte mémoriel, et son débit est régulier sans surexplication.",
            hints = listOf(
                "Les mouvements oculaires vers la gauche indiquent souvent un rappel visuel réel.",
                "La description d'un acte routinier est-elle fluide et naturelle ?",
                "Cherche une surexplication — signe possible de fabrication.",
                "Le ton de voix reste-t-il stable même sous pression ?",
                "Ses gestes 'montrent'-ils la scène qu'elle décrit ?"
            ),
            truthTags = listOf()
        ),

        VideoChallenge(
            id = "archive_21",
            title = "Le Lobbyiste Pris",
            videoUrl = "https://videos.pexels.com/video-files/8103033/8103033-sd_640_360_25fps.mp4",
            durationMs = 43000,
            isLie = true,
            difficulty = Difficulty.MEDIUM,
            storyPrompt = "« Je n'ai jamais exercé de pression sur ce ministère. Ce sont des accusations sans fondement. »",
            archiveContext = "Commission parlementaire d'enquête — Un lobbyiste nie toute intervention illégale auprès d'un ministre.",
            sourceAttribution = "Pexels (Licence libre)",
            sourceUrl = "https://www.pexels.com/video/8103033/",
            verdictExplanation = "MENSONGE — Il qualifie les accusations de 'sans fondement' avec un sourire de mépris fugace, révélant qu'il les trouve au contraire très fondées. Sa compression des lèvres et son incongruence de tête sur 'jamais' sont les deux marqueurs classiques du déni construit.",
            hints = listOf(
                "Un sourire lors de la qualification 'sans fondement' est très révélateur.",
                "Le mot 'jamais' est-il accompagné d'un signal corporel ?",
                "La compression des lèvres indique-t-elle une rétention d'information ?",
                "Observe la tête lors des dénégations les plus fortes.",
                "La gestuelle se ferme-t-elle progressivement pendant l'interrogatoire ?"
            ),
            truthTags = listOf(
                TruthTag(MicroExpressionType.MICRO_CONTEMPT, 3000, "Sourire de mépris en qualifiant les accusations de 'sans fondement'."),
                TruthTag(MicroExpressionType.LIP_COMPRESS, 12000, "Lèvres pincées — retient des détails sur ses contacts ministériels."),
                TruthTag(MicroExpressionType.HEAD_INCONGRUENCE, 22000, "Incongruence — dit 'jamais' en acquiesçant légèrement.")
            )
        ),

        VideoChallenge(
            id = "archive_22",
            title = "L'Ingénieur Transparent",
            videoUrl = "https://videos.pexels.com/video-files/5438975/5438975-sd_640_360_25fps.mp4",
            durationMs = 34000,
            isLie = false,
            difficulty = Difficulty.MEDIUM,
            storyPrompt = "« J'ai signalé le défaut structural à ma hiérarchie dès que je l'ai découvert. »",
            archiveContext = "Enquête de sécurité filmée — Un ingénieur témoigne sur le moment où il a alerté sa direction.",
            sourceAttribution = "Pexels (Licence libre)",
            sourceUrl = "https://www.pexels.com/video/5438975/",
            verdictExplanation = "VÉRITÉ — Il témoigne avec une légère tension émotionnelle appropriée à quelqu'un qui a pris un risque professionnel. Ses gestes illustrent le moment de la découverte, ses yeux cherchent le souvenir vers la gauche-haut (rappel visuel réel), et son récit est chronologiquement ancré.",
            hints = listOf(
                "Une tension légère est-elle appropriée au contexte ou excessive ?",
                "Les yeux cherchent-ils le souvenir dans la bonne direction pour un rappel réel ?",
                "Le récit est-il ancré temporellement de manière naturelle ?",
                "Ses gestes décrivent-ils la découverte physiquement ?",
                "L'émotion affichée (inquiétude professionnelle) est-elle cohérente ?"
            ),
            truthTags = listOf()
        ),

        VideoChallenge(
            id = "archive_23",
            title = "La Responsable Évasive",
            videoUrl = "https://videos.pexels.com/video-files/9365158/9365158-sd_640_360_25fps.mp4",
            durationMs = 33000,
            isLie = true,
            difficulty = Difficulty.MEDIUM,
            storyPrompt = "« Je n'étais pas au courant de ces pratiques. Mon équipe agissait de sa propre initiative. »",
            archiveContext = "Audition de direction filmée — Une responsable RH se distancie des pratiques de harcèlement de son équipe.",
            sourceAttribution = "Pexels (Licence libre)",
            sourceUrl = "https://www.pexels.com/video/9365158/",
            verdictExplanation = "MENSONGE — Sa distanciation verbale ('mon équipe agissait') est accompagnée d'un micro-mépris envers les victimes, révélant une indifférence camouflée. Son blocage oculaire survient précisément sur 'je n'étais pas au courant', et son auto-contact trahit le stress du mensonge hiérarchique.",
            hints = listOf(
                "La distanciation verbale ('mon équipe') peut masquer une implication directe.",
                "Un micro-mépris envers les victimes est un signal très fort.",
                "Le blocage oculaire survient-il sur les affirmations d'ignorance ?",
                "L'auto-contact augmente-t-il avec les détails des pratiques signalées ?",
                "Compare le ton quand elle parle d'elle-même vs. de son équipe."
            ),
            truthTags = listOf(
                TruthTag(MicroExpressionType.MICRO_CONTEMPT, 4000, "Micro-mépris en évoquant les victimes — indifférence masquée."),
                TruthTag(MicroExpressionType.EYE_BLOCK, 14000, "Blocage oculaire sur 'je n'étais pas au courant' — nier ce qu'elle sait."),
                TruthTag(MicroExpressionType.SELF_TOUCH, 23000, "Auto-contact — stress lors du détail des pratiques qu'elle supervisait.")
            )
        ),

        // ── NIVEAU DIFFICILE ─────────────────────────────────────────

        VideoChallenge(
            id = "archive_05",
            title = "L'Héritier Suspect",
            videoUrl = "https://videos.pexels.com/video-files/5438948/5438948-sd_640_360_25fps.mp4",
            durationMs = 45000,
            isLie = true,
            difficulty = Difficulty.HARD,
            storyPrompt = "« Mon oncle était un homme bon. Sa mort m'a dévasté. »",
            archiveContext = "Audition filmée — L'héritier principal est interrogé sur la mort suspecte de son oncle.",
            sourceAttribution = "Pexels (Licence libre)",
            sourceUrl = "https://www.pexels.com/video/5438948/",
            verdictExplanation = "MENSONGE — L'héritier affiche un micro-mépris révélateur quand il parle de la 'bonté' de son oncle. Ses blocages oculaires prolongés montrent une déconnexion émotionnelle — il ne ressent pas la dévastation qu'il décrit. La compression des lèvres avant de mentionner l'héritage trahit la vraie motivation, et l'incongruence finale ('je l'aimais' + secouement de tête) est la signature classique du mensonge.",
            hints = listOf(
                "Un sourire asymétrique peut trahir une émotion cachée.",
                "Observez la durée des fermetures de paupières.",
                "Les lèvres peuvent se comprimer avant un mensonge clé.",
                "Surveillez les gestes d'auto-apaisement au niveau du cou.",
                "Comparez la direction de la tête avec les déclarations d'amour."
            ),
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
            videoUrl = "https://videos.pexels.com/video-files/5438885/5438885-sd_640_360_25fps.mp4",
            durationMs = 31000,
            isLie = true,
            difficulty = Difficulty.HARD,
            storyPrompt = "« Je n'ai aucun lien avec cette organisation. »",
            archiveContext = "Commission d'enquête — Un homme d'affaires nie toute connexion avec un réseau criminel.",
            sourceAttribution = "Pexels (Licence libre)",
            sourceUrl = "https://www.pexels.com/video/5438885/",
            verdictExplanation = "MENSONGE — L'homme d'affaires bloque immédiatement ses yeux dès la première question, signe d'un réflexe de protection. Le grattage derrière l'oreille signale un stress intense, les lèvres pincées indiquent la rétention d'informations compromettantes, et son micro-mépris montre qu'il se croit supérieur à l'enquêteur. La double incongruence finale — dire 'absolument pas' en acquiesçant — est irréfutable.",
            hints = listOf(
                "Les yeux se ferment-ils brusquement aux questions directes ?",
                "Remarquez les contacts main-oreille ou main-cou.",
                "Les lèvres se pincent-elles avant certaines réponses ?",
                "Cherchez un sourire asymétrique de supériorité.",
                "Observez la tête quand il dit 'absolument pas'."
            ),
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
            videoUrl = "https://videos.pexels.com/video-files/5438950/5438950-sd_640_360_25fps.mp4",
            durationMs = 31000,
            isLie = false,
            difficulty = Difficulty.HARD,
            storyPrompt = "« J'ai mené cette enquête selon les règles. Chaque preuve est documentée. »",
            archiveContext = "Rapport de police filmé — L'inspecteur présente méthodiquement son enquête.",
            sourceAttribution = "Pexels (Licence libre)",
            sourceUrl = "https://www.pexels.com/video/5438950/",
            verdictExplanation = "VÉRITÉ — L'inspecteur présente son rapport avec méthode et assurance. Son langage corporel est ouvert et cohérent avec ses propos. Il maintient un contact visuel naturel (sans fixation anormale), ses gestes sont illustratifs (accompagnent le discours) plutôt que d'auto-apaisement, et son débit est régulier. Aucune fuite comportementale détectée.",
            hints = listOf(
                "Le contact visuel est-il maintenu naturellement ?",
                "Les gestes accompagnent-ils le discours de manière illustrative ?",
                "Le débit de parole est-il régulier ou présente-t-il des ruptures ?",
                "La posture reste-t-elle ouverte et cohérente ?",
                "Cherchez des signes de stress disproportionné par rapport au contexte."
            ),
            truthTags = listOf() // Vérité — rapport honnête et méthodique
        ),

        VideoChallenge(
            id = "archive_24",
            title = "Le Diplomate Corrompu",
            videoUrl = "https://videos.pexels.com/video-files/5330655/5330655-sd_640_360_25fps.mp4",
            durationMs = 31000,
            isLie = true,
            difficulty = Difficulty.HARD,
            storyPrompt = "« Ces transferts correspondent à des dons légaux d'une fondation caritative. »",
            archiveContext = "Commission anti-corruption — Un diplomate justifie des virements de plusieurs millions.",
            sourceAttribution = "Pexels (Licence libre)",
            sourceUrl = "https://www.pexels.com/video/5330655/",
            verdictExplanation = "MENSONGE — Sa maîtrise de façade est trahie par des fuites microscopiques. Le micro-mépris apparaît précisément au mot 'caritative', révélant la moquerie intérieure. Son blocage oculaire bref mais répété sur les montants indique la construction d'une justification fictive. L'incongruence tête-message sur 'légaux' est imperceptible mais irréfutable.",
            hints = listOf(
                "Un menteur expérimenté contrôle son corps — cherche les fuites microscopiques.",
                "Le mot 'caritative' provoque-t-il une réaction faciale fugace ?",
                "Les blocages oculaires brefs et répétés diffèrent-ils d'un blocage unique ?",
                "L'incongruence de tête est-elle visible sur les mots à haute charge légale ?",
                "Compare l'intensité émotionnelle entre les mots neutres et les mots clés."
            ),
            truthTags = listOf(
                TruthTag(MicroExpressionType.MICRO_CONTEMPT, 4000, "Micro-mépris fugace sur le mot 'caritative' — moquerie intérieure de son propre mensonge."),
                TruthTag(MicroExpressionType.EYE_BLOCK, 10000, "Blocages oculaires répétés et brefs sur les montants — construction de justifications."),
                TruthTag(MicroExpressionType.HEAD_INCONGRUENCE, 18000, "Incongruence imperceptible — acquiesce en niant sur le mot 'légaux'."),
                TruthTag(MicroExpressionType.LIP_COMPRESS, 25000, "Compression minimale des lèvres — signal résiduel d'un menteur entraîné.")
            )
        ),

        VideoChallenge(
            id = "archive_25",
            title = "Le Lanceur d'Alerte",
            videoUrl = "https://videos.pexels.com/video-files/5330648/5330648-sd_640_360_25fps.mp4",
            durationMs = 31000,
            isLie = false,
            difficulty = Difficulty.HARD,
            storyPrompt = "« J'ai enregistré ces conversations parce que je savais qu'on me ferait taire. »",
            archiveContext = "Déposition protégée filmée — Un lanceur d'alerte justifie ses méthodes d'enregistrement.",
            sourceAttribution = "Pexels (Licence libre)",
            sourceUrl = "https://www.pexels.com/video/5330648/",
            verdictExplanation = "VÉRITÉ — Il presente la tension d'une vraie peur, pas d'un mensonge. Ses signaux de stress sont cohérents avec une personne exposée (regard mobile, contrôle de la voix) mais aucun ne correspond aux patrons de tromperie. L'émotion de peur authentique est très différente du stress du mensonge.",
            hints = listOf(
                "La peur authentique et le stress du mensonge produisent des signaux différents.",
                "Ses mouvements oculaires sont-ils ceux d'une vigilance ou d'une construction fictive ?",
                "La tension dans la voix est-elle constante ou apparaît-elle sur des mots précis ?",
                "Cherche la cohérence entre le contenu émotionnel et les micro-expressions.",
                "Un vrai lanceur d'alerte peut-il présenter des signaux qui ressemblent à un mensonge ?"
            ),
            truthTags = listOf()
        ),

        VideoChallenge(
            id = "archive_26",
            title = "L'Administrateur Fantôme",
            videoUrl = "https://videos.pexels.com/video-files/5977267/5977267-sd_640_360_25fps.mp4",
            durationMs = 30000,
            isLie = true,
            difficulty = Difficulty.HARD,
            storyPrompt = "« Je n'ai aucun accès privilégié aux systèmes. Mon rôle est purement consultatif. »",
            archiveContext = "Enquête de cybersécurité filmée — Un administrateur informatique nie la portée de ses droits d'accès.",
            sourceAttribution = "Pexels (Licence libre)",
            sourceUrl = "https://www.pexels.com/video/5977267/",
            verdictExplanation = "MENSONGE — Sa négation est formulée dans un registre ultra-technique pour noyer l'information — un mécanisme d'obfuscation verbale. Pourtant, ses micro-expressions contredisent cette maîtrise : micro-mépris sur 'consultatif' (il sait exactement ce qu'il peut faire), incongruence tête sur 'aucun accès'.",
            hints = listOf(
                "L'obfuscation technique peut masquer un mensonge sous la complexité.",
                "Le micro-mépris sur un terme précis (consultatif) révèle quoi ?",
                "L'incongruence de tête sur 'aucun accès' est-elle visible ?",
                "La fluidité technique inhabituelle peut-elle être un signe de récitation préparée ?",
                "Cherche le moment où la maîtrise de façade se fissure brièvement."
            ),
            truthTags = listOf(
                TruthTag(MicroExpressionType.MICRO_CONTEMPT, 6000, "Micro-mépris sur 'consultatif' — il sait exactement l'étendue réelle de ses droits."),
                TruthTag(MicroExpressionType.HEAD_INCONGRUENCE, 14000, "Incongruence sur 'aucun accès' — sa tête acquiesce imperceptiblement."),
                TruthTag(MicroExpressionType.EYE_BLOCK, 22000, "Blocage oculaire bref — fissure dans la façade lors d'une question directe.")
            )
        ),

        VideoChallenge(
            id = "archive_27",
            title = "La Psychiatre Intègre",
            videoUrl = "https://videos.pexels.com/video-files/5977265/5977265-sd_640_360_25fps.mp4",
            durationMs = 29000,
            isLie = false,
            difficulty = Difficulty.HARD,
            storyPrompt = "« Mon patient m'a bien signifié son refus. J'ai respecté sa décision. »",
            archiveContext = "Conseil de l'Ordre filmé — Une psychiatre défend une décision thérapeutique contestée.",
            sourceAttribution = "Pexels (Licence libre)",
            sourceUrl = "https://www.pexels.com/video/5977265/",
            verdictExplanation = "VÉRITÉ — Malgré la pression institutionnelle, sa cohérence comportementale est totale. La légère tristesse visible est une émotion congruente (elle regrette sincèrement), pas une façade. Ses micro-expressions fugaces de tristesse vraie sont incompatibles avec les patrons du mensonge.",
            hints = listOf(
                "La tristesse visible est-elle congruente avec le contexte ou simulée ?",
                "Une vraie émotion de regret a une signature micro-expressive différente du mensonge.",
                "Cherche des asymétries faciales — les émotions vraies sont souvent symétriques.",
                "Le maintien de la posture sous pression institutionnelle signifie-t-il quelque chose ?",
                "La légère hésitation est-elle celle d'une personne qui cherche ses mots ou qui construit un récit ?"
            ),
            truthTags = listOf()
        ),

        VideoChallenge(
            id = "archive_28",
            title = "Le PDG Insaisissable",
            videoUrl = "https://videos.pexels.com/video-files/5439067/5439067-sd_640_360_25fps.mp4",
            durationMs = 29000,
            isLie = true,
            difficulty = Difficulty.HARD,
            storyPrompt = "« Notre politique de données est irréprochable. Nous n'avons jamais vendu d'informations utilisateurs. »",
            archiveContext = "Audition parlementaire filmée — Le PDG d'une plateforme numérique répond sur la protection des données.",
            sourceAttribution = "Pexels (Licence libre)",
            sourceUrl = "https://www.pexels.com/video/5439067/",
            verdictExplanation = "MENSONGE — C'est un menteur de haut niveau, entraîné aux auditions. Ses fuites sont quasi imperceptibles : un micro-mépris d'une fraction de seconde sur 'irréprochable', une légère rétraction de la mâchoire sur 'jamais vendu'. Son seul trahisseur visible : l'incongruence entre la sérénité affichée et la légère accélération respiratoire.",
            hints = listOf(
                "Un menteur de haut niveau réduit ses signaux au minimum — cherche les fractionnels.",
                "L'accélération respiratoire visible (épaules) est-elle cohérente avec le calme affiché ?",
                "Le micro-mépris sur 'irréprochable' dure moins d'1/5 de seconde — détectes-tu quelque chose ?",
                "La rétraction de mâchoire sur 'jamais vendu' est un signal involontaire rare.",
                "Compare l'intensité des réponses sur les questions neutres vs. les questions directes."
            ),
            truthTags = listOf(
                TruthTag(MicroExpressionType.MICRO_CONTEMPT, 3000, "Micro-mépris fractionnel sur 'irréprochable' — <200ms, presque indétectable."),
                TruthTag(MicroExpressionType.SELF_TOUCH, 13000, "Légère tension dans la posture — accélération respiratoire sous le calme de façade."),
                TruthTag(MicroExpressionType.HEAD_INCONGRUENCE, 23000, "Incongruence minimale sur 'jamais vendu' — le signal le plus discret du dossier.")
            )
        ),

        VideoChallenge(
            id = "archive_29",
            title = "Le Vétéran Fiable",
            videoUrl = "https://videos.pexels.com/video-files/3255321/3255321-sd_640_360_25fps.mp4",
            durationMs = 26000,
            isLie = false,
            difficulty = Difficulty.HARD,
            storyPrompt = "« Cet ordre de tir venait du haut. Je n'avais aucune marge de manœuvre. »",
            archiveContext = "Tribunal militaire filmé — Un vétéran témoigne sur une opération controversée.",
            sourceAttribution = "Pexels (Licence libre)",
            sourceUrl = "https://www.pexels.com/video/3255321/",
            verdictExplanation = "VÉRITÉ — Son témoignage porte la marque d'une culpabilité authentique, pas d'un mensonge. La tristesse et la honte qu'il présente sont des émotions complexes congruentes avec ce qu'il décrit. Ses moments de silence ne sont pas des pauses de construction — ce sont des pauses d'émotion réelle.",
            hints = listOf(
                "La honte authentique produit-elle les mêmes signaux que le mensonge ?",
                "Ses pauses sont-elles des pauses de construction fictive ou d'émotion réelle ?",
                "La tristesse visible est-elle symétrique (vraie) ou asymétrique (feinte) ?",
                "Le contexte militaire influence-t-il la manière dont les émotions sont exprimées ?",
                "Cherche des gestes d'auto-apaisement dus à la douleur plutôt qu'au mensonge."
            ),
            truthTags = listOf()
        ),

        VideoChallenge(
            id = "archive_30",
            title = "Le Consultant Fantôme",
            videoUrl = "https://videos.pexels.com/video-files/5438890/5438890-sd_640_360_25fps.mp4",
            durationMs = 31000,
            isLie = true,
            difficulty = Difficulty.HARD,
            storyPrompt = "« J'ai livré exactement ce qui était commandé. Le rapport final est conforme au cahier des charges. »",
            archiveContext = "Litige de prestation filmé — Un consultant défend la valeur d'un rapport facturé 400 000 €.",
            sourceAttribution = "Pexels (Licence libre)",
            sourceUrl = "https://www.pexels.com/video/5438890/",
            verdictExplanation = "MENSONGE — Il confond précision et vérité. Son récit est parfaitement articulé (trop récité), et ses micro-expressions trahissent une maîtrise apprise : le mépris envers le client qui 'ne comprendrait pas', les lèvres légèrement pincées sur les termes contractuels. La fissure finale — un soupir rapide avant la conclusion — trahit le soulagement prématuré du menteur.",
            hints = listOf(
                "Un discours trop parfaitement articulé peut indiquer une récitation préparée.",
                "Le mépris envers le client qui 'ne comprendrait pas' révèle quel état d'esprit ?",
                "Les lèvres se pincent-elles légèrement sur les termes contractuels précis ?",
                "Un soupir rapide avant la conclusion peut trahir quel sentiment ?",
                "Compare la fluidité en début de réponse vs. à la fin — y a-t-il une dégradation ?"
            ),
            truthTags = listOf(
                TruthTag(MicroExpressionType.MICRO_CONTEMPT, 5000, "Mépris envers le client — le juge intellectuellement incapable de contester."),
                TruthTag(MicroExpressionType.LIP_COMPRESS, 14000, "Légère compression sur les termes contractuels — retient les détails de non-conformité."),
                TruthTag(MicroExpressionType.SELF_TOUCH, 25000, "Soupir rapide avant la conclusion — soulagement prématuré du menteur qui croit avoir convaincu.")
            )
        )
    )

    private val playedIds = mutableSetOf<String>()

    // ==========================================================================
    // VIDÉOS D'ARCHIVE EXCLUSIVES — Cas du Jour quotidien
    // Rotation déterministe par date (epochDay % size) — jamais mélangées avec sampleVideos
    // ==========================================================================

    private val archiveDailyVideos = listOf(

        VideoChallenge(
            id = "daily_01",
            title = "Le Serment Brisé",
            videoUrl = "https://videos.pexels.com/video-files/8873201/8873201-sd_640_360_25fps.mp4",
            durationMs = 25000,
            isLie = true,
            difficulty = Difficulty.MEDIUM,
            storyPrompt = "« Je n'ai jamais reçu cet argent. Je le jure sur mes enfants. »",
            archiveContext = "Archive exclusive · Déposition filmée — Un comptable nie avoir détourné des fonds.",
            sourceAttribution = "Pexels (Licence libre)",
            sourceUrl = "https://www.pexels.com/video/8873201/",
            verdictExplanation = "MENSONGE — Il jure sur ses enfants mais son regard fuit à gauche au moment précis du serment. La compression des lèvres juste avant de mentionner 'l'argent' trahit la retenue d'informations.",
            hints = listOf(
                "Observe attentivement la direction du regard pendant le serment.",
                "Les lèvres peuvent trahir une retenue d'informations.",
                "Un geste d'auto-contact révèle souvent du stress caché.",
                "Compare le discours verbal avec le langage corporel.",
                "Les serments solennels sont souvent un mécanisme de surcompensation."
            ),
            truthTags = listOf(
                TruthTag(MicroExpressionType.EYE_BLOCK, 4000, "Regard fuyant au moment du serment — construction mentale d'un faux souvenir."),
                TruthTag(MicroExpressionType.LIP_COMPRESS, 11000, "Lèvres pincées — retient des informations sur les virements."),
                TruthTag(MicroExpressionType.SELF_TOUCH, 19000, "Main au menton — stress lors de la mention du montant exact.")
            )
        ),

        VideoChallenge(
            id = "daily_02",
            title = "La Marchande d'Ombres",
            videoUrl = "https://videos.pexels.com/video-files/3205627/3205627-sd_640_360_25fps.mp4",
            durationMs = 24000,
            isLie = false,
            difficulty = Difficulty.EASY,
            storyPrompt = "« Je gère cette boutique depuis dix ans. Chaque centime est déclaré. »",
            archiveContext = "Archive exclusive · Audition fiscale filmée — La gérante présente ses comptes.",
            sourceAttribution = "Pexels (Licence libre)",
            sourceUrl = "https://www.pexels.com/video/3205627/",
            verdictExplanation = "VÉRITÉ — Son discours est fluide, ses gestes ouverts (paumes visibles), aucun blocage. Elle maintient un contact visuel naturel sans le sur-jeu du menteur.",
            hints = listOf(
                "Note si les gestes sont ouverts ou fermés (paumes visibles ?).",
                "Un discours fluide sans hésitation est un bon indicateur.",
                "Le contact visuel naturel ne fixe pas : il bouge régulièrement.",
                "L'absence de gestes d'auto-apaisement est significative.",
                "Compare la cohérence entre le ton de voix et les expressions faciales."
            ),
            truthTags = listOf()
        ),

        VideoChallenge(
            id = "daily_03",
            title = "L'Associé Fantôme",
            videoUrl = "https://videos.pexels.com/video-files/8873198/8873198-sd_640_360_25fps.mp4",
            durationMs = 22000,
            isLie = true,
            difficulty = Difficulty.HARD,
            storyPrompt = "« Mon associé gérait tout. Je n'avais aucune connaissance des transactions. »",
            archiveContext = "Archive exclusive · Commission parlementaire — Un dirigeant dément toute implication.",
            sourceAttribution = "Pexels (Licence libre)",
            sourceUrl = "https://www.pexels.com/video/8873198/",
            verdictExplanation = "MENSONGE — Il se distancie verbalement de son associé mais son micro-mépris révèle qu'il le méprise en réalité depuis l'intérieur. L'incongruence tête-message (dit 'non' en hochant 'oui') sur les transactions est implacable.",
            hints = listOf(
                "Le mépris envers un tiers absent peut trahir une implication partagée.",
                "Quand quelqu'un nie, observe si sa tête acquiesce ou nie en même temps.",
                "Un blocage oculaire prolongé peut indiquer la construction d'un récit.",
                "Le stress au cou se manifeste souvent lors de questions financières.",
                "La distanciation verbale (« LUI gérait ») est un mécanisme classique de déni."
            ),
            truthTags = listOf(
                TruthTag(MicroExpressionType.MICRO_CONTEMPT, 5000, "Micro-mépris envers son propre 'associé' — il le méprise car c'est son bouc émissaire."),
                TruthTag(MicroExpressionType.HEAD_INCONGRUENCE, 13000, "Incongruence — dit 'aucune connaissance' en hochant imperceptiblement la tête."),
                TruthTag(MicroExpressionType.EYE_BLOCK, 20000, "Blocage oculaire prolongé — il construit mentalement sa dénégation."),
                TruthTag(MicroExpressionType.SELF_TOUCH, 20000, "Grattage du cou — stress maximal sur la question des virements offshore.")
            )
        ),

        VideoChallenge(
            id = "daily_04",
            title = "La Dernière Nuit",
            videoUrl = "https://videos.pexels.com/video-files/5977260/5977260-sd_640_360_25fps.mp4",
            durationMs = 18000,
            isLie = true,
            difficulty = Difficulty.MEDIUM,
            storyPrompt = "« J'étais chez moi toute la nuit. Je me suis couché à 22h. »",
            archiveContext = "Archive exclusive · Interrogatoire nocturne — Le principal suspect d'une disparition présente son alibi.",
            sourceAttribution = "Pexels (Licence libre)",
            sourceUrl = "https://www.pexels.com/video/5977260/",
            verdictExplanation = "MENSONGE — Il présente un alibi trop précis (heure exacte, détails minutieux) — signe classique de fabrication. Ses lèvres pincées avant chaque détail temporel trahissent la construction consciente.",
            hints = listOf(
                "Un alibi trop détaillé et précis peut être un signe de fabrication.",
                "Observe les lèvres juste avant chaque détail temporel.",
                "Le regard qui se bloque peut indiquer la visualisation d'un faux souvenir.",
                "Les gestes d'auto-contact augmentent avec le stress du mensonge.",
                "Quand on affirme quelque chose de vrai, la tête acquiesce naturellement."
            ),
            truthTags = listOf(
                TruthTag(MicroExpressionType.LIP_COMPRESS, 3000, "Lèvres pincées avant de donner l'heure — il choisit ses mots."),
                TruthTag(MicroExpressionType.EYE_BLOCK, 9000, "Blocage — il 'voit' mentalement son alibi fabriqué."),
                TruthTag(MicroExpressionType.SELF_TOUCH, 17000, "Auto-contact — stress en donnant des détails trop précis sur le soir."),
                TruthTag(MicroExpressionType.HEAD_INCONGRUENCE, 16000, "Hoche la tête 'non' en affirmant son alibi.")
            )
        ),

        VideoChallenge(
            id = "daily_05",
            title = "Le Médecin Silencieux",
            videoUrl = "https://videos.pexels.com/video-files/5439068/5439068-sd_640_360_25fps.mp4",
            durationMs = 19000,
            isLie = false,
            difficulty = Difficulty.HARD,
            storyPrompt = "« J'ai suivi les protocoles à la lettre. Ma conscience est nette. »",
            archiveContext = "Archive exclusive · Comité d'éthique médicale — Un médecin répond d'une procédure controversée.",
            sourceAttribution = "Pexels (Licence libre)",
            sourceUrl = "https://www.pexels.com/video/5439068/",
            verdictExplanation = "VÉRITÉ — Malgré la pression, son comportement reste congruent. Son stress visible (normal face à un comité) ne s'accompagne d'aucune des micro-expressions de tromperie. Il dit la vérité avec conviction.",
            hints = listOf(
                "Le stress est normal en situation de pression — il ne prouve pas le mensonge.",
                "Cherche une congruence entre les mots et le langage corporel.",
                "L'absence de gestes manipulateurs est un signe fort de sincérité.",
                "Un contact visuel régulier (ni trop fixe, ni fuyant) indique souvent la vérité.",
                "La conviction n'est pas de l'agressivité : observe le calme intérieur."
            ),
            truthTags = listOf()
        ),

        VideoChallenge(
            id = "daily_06",
            title = "L'Héritière Prudente",
            videoUrl = "https://videos.pexels.com/video-files/5977451/5977451-sd_640_360_25fps.mp4",
            durationMs = 25000,
            isLie = true,
            difficulty = Difficulty.EASY,
            storyPrompt = "« Je n'étais pas au courant du testament. C'est une surprise totale. »",
            archiveContext = "Archive exclusive · Notariat filmé — L'héritière découvre officellement le testament.",
            sourceAttribution = "Pexels (Licence libre)",
            sourceUrl = "https://www.pexels.com/video/5977451/",
            verdictExplanation = "MENSONGE — Son sourire de satisfaction (micro-mépris) apparaît 0.3 secondes AVANT la lecture du montant — elle le connaissait déjà. Ses lèvres pincées trahissent la retenue d'une satisfaction anticipée.",
            hints = listOf(
                "Une émotion qui apparaît AVANT l'événement révèle une connaissance préalable.",
                "Le micro-sourire de satisfaction est différent du vrai sourire de surprise.",
                "Les lèvres pincées peuvent retenir une émotion qu'on veut cacher.",
                "Observe le timing : la surprise authentique est toujours en retard, jamais en avance.",
                "La tête peut acquiescer alors que les mots expriment la surprise."
            ),
            truthTags = listOf(
                TruthTag(MicroExpressionType.MICRO_CONTEMPT, 10000, "Micro-sourire de satisfaction AVANT la révélation — elle connaît déjà le montant."),
                TruthTag(MicroExpressionType.LIP_COMPRESS, 18000, "Retient sa satisfaction en jouant la surprise."),
                TruthTag(MicroExpressionType.HEAD_INCONGRUENCE, 23000, "Sa tête fait 'oui' quand elle dit être 'surprise'.")
            )
        ),

        VideoChallenge(
            id = "daily_07",
            title = "Le Passager Clandestin",
            videoUrl = "https://videos.pexels.com/video-files/5439078/5439078-sd_640_360_25fps.mp4",
            durationMs = 25000,
            isLie = true,
            difficulty = Difficulty.MEDIUM,
            storyPrompt = "« Je ne connais pas cet homme. Je ne l'ai jamais vu. »",
            archiveContext = "Archive exclusive · Interrogatoire douanier — Un passager nie connaître un individu recherché.",
            sourceAttribution = "Pexels (Licence libre)",
            sourceUrl = "https://www.pexels.com/video/5439078/",
            verdictExplanation = "MENSONGE — Le blocage oculaire au moment de prononcer 'jamais' est un signal classique de déni fabriqué. Son auto-contact (main au cou) au moment de la photo de l'individu révèle une réponse émotionnelle réelle.",
            hints = listOf(
                "Le mot 'jamais' est souvent accompagné d'un signal corporel quand il est faux.",
                "Un blocage des yeux peut indiquer le rejet d'une information dérangeante.",
                "La main au cou est un geste d'auto-apaisement classique sous pression.",
                "Les lèvres pincées retiennent les mots qu'on ne veut pas prononcer.",
                "Observe la réaction émotionnelle au moment où une preuve est présentée."
            ),
            truthTags = listOf(
                TruthTag(MicroExpressionType.EYE_BLOCK, 5000, "Blocage oculaire prolongé en disant 'jamais vu'."),
                TruthTag(MicroExpressionType.SELF_TOUCH, 12000, "Main au cou à la mention de l'individu recherché."),
                TruthTag(MicroExpressionType.LIP_COMPRESS, 20000, "Lèvres pincées — retient des informations sur la rencontre.")
            )
        )
    )

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
        return sampleVideos.find { it.id == id } ?: archiveDailyVideos.find { it.id == id }
    }

    override fun getAllChallenges(): List<VideoChallenge> = sampleVideos

    override suspend fun getDailyCaseChallenge(): VideoChallenge {
        val epochDay = LocalDate.now().toEpochDay()
        val index = (epochDay % archiveDailyVideos.size).toInt()
        return archiveDailyVideos[index]
    }
}
