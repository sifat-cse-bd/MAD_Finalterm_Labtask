package com.university.usersettings

import android.content.Context
import androidx.appcompat.app.AppCompatDelegate

object ThemeUtils {
    fun applyThemeFromPrefs(context: Context) {
        val theme = context.getSharedPreferences(PrefFiles.APP_SETTINGS, Context.MODE_PRIVATE)
            .getString(PrefKeys.KEY_THEME, "light")
        applyTheme(theme)
    }

    fun applyTheme(theme: String?) {
        val mode = when (theme) {
            "dark" -> AppCompatDelegate.MODE_NIGHT_YES
            "system" -> AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM
            else -> AppCompatDelegate.MODE_NIGHT_NO
        }
        AppCompatDelegate.setDefaultNightMode(mode)
    }
}
