package com.example.myfirstapp

import android.view.View
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.google.android.material.bottomnavigation.BottomNavigationView

object StaffBottomNavHelper {

    fun bind(fragment: Fragment, rootView: View, selectedItemId: Int) {
        val bottomNav = rootView.findViewById<BottomNavigationView>(R.id.staffBottomNav)
        bottomNav.selectedItemId = selectedItemId
        bottomNav.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_staff_orders -> {
                    if (fragment !is StaffDashboard) {
                        fragment.parentFragmentManager.beginTransaction()
                            .replace(R.id.fragment_container_login, StaffDashboard())
                            .addToBackStack(null)
                            .commit()
                    }
                    true
                }

                R.id.nav_staff_history -> {
                    Toast.makeText(fragment.requireContext(), "History selected", Toast.LENGTH_SHORT).show()
                    true
                }

                R.id.nav_staff_team -> {
                    Toast.makeText(fragment.requireContext(), "Staff selected", Toast.LENGTH_SHORT).show()
                    true
                }

                R.id.nav_staff_settings -> {
                    if (fragment !is StaffSettingsFragment) {
                        fragment.parentFragmentManager.beginTransaction()
                            .replace(R.id.fragment_container_login, StaffSettingsFragment())
                            .addToBackStack(null)
                            .commit()
                    }
                    true
                }

                else -> false
            }
        }
    }
}
