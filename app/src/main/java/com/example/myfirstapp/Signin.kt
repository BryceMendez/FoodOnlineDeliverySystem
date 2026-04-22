package com.example.myfirstapp

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.RadioButton
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.google.firebase.firestore.FirebaseFirestore
import org.mindrot.jbcrypt.BCrypt

class Signin : Fragment() {

    private val db = FirebaseFirestore.getInstance()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_signin, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val nextBtn = view.findViewById<Button>(R.id.btnNext)
        val rbTerms = view.findViewById<RadioButton>(R.id.rbTerms)
        val etFirstName = view.findViewById<EditText>(R.id.etFirstName)
        val etLastName = view.findViewById<EditText>(R.id.etLastName)
        val etEmail = view.findViewById<EditText>(R.id.etEmail)
        val etMobile = view.findViewById<EditText>(R.id.etMobile)
        val etPassword = view.findViewById<EditText>(R.id.etPassword)
        val etConfirmPassword = view.findViewById<EditText>(R.id.etConfirmPassword)

        nextBtn.setOnClickListener {
            val firstName = etFirstName.text.toString().trim()
            val lastName = etLastName.text.toString().trim()
            val email = etEmail.text.toString().trim()
            val mobile = etMobile.text.toString().trim()
            val password = etPassword.text.toString().trim()
            val confirmPass = etConfirmPassword.text.toString().trim()

            // 1. Validation
            if (firstName.isEmpty() || email.isEmpty() || password.isEmpty()) {
                Toast.makeText(context, "Please fill in all fields", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (password != confirmPass) {
                Toast.makeText(context, "Passwords do not match", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (!rbTerms.isChecked) {
                Toast.makeText(context, "Please agree to the Terms and Conditions", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // 2. HASH THE PASSWORD
            // We salt and hash so the real password is never stored
            val hashedPassword = BCrypt.hashpw(password, BCrypt.gensalt())

            // 3. Prepare Data
            val user = hashMapOf(
                "firstName" to firstName,
                "lastName" to lastName,
                "email" to email,
                "mobile" to mobile,
                "password" to hashedPassword // Storing the HASHED version
            )

            // 4. Save to Firestore
            db.collection("users").document(email)
                .set(user)
                .addOnSuccessListener {
                    Toast.makeText(context, "Account Created Successfully!", Toast.LENGTH_SHORT).show()

                    val sharedPref = requireActivity().getSharedPreferences("UserSession", Context.MODE_PRIVATE)
                    val editor = sharedPref.edit()
                    editor.putString("full_name", "$firstName $lastName")
                    editor.apply()

                    val intent = Intent(requireActivity(), MainActivity::class.java)
                    startActivity(intent)
                    requireActivity().finish()
                }
                .addOnFailureListener { e ->
                    Toast.makeText(context, "Error: ${e.message}", Toast.LENGTH_SHORT).show()
                }
        }
    }
}