package com.theverdict.app.data.crash

import android.content.Context
import android.os.Build
import java.io.File
import java.io.PrintWriter
import java.io.StringWriter
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

/**
 * Gestionnaire de crash léger — enregistre les exceptions non-interceptées
 * dans un fichier local crash_log.txt pour diagnostic.
 * Alternative légère à Firebase Crashlytics, sans dépendance externe.
 */
class CrashReporter(private val context: Context) {

    private val defaultHandler = Thread.getDefaultUncaughtExceptionHandler()

    fun install() {
        Thread.setDefaultUncaughtExceptionHandler { thread, throwable ->
            try {
                saveCrashToFile(thread, throwable)
            } catch (_: Exception) {
                // Don't crash while reporting a crash
            }
            // Forward to default handler (will kill the app)
            defaultHandler?.uncaughtException(thread, throwable)
        }
    }

    private fun saveCrashToFile(thread: Thread, throwable: Throwable) {
        val sw = StringWriter()
        throwable.printStackTrace(PrintWriter(sw))

        val timestamp = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.FRANCE).format(Date())
        val report = buildString {
            appendLine("═══ CRASH REPORT ═══")
            appendLine("Date: $timestamp")
            appendLine("Thread: ${thread.name}")
            appendLine("Device: ${Build.MANUFACTURER} ${Build.MODEL}")
            appendLine("Android: ${Build.VERSION.RELEASE} (SDK ${Build.VERSION.SDK_INT})")
            appendLine("App version: ${getVersionName()}")
            appendLine("───────────────────")
            appendLine(sw.toString())
            appendLine()
        }

        val file = File(context.filesDir, "crash_log.txt")
        // Keep max 50KB of crash logs
        if (file.exists() && file.length() > 50_000) {
            val trimmed = file.readText().takeLast(25_000)
            file.writeText(trimmed)
        }
        file.appendText(report)
    }

    private fun getVersionName(): String {
        return try {
            context.packageManager.getPackageInfo(context.packageName, 0).versionName ?: "?"
        } catch (_: Exception) {
            "?"
        }
    }

    companion object {
        /**
         * Retrieve crash log contents (useful for future in-app reporting).
         */
        fun getCrashLog(context: Context): String? {
            val file = File(context.filesDir, "crash_log.txt")
            return if (file.exists()) file.readText() else null
        }
    }
}
