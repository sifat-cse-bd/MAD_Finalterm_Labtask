package com.university.usersettings

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.appbar.MaterialToolbar
import com.google.android.material.button.MaterialButton
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class SettingsViewerActivity : AppCompatActivity() {

    private lateinit var containerPrefs: LinearLayout
    private lateinit var tvEmptyState: TextView
    private lateinit var btnEdit: MaterialButton

    override fun onCreate(savedInstanceState: Bundle?) {
        ThemeUtils.applyThemeFromPrefs(this)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings_viewer)

        val toolbar = findViewById<MaterialToolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)
        supportActionBar?.title = ""
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        containerPrefs = findViewById(R.id.containerPrefs)
        tvEmptyState = findViewById(R.id.tvEmptyState)
        btnEdit = findViewById(R.id.btnEdit)

        btnEdit.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP
            startActivity(intent)
            finish()
        }

        showSavedPreferences()
    }

    override fun onSupportNavigateUp(): Boolean {
        finish()
        return true
    }

    private fun showSavedPreferences() {
        val appSettings = getSharedPreferences(PrefFiles.APP_SETTINGS, Context.MODE_PRIVATE)
        val profilePrefs = getSharedPreferences(PrefFiles.PROFILE_PREFS, Context.MODE_PRIVATE)

        val hasAppSettings = appSettings.contains(PrefKeys.KEY_LAST_SAVED)
        val hasProfile = profilePrefs.all.isNotEmpty()

        containerPrefs.removeAllViews()

        if (!hasAppSettings && !hasProfile) {
            tvEmptyState.visibility = View.VISIBLE
            btnEdit.visibility = View.VISIBLE
            return
        }

        tvEmptyState.visibility = View.GONE
        btnEdit.visibility = View.VISIBLE

        if (hasProfile) {
            addSectionTitle("Student Profile")
            addPreferenceRow("Student Name", profilePrefs.getString(PrefKeys.KEY_STUDENT_NAME, "Not set") ?: "Not set")
            addPreferenceRow("Student ID", profilePrefs.getString(PrefKeys.KEY_STUDENT_ID, "Not set") ?: "Not set")
            addPreferenceRow("Department", profilePrefs.getString(PrefKeys.KEY_DEPARTMENT, "Not set") ?: "Not set")
            addPreferenceRow("Year of Study", profilePrefs.getString(PrefKeys.KEY_YEAR, "Not set") ?: "Not set")
            addPreferenceRow("Email", profilePrefs.getString(PrefKeys.KEY_EMAIL, "Not set") ?: "Not set")
        }

        if (hasAppSettings) {
            addSectionTitle("App Settings")
            addPreferenceRow("Theme", readableTheme(appSettings.getString(PrefKeys.KEY_THEME, "light")))
            addPreferenceRow("Notifications", if (appSettings.getBoolean(PrefKeys.KEY_NOTIFICATIONS, true)) "Enabled" else "Disabled")
            addPreferenceRow("Language", appSettings.getString(PrefKeys.KEY_LANGUAGE, "English") ?: "English")
            addPreferenceRow("Font Size", "${appSettings.getInt(PrefKeys.KEY_FONT_SIZE, 16)}sp")

            val lastSaved = appSettings.getLong(PrefKeys.KEY_LAST_SAVED, 0L)
            addPreferenceRow("Last Saved", formatDate(lastSaved))
        }
    }

    private fun addSectionTitle(title: String) {
        val view = LayoutInflater.from(this).inflate(R.layout.item_section_title, containerPrefs, false)
        view.findViewById<TextView>(R.id.tvSectionTitle).text = title
        containerPrefs.addView(view)
    }

    private fun addPreferenceRow(label: String, value: String) {
        val view = LayoutInflater.from(this).inflate(R.layout.item_preference_row, containerPrefs, false)
        view.findViewById<TextView>(R.id.tvLabel).text = label
        view.findViewById<TextView>(R.id.tvValue).text = value.ifBlank { "Not set" }
        containerPrefs.addView(view)
    }

    private fun readableTheme(theme: String?): String {
        return when (theme) {
            "dark" -> "Dark"
            "system" -> "System Default"
            else -> "Light"
        }
    }

    private fun formatDate(timeMillis: Long): String {
        if (timeMillis <= 0L) return "Not saved yet"
        return SimpleDateFormat("dd MMM yyyy, hh:mm a", Locale.getDefault()).format(Date(timeMillis))
    }
}
