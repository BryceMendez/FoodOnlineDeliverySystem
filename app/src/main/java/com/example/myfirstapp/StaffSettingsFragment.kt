package com.example.myfirstapp

import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.widget.SwitchCompat
import androidx.fragment.app.Fragment

class StaffSettingsFragment : Fragment(R.layout.fragment_staff_settings) {

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val autoAcceptSwitch = view.findViewById<SwitchCompat>(R.id.switchAutoAccept)
        val soundSwitch = view.findViewById<SwitchCompat>(R.id.switchSound)
        val darkModeSwitch = view.findViewById<SwitchCompat>(R.id.switchDarkMode)
        val closeStoreButton = view.findViewById<Button>(R.id.btnCloseStore)
        val testPrintButton = view.findViewById<TextView>(R.id.btnTestPrint)   // TextView in XML
        val callSupportButton = view.findViewById<TextView>(R.id.btnCallSupport) // TextView in XML

        autoAcceptSwitch.setOnCheckedChangeListener { _, checked ->
            Toast.makeText(requireContext(), if (checked) "Auto-accept enabled" else "Auto-accept disabled", Toast.LENGTH_SHORT).show()
        }
        soundSwitch.setOnCheckedChangeListener { _, checked ->
            Toast.makeText(requireContext(), if (checked) "Sound enabled" else "Sound disabled", Toast.LENGTH_SHORT).show()
        }
        darkModeSwitch.setOnCheckedChangeListener { _, checked ->
            Toast.makeText(requireContext(), if (checked) "Dark mode enabled" else "Dark mode disabled", Toast.LENGTH_SHORT).show()
        }

        closeStoreButton.setOnClickListener {
            Toast.makeText(requireContext(), "Close store action tapped", Toast.LENGTH_SHORT).show()
        }
        testPrintButton.setOnClickListener {
            Toast.makeText(requireContext(), "Print test sent", Toast.LENGTH_SHORT).show()
        }
        callSupportButton.setOnClickListener {
            Toast.makeText(requireContext(), "Calling support...", Toast.LENGTH_SHORT).show()
        }
    }
}