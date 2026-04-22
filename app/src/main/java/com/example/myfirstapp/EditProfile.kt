package com.example.myfirstapp

import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView

class EditProfile : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_editprofile, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // 1. Find the Views
        val tvName = view.findViewById<TextView>(R.id.tvProfileName)
        val tvEmail = view.findViewById<TextView>(R.id.tvProfileEmail)
        val tvMobile = view.findViewById<TextView>(R.id.tvProfileMobile)
        val btnBack = view.findViewById<ImageView>(R.id.btnBack)

        // 2. BACK BUTTON LOGIC
        btnBack.setOnClickListener {
            // Returns to the previous fragment (Home)
            parentFragmentManager.popBackStack()
        }

        // 3. Load data from SharedPreferences
        val sharedPref = requireActivity().getSharedPreferences("UserSession", Context.MODE_PRIVATE)

        val savedName = sharedPref.getString("full_name", "User Name")
        val savedEmail = sharedPref.getString("email_address", "Email not set")
        val savedMobile = sharedPref.getString("mobile_number", "Mobile not set")

        // 4. Set the text to the UI
        tvName.text = savedName
        tvEmail.text = savedEmail
        tvMobile.text = savedMobile
    }
}