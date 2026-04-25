package com.example.myfirstapp

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
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

        val nextBtn            = view.findViewById<Button>(R.id.btnNext)
        val rbTerms            = view.findViewById<RadioButton>(R.id.rbTerms)
        val etFirstName        = view.findViewById<EditText>(R.id.etFirstName)
        val etLastName         = view.findViewById<EditText>(R.id.etLastName)
        val etEmail            = view.findViewById<EditText>(R.id.etEmail)
        val etMobile           = view.findViewById<EditText>(R.id.etMobile)
        val etPassword         = view.findViewById<EditText>(R.id.etPassword)
        val etConfirmPassword  = view.findViewById<EditText>(R.id.etConfirmPassword)
        val actvUserType       = view.findViewById<AutoCompleteTextView>(R.id.actvUserType)
        val btnBack            = view.findViewById<android.widget.ImageButton>(R.id.btnBack)

        // ── Dropdown: Customer | Staff | Admin ───────────────────────────────
        val roles = arrayOf("Customer", "Staff", "Admin")
        val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_list_item_1, roles)
        actvUserType.setAdapter(adapter)

        btnBack.setOnClickListener {
            requireActivity().onBackPressed()
        }

        nextBtn.setOnClickListener {
            val firstName   = etFirstName.text.toString().trim()
            val lastName    = etLastName.text.toString().trim()
            val email       = etEmail.text.toString().trim()
            val mobile      = etMobile.text.toString().trim()
            val password    = etPassword.text.toString().trim()
            val confirmPass = etConfirmPassword.text.toString().trim()
            val userType    = actvUserType.text.toString()

            // ── Validation ───────────────────────────────────────────────────
            if (firstName.isEmpty() || lastName.isEmpty() || email.isEmpty() ||
                mobile.isEmpty() || password.isEmpty() || userType.isEmpty()) {
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

            // ── Save to Firestore ────────────────────────────────────────────
            val hashedPassword = BCrypt.hashpw(password, BCrypt.gensalt())
            val user = hashMapOf(
                "firstName" to firstName,
                "lastName"  to lastName,
                "email"     to email,
                "mobile"    to mobile,
                "password"  to hashedPassword,
                "role"      to userType
            )

            db.collection("users").document(email)
                .set(user)
                .addOnSuccessListener {
                    Toast.makeText(context, "Account Created Successfully!", Toast.LENGTH_SHORT).show()

                    // ── Save session ─────────────────────────────────────────
                    val sharedPref = requireActivity().getSharedPreferences("UserSession", Context.MODE_PRIVATE)
                    with(sharedPref.edit()) {
                        putString("full_name", "$firstName $lastName")
                        putString("email",     email)
                        putString("user_role", userType)
                        apply()
                    }

                    // ── Route by role ────────────────────────────────────────
                    val intent = Intent(requireActivity(), MainActivity::class.java)
                    startActivity(intent)
                    requireActivity().finish()
                    // MainActivity already reads user_role and routes to:
                    //   "Admin"    → AdminDashboard  (handled in setupAdminUI)
                    //   "Staff"    → StaffDashboard
                    //   "Customer" → Home
                }
                .addOnFailureListener { e ->
                    Toast.makeText(context, "Error: ${e.message}", Toast.LENGTH_SHORT).show()
                }
        }
    }
}