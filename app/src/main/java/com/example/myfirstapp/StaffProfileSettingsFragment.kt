package com.example.myfirstapp

import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.widget.SwitchCompat
import androidx.fragment.app.Fragment

class StaffProfileSettingsFragment : Fragment(R.layout.fragment_staff_profile_settings) {

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val pushSwitch = view.findViewById<SwitchCompat>(R.id.switchPushNotifications)
        val shiftSwitch = view.findViewById<SwitchCompat>(R.id.switchShiftReminders)
        val logoutButton = view.findViewById<Button>(R.id.btnLogoutStaff)

        pushSwitch.setOnCheckedChangeListener { _, isChecked ->
            val message = if (isChecked) "Push notifications enabled" else "Push notifications disabled"
            Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
        }

        shiftSwitch.setOnCheckedChangeListener { _, isChecked ->
            val message = if (isChecked) "Shift reminders enabled" else "Shift reminders disabled"
            Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
        }

        logoutButton.setOnClickListener {
            Toast.makeText(requireContext(), "Logout action tapped", Toast.LENGTH_SHORT).show()
        }

        StaffBottomNavHelper.bind(this, view, R.id.nav_staff_settings)
    }
}
