package com.university.studentauthapp

import android.content.Intent
import android.os.Bundle
import android.text.format.DateFormat
import android.view.View
import android.widget.ProgressBar
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.button.MaterialButton
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.auth.FirebaseAuth
import java.util.Date

class HomeActivity : AppCompatActivity() {
    private lateinit var auth: FirebaseAuth
    private lateinit var rootView: View
    private lateinit var txtAvatar: TextView
    private lateinit var txtUserEmail: TextView
    private lateinit var txtUserUid: TextView
    private lateinit var txtCreationDate: TextView
    private lateinit var edtNewPassword: TextInputEditText
    private lateinit var edtConfirmPassword: TextInputEditText
    private lateinit var btnUpdatePassword: MaterialButton
    private lateinit var btnLogout: MaterialButton
    private lateinit var btnDeleteAccount: MaterialButton
    private lateinit var progressBar: ProgressBar

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)

        auth = FirebaseAuth.getInstance()
        rootView = findViewById(R.id.rootHome)
        txtAvatar = findViewById(R.id.txtAvatar)
        txtUserEmail = findViewById(R.id.txtUserEmail)
        txtUserUid = findViewById(R.id.txtUserUid)
        txtCreationDate = findViewById(R.id.txtCreationDate)
        edtNewPassword = findViewById(R.id.edtNewPassword)
        edtConfirmPassword = findViewById(R.id.edtNewConfirmPassword)
        btnUpdatePassword = findViewById(R.id.btnUpdatePassword)
        btnLogout = findViewById(R.id.btnLogout)
        btnDeleteAccount = findViewById(R.id.btnDeleteAccount)
        progressBar = findViewById(R.id.progressHome)

        showUserInfo()

        btnLogout.setOnClickListener { logoutUser() }
        btnUpdatePassword.setOnClickListener { updatePassword() }
        btnDeleteAccount.setOnClickListener { showDeleteConfirmation() }
    }

    private fun showUserInfo() {
        val user = auth.currentUser ?: run { openLoginAndClearBackStack(); return }
        val email = user.email ?: "No email"
        txtAvatar.text = email.firstOrNull()?.uppercaseChar()?.toString() ?: "S"
        txtUserEmail.text = "Email: $email"
        txtUserUid.text = "UID: ${user.uid.take(8)}"
        val creation = user.metadata?.creationTimestamp ?: 0L
        txtCreationDate.text = "Created: ${DateFormat.format("dd MMM yyyy", Date(creation))}"
    }

    private fun updatePassword() {
        val newPass = edtNewPassword.text.toString().trim()
        val confirm = edtConfirmPassword.text.toString().trim()
        if (newPass.isEmpty()) { edtNewPassword.error="Required"; edtNewPassword.requestFocus(); return }
        if (newPass.length<8){ edtNewPassword.error=">=8 chars"; edtNewPassword.requestFocus(); return }
        if (confirm.isEmpty()){ edtConfirmPassword.error="Confirm required"; edtConfirmPassword.requestFocus(); return }
        if (newPass != confirm){ edtConfirmPassword.error="Not match"; edtConfirmPassword.requestFocus(); return }

        val user = auth.currentUser ?: run { openLoginAndClearBackStack(); return }

        setLoading(true)
        user.updatePassword(newPass).addOnCompleteListener{
            setLoading(false)
            if(it.isSuccessful){
                edtNewPassword.text?.clear(); edtConfirmPassword.text?.clear()
                Snackbar.make(rootView,"Password updated",Snackbar.LENGTH_LONG).show()
            } else {
                Snackbar.make(rootView,it.exception?.message ?: "Update failed",Snackbar.LENGTH_LONG).show()
            }
        }
    }

    private fun showDeleteConfirmation(){
        AlertDialog.Builder(this)
            .setTitle("Delete Account")
            .setMessage("Are you sure to permanently delete this account?")
            .setPositiveButton("Delete"){_,_-> deleteAccount() }
            .setNegativeButton("Cancel", null)
            .show()
    }

    private fun deleteAccount(){
        val user = auth.currentUser ?: run { openLoginAndClearBackStack(); return }
        setLoading(true)
        user.delete().addOnCompleteListener{
            setLoading(false)
            if(it.isSuccessful){
                Snackbar.make(rootView,"Account deleted",Snackbar.LENGTH_LONG).show()
                openLoginAndClearBackStack()
            } else {
                Snackbar.make(rootView,it.exception?.message ?: "Delete failed",Snackbar.LENGTH_LONG).show()
            }
        }
    }

    private fun logoutUser(){
        auth.signOut()
        openLoginAndClearBackStack()
    }

    private fun openLoginAndClearBackStack(){
        startActivity(Intent(this,LoginActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        })
        finish()
    }

    private fun setLoading(isLoading:Boolean){
        progressBar.visibility = if(isLoading) View.VISIBLE else View.GONE
        btnUpdatePassword.isEnabled = !isLoading
        btnLogout.isEnabled = !isLoading
        btnDeleteAccount.isEnabled = !isLoading
    }
}