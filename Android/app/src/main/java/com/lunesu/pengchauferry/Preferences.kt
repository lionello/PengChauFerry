package com.lunesu.pengchauferry

import android.content.Context

class Preferences(context: Context) {
    companion object {
        private const val LANGUAGE_PREF = "language"
        private const val SHAREDPREF_NAME = "com.lunesu.pengchauferry_preferences"
    }

    private val shared = context.getSharedPreferences(SHAREDPREF_NAME, Context.MODE_PRIVATE)

    var language: String?
        get() = shared.getString(LANGUAGE_PREF, null)
        set(value) = shared.edit().putString(LANGUAGE_PREF, value).apply()
}
