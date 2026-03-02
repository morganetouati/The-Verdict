# ProGuard rules for The Verdict

# Keep AdMob classes
-keep class com.google.android.gms.ads.** { *; }
-keep class com.google.android.gms.internal.ads.** { *; }

# Keep Google Fonts provider
-keep class com.google.android.gms.fonts.** { *; }
-keep class androidx.compose.ui.text.google.** { *; }

# Keep Compose
-dontwarn androidx.compose.**
-keep class androidx.compose.** { *; }

# Keep ExoPlayer / Media3
-dontwarn androidx.media3.**
-keep class androidx.media3.** { *; }

# Keep Navigation Compose
-keep class androidx.navigation.** { *; }

# Keep DataStore
-keepclassmembers class * extends com.google.protobuf.GeneratedMessageLite { *; }

# Keep Kotlin Coroutines
-dontwarn kotlinx.coroutines.**
-keep class kotlinx.coroutines.** { *; }

# Keep model classes (needed for any future serialization)
-keep class com.theverdict.app.domain.model.** { *; }
