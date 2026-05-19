package com.university.studentauthapp

import android.content.Intent
import android.os.Bundle
import android.util.Patterns
import android.view.View
import android.widget.ProgressBar
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.button.MaterialButton
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.UserProfileChangeRequest

class RegistrationActivity : AppCompatActivity() {
    private lateinit var auth: FirebaseAuth
    private lateinit var rootView: View
    private lateinit var edtFullName: TextInputEditText
    private lateinit var edtEmail: TextInputEditText
    private lateinit var edtPassword: TextInputEditText
    private lateinit var edtConfirmPassword: TextInputEditText
    private lateinit var btnRegister: MaterialButton
    private lateinit var txtGoLogin: TextView
    private lateinit var progressBar: ProgressBar

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_registration)

        auth = FirebaseAuth.getInstance()
        rootView = findViewById(R.id.rootRegister)
        edtFullName = findViewById(R.id.edtFullName)
        edtEmail = findViewById(R.id.edtRegisterEmail)
        edtPassword = findViewById(R.id.edtRegisterPassword)
        edtConfirmPassword = findViewById(R.id.edtConfirmPassword)
        btnRegister = findViewById(R.id.btnRegister)
        txtGoLogin = findViewById(R.id.txtGoLogin)
        progressBar = findViewById(R.id.progressRegister)

        btnRegister.setOnClickListener { registerUser() }
        txtGoLogin.setOnClickListener { finish() }
    }

    private fun registerUser() {
        val fullName = edtFullName.text.toString().trim()
        val email = edtEmail.text.toString().trim()
        val password = edtPassword.text.toString().trim()
        val confirmPassword = edtConfirmPassword.text.toString().trim()

        if (fullName.isEmpty()) { edtFullName.error = "Full name required"; edtFullName.requestFocus(); return }
        if (email.isEmpty()) { edtEmail.error = "Email required"; edtEmail.requestFocus(); return }
        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) { edtEmail.error = "Valid email required"; edtEmail.requestFocus(); return }
        if (password.isEmpty()) { edtPassword.error = "Password required"; edtPassword.requestFocus(); return }
        if (password.length < 8) { edtPassword.error = "Password >=8 chars"; edtPassword.requestFocus(); return }
        if (confirmPassword.isEmpty()) { edtConfirmPassword.error = "Confirm required"; edtConfirmPassword.requestFocus(); return }
        if (password != confirmPassword) { edtConfirmPassword.error = "Passwords do not match"; edtConfirmPassword.requestFocus(); return }

        setLoading(true)
        auth.createUserWithEmailAndPassword(email, password).addOnCompleteListener { task ->
            if (task.isSuccessful) {
                auth.currentUser?.updateProfile(UserProfileChangeRequest.Builder().setDisplayName(fullName).build())
                    ?.addOnCompleteListener {
                        setLoading(false)
                        startActivity(Intent(this, HomeActivity::class.java).apply {
                            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                        })
                    }
            } else {
                setLoading(false)
                Snackbar.make(rootView, task.exception?.message ?: "Registration failed", Snackbar.LENGTH_LONG).show()
            }
        }
    }

    private fun setLoading(isLoading: Boolean) {
        progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
        btnRegister.isEnabled = !isLoading
    }
}