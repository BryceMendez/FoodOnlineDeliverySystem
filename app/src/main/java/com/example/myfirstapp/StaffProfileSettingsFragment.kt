package com.example.myfirstapp

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.google.firebase.firestore.FirebaseFirestore

class StaffProfileSettingsFragment : Fragment() {

    private val db = FirebaseFirestore.getInstance()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_staff_profile_settings, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Profile header views
        val tvProfileName = view.findViewById<TextView>(R.id.tvProfileName)
        val tvProfileRole = view.findViewById<TextView>(R.id.tvProfileRole)
        val tvProfileBranch = view.findViewById<TextView>(R.id.tvProfileBranch)

        // Personal info views
        val tvInfoName = view.findViewById<TextView>(R.id.tvInfoName)
        val tvInfoEmail = view.findViewById<TextView>(R.id.tvInfoEmail)
        val tvInfoPhone = view.findViewById<TextView>(R.id.tvInfoPhone)

        val btnLogout = view.findViewById<Button>(R.id.btnLogoutStaff)

        // Load user data from SharedPreferences first (fast)
        val sharedPref = requireActivity().getSharedPreferences("UserSession", Context.MODE_PRIVATE)
        val savedName = sharedPref.getString("full_name", "") ?: ""
        val savedEmail = sharedPref.getString("email", "") ?: ""
        val savedRole = sharedPref.getString("user_role", "") ?: ""

        // Populate with cached data immediately
        if (savedName.isNotEmpty()) {
            tvProfileName.text = savedName
            tvInfoName.text = savedName
        }
        if (savedEmail.isNotEmpty()) {
            tvInfoEmail.text = savedEmail
        }
        if (savedRole.isNotEmpty()) {
            tvProfileRole.text = savedRole.uppercase()
        }

        // Fetch full user details from Firestore
        if (savedEmail.isNotEmpty()) {
            db.collection("users").document(savedEmail).get()
                .addOnSuccessListener { document ->
                    if (document != null && document.exists()) {
                        val firstName = document.getString("firstName") ?: ""
                        val lastName = document.getString("lastName") ?: ""
                        val email = document.getString("email") ?: savedEmail
                        val mobile = document.getString("mobile") ?: ""
                        val role = document.getString("role") ?: savedRole
                        val fullName = "$firstName $lastName".trim()

                        // Update profile header
                        if (fullName.isNotEmpty()) {
                            tvProfileName.text = fullName
                            tvInfoName.text = fullName
                        }
                        tvInfoEmail.text = email
                        tvProfileRole.text = role.uppercase()

                        // Update phone
                        if (mobile.isNotEmpty()) {
                            tvInfoPhone.text = mobile
                        } else {
                            tvInfoPhone.text = "Not provided"
                        }

                        // Update branch if available
                        val branch = document.getString("branch")
                        if (!branch.isNullOrEmpty()) {
                            tvProfileBranch.text = branch
                            tvProfileBranch.visibility = View.VISIBLE
                        }
                    }
                }
                .addOnFailureListener {
                    // Firestore fetch failed, but we already have SharedPreferences data
                    if (savedName.isEmpty()) {
                        tvProfileName.text = "Unknown User"
                        tvInfoName.text = "Unknown User"
                    }
                    tvInfoPhone.text = "Unavailable"
                }
        }

        // Logout button
        btnLogout.setOnClickListener {
            // Clear session
            sharedPref.edit().clear().apply()

            Toast.makeText(context, "Logged out", Toast.LENGTH_SHORT).show()

            // Navigate back to Login screen
            val intent = Intent(requireActivity(), Login::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
            requireActivity().finish()
        }
    }
}
