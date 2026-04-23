package com.example.myfirstapp

import android.app.AlertDialog
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.text.InputType
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.firestore.FirebaseFirestore
import org.mindrot.jbcrypt.BCrypt

class Login : AppCompatActivity() {

    private val db = FirebaseFirestore.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.fragment_loginpage)

        val nextButton = findViewById<Button>(R.id.btnLoginNext)
        val createAccountText = findViewById<TextView>(R.id.tvCreateAccount)
        val loginUiContent = findViewById<View>(R.id.login_ui_content)
        val etEmail = findViewById<EditText>(R.id.etLoginEmail)
        val etPassword = findViewById<EditText>(R.id.etLoginPassword)
        val btnFacebook = findViewById<ImageButton>(R.id.btnFacebook)
        val btnGoogle = findViewById<ImageButton>(R.id.btnGoogle)

        // Social Logins - Implicit Intents
        btnFacebook.setOnClickListener {
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse("https://www.facebook.com/login"))
            startActivity(intent)
        }

        btnGoogle.setOnClickListener {
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse("https://accounts.google.com/"))
            startActivity(intent)
        }

        nextButton.setOnClickListener {
            val emailEntered = etEmail.text.toString().trim()
            val passwordEntered = etPassword.text.toString().trim()

            if (emailEntered.isEmpty() || passwordEntered.isEmpty()) {
                Toast.makeText(this, "Please enter email and password", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // UPDATED: Search by the email field, not the Document ID
            db.collection("users")
                .whereEqualTo("email", emailEntered)
                .get()
                .addOnSuccessListener { documents ->
                    if (!documents.isEmpty) {
                        // Get the first document that matches the email
                        val document = documents.documents[0]

                        val dbPasswordHash = document.getString("password") ?: ""
                        val isFirstLogin = document.getBoolean("isFirstLogin") ?: false
                        val role = document.getString("role") ?: "user"

                        if (BCrypt.checkpw(passwordEntered, dbPasswordHash)) {
                            if (isFirstLogin) {
                                showNewPasswordDialog(emailEntered)
                            } else {
                                navigateToRolePage(role)
                            }
                        } else {
                            Toast.makeText(this, "Incorrect password", Toast.LENGTH_SHORT).show()
                        }
                    } else {
                        // If the list of documents is empty
                        Toast.makeText(this, "No account found with this email", Toast.LENGTH_SHORT).show()
                    }
                }
                .addOnFailureListener { e ->
                    Toast.makeText(this, "Error: ${e.message}", Toast.LENGTH_SHORT).show()
                }
        }

        createAccountText.setOnClickListener {
            loginUiContent.visibility = View.GONE
            supportFragmentManager.beginTransaction()
                .replace(R.id.fragment_container_login, Signin())
                .addToBackStack(null)
                .commit()
        }

        supportFragmentManager.addOnBackStackChangedListener {
            if (supportFragmentManager.backStackEntryCount == 0) {
                loginUiContent.visibility = View.VISIBLE
            }
        }
    }

    private fun showNewPasswordDialog(email: String) {
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Update Password")
        builder.setMessage("This is your first login. Please set a new password.")

        val input = EditText(this)
        input.hint = "New Password"
        input.inputType = InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_PASSWORD
        builder.setView(input)

        builder.setPositiveButton("Update") { _, _ ->
            val newPassword = input.text.toString().trim()
            if (newPassword.length < 6) {
                Toast.makeText(this, "Password too short!", Toast.LENGTH_SHORT).show()
            } else {
                val newHash = BCrypt.hashpw(newPassword, BCrypt.gensalt())
                db.collection("users").document(email)
                    .update(mapOf("password" to newHash, "isFirstLogin" to false))
                    .addOnSuccessListener {
                        Toast.makeText(this, "Password updated! Please login again.", Toast.LENGTH_LONG).show()
                        findViewById<EditText>(R.id.etLoginPassword).text.clear()
                    }
            }
        }
        builder.setCancelable(false)
        builder.show()
    }

    private fun navigateToRolePage(role: String) {
        val loginUiContent = findViewById<View>(R.id.login_ui_content)
        loginUiContent.visibility = View.GONE

        val fragment = when (role) {
            "admin" -> AdminFragment()
            "staff" -> StaffDashboard()
            else -> {
                // Regular user goes to MainActivity
                startActivity(Intent(this, MainActivity::class.java))
                finish()
                return
            }
        }

        supportFragmentManager.beginTransaction()
            .replace(R.id.fragment_container_login, fragment)
            .commit()
    }
}