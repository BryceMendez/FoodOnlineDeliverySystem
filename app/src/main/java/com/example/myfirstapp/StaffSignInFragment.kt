package com.example.myfirstapp

import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment

class StaffSignInFragment : Fragment(R.layout.fragment_staff_signin) {

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val employeeIdField = view.findViewById<EditText>(R.id.etStaffEmployeeId)
        val pinField = view.findViewById<EditText>(R.id.etStaffPin)
        val signInButton = view.findViewById<Button>(R.id.btnStaffSignIn)
        val forgotPinText = view.findViewById<TextView>(R.id.tvForgotPin)
        val supportText = view.findViewById<TextView>(R.id.tvSupport)
        val languageText = view.findViewById<TextView>(R.id.tvLanguage)

        signInButton.setOnClickListener {
            val employeeId = employeeIdField.text.toString().trim()
            val pin = pinField.text.toString().trim()

            if (employeeId.isEmpty() || pin.isEmpty()) {
                Toast.makeText(requireContext(), "Please enter Employee ID and PIN.", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(requireContext(), "Staff sign-in ready for backend validation.", Toast.LENGTH_SHORT).show()
            }
        }

        forgotPinText.setOnClickListener {
            Toast.makeText(requireContext(), "Forgot PIN flow is not connected yet.", Toast.LENGTH_SHORT).show()
        }

        supportText.setOnClickListener {
            Toast.makeText(requireContext(), "Support action tapped.", Toast.LENGTH_SHORT).show()
        }

        languageText.setOnClickListener {
            Toast.makeText(requireContext(), "Language action tapped.", Toast.LENGTH_SHORT).show()
        }
    }
}
