package com.lunesu.pengchauferry

import android.content.Context
import org.joda.time.LocalDateTime

class Preferences(context: Context) {
    companion object {
        private const val SHAREDPREF_NAME = "com.lunesu.pengchauferry_preferences"
        private const val LANGUAGE_PREF = "language"
        private const val LAST_REFRESH_PREF = "lastRefresh"
    }

    // Note: shared will be null during unit testing
    private val shared = context.getSharedPreferences(SHAREDPREF_NAME, Context.MODE_PRIVATE)

    var language: String?
        get() = shared.getString(LANGUAGE_PREF, null)
        set(value) = shared.edit().putString(LANGUAGE_PREF, value).apply()

    var lastRefresh: LocalDateTime?
        get() = shared.getString(LAST_REFRESH_PREF, null)?.let { LocalDateTime.parse(it) }
        set(value) { shared?.run { edit().putString(LAST_REFRESH_PREF, value?.toString()).apply() } }

}
