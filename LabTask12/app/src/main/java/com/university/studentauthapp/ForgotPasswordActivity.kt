package com.university.studentauthapp

import android.os.Bundle
import android.util.Patterns
import android.view.View
import android.widget.ImageButton
import android.widget.ProgressBar
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.button.MaterialButton
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.auth.FirebaseAuth

class ForgotPasswordActivity : AppCompatActivity() {
    private lateinit var auth: FirebaseAuth
    private lateinit var rootView: View
    private lateinit var edtEmail: TextInputEditText
    private lateinit var btnSendReset: MaterialButton
    private lateinit var progressBar: ProgressBar
    private lateinit var btnBack: ImageButton

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_forgot_password)

        auth = FirebaseAuth.getInstance()
        rootView = findViewById(R.id.rootForgot)
        edtEmail = findViewById(R.id.edtForgotEmail)
        btnSendReset = findViewById(R.id.btnSendReset)
        progressBar = findViewById(R.id.progressForgot)
        btnBack = findViewById(R.id.btnBack)

        btnBack.setOnClickListener { finish() }
        btnSendReset.setOnClickListener { sendResetEmail() }
    }

    private fun sendResetEmail() {
        val email = edtEmail.text.toString().trim()
        if (email.isEmpty()) { edtEmail.error = "Email required"; edtEmail.requestFocus(); return }
        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) { edtEmail.error = "Valid email"; edtEmail.requestFocus(); return }

        setLoading(true)
        auth.sendPasswordResetEmail(email).addOnCompleteListener { task ->
            setLoading(false)
            if (task.isSuccessful) {
                Toast.makeText(this, "Reset email sent. Check inbox.", Toast.LENGTH_LONG).show()
                finish()
            } else {
                Snackbar.make(rootView, task.exception?.message ?: "Reset failed", Snackbar.LENGTH_LONG).show()
            }
        }
    }

    private fun setLoading(isLoading: Boolean) {
        progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
        btnSendReset.isEnabled = !isLoading
    }
}