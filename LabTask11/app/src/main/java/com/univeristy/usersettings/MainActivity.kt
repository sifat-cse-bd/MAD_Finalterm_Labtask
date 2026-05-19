package com.university.usersettings

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.*
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SwitchCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.android.material.button.MaterialButton
import com.google.android.material.floatingactionbutton.FloatingActionButton

class MainActivity : AppCompatActivity() {

    private lateinit var etStudentName: EditText
    private lateinit var radioTheme: RadioGroup
    private lateinit var rbLight: RadioButton
    private lateinit var rbDark: RadioButton
    private lateinit var rbSystem: RadioButton
    private lateinit var switchNotif: SwitchCompat
    private lateinit var spinnerLang: Spinner
    private lateinit var seekBarFont: SeekBar
    private lateinit var tvFontSize: TextView
    private lateinit var btnSave: MaterialButton
    private lateinit var btnReset: MaterialButton
    private lateinit var btnView: MaterialButton
    private lateinit var fabProfile: FloatingActionButton

    override fun onCreate(savedInstanceState: Bundle?) {
        ThemeUtils.applyThemeFromPrefs(this)
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        initViews()
        setSupportActionBar(findViewById(R.id.toolbar))
        supportActionBar?.title = ""
        setupListeners()
        setupSpinner()
    }

    private fun initViews() {
        etStudentName = findViewById(R.id.etStudentName)
        radioTheme = findViewById(R.id.radioTheme)
        rbLight = findViewById(R.id.rbLight)
        rbDark = findViewById(R.id.rbDark)
        rbSystem = findViewById(R.id.rbSystem)
        switchNotif = findViewById(R.id.switchNotif)
        spinnerLang = findViewById(R.id.spinnerLang)
        seekBarFont = findViewById(R.id.seekBarFont)
        tvFontSize = findViewById(R.id.tvFontSize)
        btnSave = findViewById(R.id.btnSave)
        btnReset = findViewById(R.id.btnReset)
        btnView = findViewById(R.id.btnView)
        fabProfile = findViewById(R.id.fabProfile)
    }

    private fun setupListeners() {
        seekBarFont.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                val size = progress + 12
                tvFontSize.text = "Font Size: ${size}sp"
            }
            override fun onStartTrackingTouch(seekBar: SeekBar?) {}
            override fun onStopTrackingTouch(seekBar: SeekBar?) {}
        })

        btnSave.setOnClickListener { saveSettings() }
        btnReset.setOnClickListener { resetPreferences() }
        btnView.setOnClickListener {
            startActivity(Intent(this, SettingsViewerActivity::class.java))
        }
        fabProfile.setOnClickListener {
            startActivity(Intent(this, ProfileActivity::class.java))
        }
    }

    private fun setupSpinner() {
        val languages = arrayOf("English", "Bangla", "Arabic", "French")
        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, languages)
        spinnerLang.adapter = adapter
    }

    override fun onResume() {
        super.onResume()
        loadSettings()
    }

    private fun loadSettings() {
        val appPrefs = getSharedPreferences(PrefFiles.APP_SETTINGS, Context.MODE_PRIVATE)
        val profilePrefs = getSharedPreferences(PrefFiles.PROFILE_PREFS, Context.MODE_PRIVATE)

        etStudentName.setText(profilePrefs.getString(PrefKeys.KEY_STUDENT_NAME, ""))

        when (appPrefs.getString(PrefKeys.KEY_THEME, "light")) {
            "dark" -> rbDark.isChecked = true
            "system" -> rbSystem.isChecked = true
            else -> rbLight.isChecked = true
        }

        switchNotif.isChecked = appPrefs.getBoolean(PrefKeys.KEY_NOTIFICATIONS, true)

        val lang = appPrefs.getString(PrefKeys.KEY_LANGUAGE, "English")
        val languages = arrayOf("English", "Bangla", "Arabic", "French")
        val langIndex = languages.indexOf(lang)
        if (langIndex >= 0) spinnerLang.setSelection(langIndex)

        val fontSize = appPrefs.getInt(PrefKeys.KEY_FONT_SIZE, 16)
        seekBarFont.progress = fontSize - 12
        tvFontSize.text = "Font Size: ${fontSize}sp"
    }

    private fun saveSettings() {
        val appPrefs = getSharedPreferences(PrefFiles.APP_SETTINGS, Context.MODE_PRIVATE)
        val profilePrefs = getSharedPreferences(PrefFiles.PROFILE_PREFS, Context.MODE_PRIVATE)

        val selectedTheme = when (radioTheme.checkedRadioButtonId) {
            R.id.rbDark -> "dark"
            R.id.rbSystem -> "system"
            else -> "light"
        }

        with(appPrefs.edit()) {
            putString(PrefKeys.KEY_THEME, selectedTheme)
            putBoolean(PrefKeys.KEY_NOTIFICATIONS, switchNotif.isChecked)
            putString(PrefKeys.KEY_LANGUAGE, spinnerLang.selectedItem.toString())
            putInt(PrefKeys.KEY_FONT_SIZE, seekBarFont.progress + 12)
            putLong(PrefKeys.KEY_LAST_SAVED, System.currentTimeMillis())
            apply()
        }

        with(profilePrefs.edit()) {
            putString(PrefKeys.KEY_STUDENT_NAME, etStudentName.text.toString())
            apply()
        }

        ThemeUtils.applyTheme(selectedTheme)
        Toast.makeText(this, "Settings saved successfully", Toast.LENGTH_SHORT).show()
    }

    private fun resetPreferences() {
        val appPrefs = getSharedPreferences(PrefFiles.APP_SETTINGS, Context.MODE_PRIVATE)
        val profilePrefs = getSharedPreferences(PrefFiles.PROFILE_PREFS, Context.MODE_PRIVATE)

        appPrefs.edit().clear().apply()
        profilePrefs.edit().clear().apply()

        etStudentName.setText("")
        rbLight.isChecked = true
        switchNotif.isChecked = true
        spinnerLang.setSelection(0)
        seekBarFont.progress = 4
        tvFontSize.text = "Font Size: 16sp"

        ThemeUtils.applyTheme("light")
        Toast.makeText(this, "Settings reset to default", Toast.LENGTH_SHORT).show()
    }
}
