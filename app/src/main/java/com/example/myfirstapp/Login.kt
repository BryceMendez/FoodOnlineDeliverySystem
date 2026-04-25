package com.example.myfirstapp

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ScrollView
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

        // Find Views
        val etEmail = findViewById<EditText>(R.id.etLoginEmail)
        val etPassword = findViewById<EditText>(R.id.etLoginPassword)
        val btnLogin = findViewById<Button>(R.id.btnLoginNext)
        val tvCreateAccount = findViewById<TextView>(R.id.tvCreateAccount)
        val loginUiContent = findViewById<ScrollView>(R.id.login_ui_content)

        // CREATE ACCOUNT LINK LOGIC
        tvCreateAccount.setOnClickListener {
            // 1. Hide the Login UI ScrollView
            loginUiContent.visibility = View.GONE

            // 2. Load the Signin Fragment into the container
            supportFragmentManager.beginTransaction()
                .replace(R.id.fragment_container_login, Signin())
                .addToBackStack(null) // Allows user to press back to return to Login
                .commit()
        }

        // LOGIN BUTTON LOGIC
        btnLogin.setOnClickListener {
            val email = etEmail.text.toString().trim()
            val password = etPassword.text.toString().trim()

            if (email.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "Please enter credentials", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            db.collection("users").document(email).get()
                .addOnSuccessListener { document ->
                    if (document.exists()) {
                        val hashedPassword = document.getString("password") ?: ""
                        val role = document.getString("role") ?: "Customer"
                        val firstName = document.getString("firstName") ?: ""
                        val lastName = document.getString("lastName") ?: ""

                        if (BCrypt.checkpw(password, hashedPassword)) {
                            val sharedPref = getSharedPreferences("UserSession", Context.MODE_PRIVATE)
                            with(sharedPref.edit()) {
                                putString("email", email)
                                putString("full_name", "$firstName $lastName")
                                putString("user_role", role)
                                apply()
                            }

                            Toast.makeText(this, "Welcome, $firstName!", Toast.LENGTH_SHORT).show()
                            val intent = Intent(this, MainActivity::class.java)
                            startActivity(intent)
                            finish()
                        } else {
                            Toast.makeText(this, "Invalid Password", Toast.LENGTH_SHORT).show()
                        }
                    } else {
                        Toast.makeText(this, "User not found", Toast.LENGTH_SHORT).show()
                    }
                }
                .addOnFailureListener { e ->
                    Toast.makeText(this, "Error: ${e.message}", Toast.LENGTH_SHORT).show()
                }
        }
    }

    // This ensures that if the user presses 'Back' while on the Signin screen,
    // the Login UI becomes visible again.
    override fun onBackPressed() {
        val loginUiContent = findViewById<ScrollView>(R.id.login_ui_content)
        if (supportFragmentManager.backStackEntryCount > 0) {
            supportFragmentManager.popBackStack()
            loginUiContent.visibility = View.VISIBLE
        } else {
            super.onBackPressed()
        }
    }
}