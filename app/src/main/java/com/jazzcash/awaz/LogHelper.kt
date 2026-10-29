package com.jazzcash.awaz

import android.content.Context
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

// Last 20 announcements + the on/off switch, kept in SharedPreferences.
object LogHelper {
    private const val PREFS = "jazzcash_awaz"
    private const val KEY_LOG = "log"
    private const val KEY_ENABLED = "enabled"

    fun add(context: Context, text: String) {
        val prefs = context.getSharedPreferences(PREFS, Context.MODE_PRIVATE)
        val time = SimpleDateFormat("dd MMM, hh:mm a", Locale.ENGLISH).format(Date())
        val old = prefs.getString(KEY_LOG, "") ?: ""
        val lines = ("$time | $text" + if (old.isEmpty()) "" else "\n$old")
            .lines().take(20).joinToString("\n")
        prefs.edit().putString(KEY_LOG, lines).apply()
    }

    fun read(context: Context): List<String> {
        val raw = context.getSharedPreferences(PREFS, Context.MODE_PRIVATE).getString(KEY_LOG, "") ?: ""
        return raw.lines().filter { it.isNotBlank() }
    }

    fun isEnabled(context: Context): Boolean {
        return context.getSharedPreferences(PREFS, Context.MODE_PRIVATE).getBoolean(KEY_ENABLED, true)
    }

    fun setEnabled(context: Context, on: Boolean) {
        context.getSharedPreferences(PREFS, Context.MODE_PRIVATE).edit().putBoolean(KEY_ENABLED, on).apply()
    }
}
