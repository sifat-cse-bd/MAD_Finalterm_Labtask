package com.university.usersettings

import android.content.Context
import android.os.Bundle
import android.util.Patterns
import android.widget.ArrayAdapter
import android.widget.Spinner
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.appbar.MaterialToolbar
import com.google.android.material.button.MaterialButton
import com.google.android.material.textfield.TextInputEditText

class ProfileActivity : AppCompatActivity() {

    private lateinit var tvWelcome: TextView
    private lateinit var etStudentId: TextInputEditText
    private lateinit var etFullName: TextInputEditText
    private lateinit var spinnerDepartment: Spinner
    private lateinit var spinnerYear: Spinner
    private lateinit var etEmail: TextInputEditText
    private lateinit var btnSaveProfile: MaterialButton

    private val departments = listOf("CSE", "EEE", "BBA", "English", "Law")
    private val years = listOf("1st Year", "2nd Year", "3rd Year", "4th Year")

    override fun onCreate(savedInstanceState: Bundle?) {
        ThemeUtils.applyThemeFromPrefs(this)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile)

        val toolbar = findViewById<MaterialToolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)
        supportActionBar?.title = ""
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        bindViews()
        setupSpinners()
        restoreProfile()

        btnSaveProfile.setOnClickListener { saveProfile() }
    }

    override fun onSupportNavigateUp(): Boolean {
        finish()
        return true
    }

    private fun bindViews() {
        tvWelcome = findViewById(R.id.tvWelcome)
        etStudentId = findViewById(R.id.etStudentId)
        etFullName = findViewById(R.id.etFullName)
        spinnerDepartment = findViewById(R.id.spinnerDepartment)
        spinnerYear = findViewById(R.id.spinnerYear)
        etEmail = findViewById(R.id.etEmail)
        btnSaveProfile = findViewById(R.id.btnSaveProfile)
    }

    private fun setupSpinners() {
        val departmentAdapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, departments)
        departmentAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinnerDepartment.adapter = departmentAdapter

        val yearAdapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, years)
        yearAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinnerYear.adapter = yearAdapter
    }

    private fun restoreProfile() {
        val profilePrefs = getSharedPreferences(PrefFiles.PROFILE_PREFS, Context.MODE_PRIVATE)

        val name = profilePrefs.getString(PrefKeys.KEY_STUDENT_NAME, "").orEmpty()
        val studentId = profilePrefs.getString(PrefKeys.KEY_STUDENT_ID, "").orEmpty()
        val department = profilePrefs.getString(PrefKeys.KEY_DEPARTMENT, departments.first()).orEmpty()
        val year = profilePrefs.getString(PrefKeys.KEY_YEAR, years.first()).orEmpty()
        val email = profilePrefs.getString(PrefKeys.KEY_EMAIL, "").orEmpty()

        tvWelcome.text = if (name.isNotBlank()) "Welcome back, $name!" else "Welcome to Profile Setup!"
        etFullName.setText(name)
        etStudentId.setText(studentId)
        etEmail.setText(email)

        spinnerDepartment.setSelection(departments.indexOf(department).takeIf { it >= 0 } ?: 0)
        spinnerYear.setSelection(years.indexOf(year).takeIf { it >= 0 } ?: 0)
    }

    private fun saveProfile() {
        val studentId = etStudentId.text?.toString()?.trim().orEmpty()
        val fullName = etFullName.text?.toString()?.trim().orEmpty()
        val email = etEmail.text?.toString()?.trim().orEmpty()
        val department = spinnerDepartment.selectedItem.toString()
        val year = spinnerYear.selectedItem.toString()

        if (studentId.isBlank()) {
            etStudentId.error = "Student ID is required"
            return
        }

        if (fullName.isBlank()) {
            etFullName.error = "Full name is required"
            return
        }

        if (email.isBlank() || !Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            etEmail.error = "Enter a valid email address"
            return
        }

        val profilePrefs = getSharedPreferences(PrefFiles.PROFILE_PREFS, Context.MODE_PRIVATE)
        with(profilePrefs.edit()) {
            putString(PrefKeys.KEY_STUDENT_ID, studentId)
            putString(PrefKeys.KEY_STUDENT_NAME, fullName)
            putString(PrefKeys.KEY_DEPARTMENT, department)
            putString(PrefKeys.KEY_YEAR, year)
            putString(PrefKeys.KEY_EMAIL, email)
            apply()
        }

        tvWelcome.text = "Welcome back, $fullName!"
        Toast.makeText(this, "Profile saved successfully", Toast.LENGTH_SHORT).show()
    }
}
