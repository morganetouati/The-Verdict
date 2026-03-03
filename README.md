# The Verdict 🔍

> *Lis dans les gens comme dans un livre ouvert.*

[![Android](https://img.shields.io/badge/Platform-Android%2026%2B-3DDC84?logo=android)](https://developer.android.com)
[![Kotlin](https://img.shields.io/badge/Kotlin-2.0.21-7F52FF?logo=kotlin)](https://kotlinlang.org)
[![Jetpack Compose](https://img.shields.io/badge/Jetpack%20Compose-2024.12.01-4285F4?logo=jetpackcompose)](https://developer.android.com/jetpack/compose)
[![License](https://img.shields.io/badge/License-MIT-blue)](LICENSE)

---

## Concept

**The Verdict** est un jeu mobile d'entraînement à la lecture comportementale.  
À chaque partie, une vidéo réelle te présente un individu en train de s'exprimer.  
Ton rôle : détecter les micro-signaux (gestes, regards, hésitations) en temps réel,  
puis rendre ton verdict — **Vérité** ou **Mensonge**.

Plus tu joues, plus ton rang de Mentaliste progresse.

---

## Architecture

```
app/src/main/java/com/theverdict/app/
├── data/
│   ├── ads/           AdManager.kt              (AdMob rewarded + crédibilité)
│   ├── audio/         DetectionSoundManager.kt  (AudioTrack 880 Hz ping)
│   ├── local/         PreferencesManager.kt     (DataStore – profil, quotidien, crédibilité)
│   └── repository/    VideoRepositoryImpl.kt    (pool normal + archive quotidienne)
│                      PlayerRepositoryImpl.kt   (profil, XP, streak, crédibilité)
├── domain/
│   ├── model/         PlayerProfile, VideoChallenge, MentalistRank
│   └── repository/    VideoRepository, PlayerRepository  (interfaces)
└── ui/
    ├── components/    DetectionBar, TagTimeline  (feedback sensoriel)
    ├── navigation/    NavGraph.kt
    ├── screens/
    │   ├── home/      HomeScreen + HomeViewModel
    │   ├── video/     VideoScreen + VideoViewModel
    │   ├── verdict/   VerdictScreen + VerdictViewModel
    │   ├── profile/   ProfileScreen + ProfileViewModel
    │   └── lessons/   LessonsScreen
    └── theme/         Color, Type, Theme
```

Pattern : **MVVM** — StateFlow + Hilt-free manual DI via `ViewModelProvider.Factory`.

---

## Fonctionnalités

### 🎬 Analyses quotidiennes
- Jusqu'à 3 parties par jour sur le pool normal de vidéos.
- Chaque partie génère de l'XP et fait progresser ton rang de Mentaliste.

### ⭐ Cas du Jour
- Une vidéo d'archive **exclusive** est déverrouillée chaque jour à minuit (déterministe : `epochDay % 7`).
- Ne peut être jouée qu'**une seule fois** par jour.
- Un compte à rebours indique le temps avant la prochaine vidéo.

### 🔥 Flamme de Lucidité
- Système de série quotidienne.
- La flamme grossit et change de couleur avec la longueur de la série (dorée → orange → rouge).
- Une alerte s'affiche si la série est brisée.

### 📊 Rang de Mentaliste (15 niveaux)
| Rang | Badge | XP requis |
|------|-------|-----------|
| Novice | 👁 | 0 |
| Observateur | 🔍 | 100 |
| Analyste | 🧠 | 300 |
| … | … | … |
| Oracle | ☯ | 10 000 |
| Mentaliste Absolu | 🌌 | 25 000 |

### ⚡ Feedback Sensoriel
- **Haptic** : vibration de précision (API 29+ `EFFECT_CLICK`, API 26+ `createOneShot 40ms`).
- **Son** : ping cristallin synthétisé (onde sinusoïdale 880 Hz, décroissance exponentielle, `AudioTrack PCM_16BIT`).
- **Flash Néon Bleu** : overlay translucide `#00C8FF` de 500 ms sur la vidéo à chaque détection.

### 🛡 Jauge de Crédibilité
- Score persistant de 0 à 100 pts, régénération automatique (+1 pt/min).
- Pénalité si trop de clics inutiles (`uselessClicks × 8 pts`).
- **Lockout 30 minutes** si la crédibilité tombe à 0.
- Déblocage immédiat via pub récompensée AdMob (ticket crédibilité).

---

## Build

### Prérequis
- Android Studio Ladybug (2024.2) ou plus récent
- JDK 17
- Android SDK 35 / Build Tools 34+
- Un fichier `local.properties` avec `sdk.dir=...`

### Compiler

```bash
# Debug APK
.\gradlew.bat assembleDebug

# L'APK se trouve dans :
# app/build/outputs/apk/debug/app-debug.apk
```

### Installer via ADB

```bash
adb push app/build/outputs/apk/debug/app-debug.apk /data/local/tmp/
adb shell pm install -r -t /data/local/tmp/app-debug.apk
```

---

## Sources vidéo

Toutes les vidéos proviennent de [Pexels](https://www.pexels.com) sous licence libre d'utilisation.  
Aucune donnée biométrique réelle n'est collectée ni analysée.

---

## Roadmap v2

- [ ] **Supabase** — Authentification, scores en ligne, classement journalier
- [ ] **Push Notifications** — Rappel quotidien "Ton Cas du Jour t'attend"
- [ ] **Mode Duel** — Comparer son score avec un ami via lien partagé
- [ ] **Pack Leçons Pro** — Contenu en achat intégré (IAP)
- [ ] **Analyse IA** — Feedback automatique post-partie via un modèle ML embarqué

---

## Licence

MIT © 2025 — The Verdict Project
