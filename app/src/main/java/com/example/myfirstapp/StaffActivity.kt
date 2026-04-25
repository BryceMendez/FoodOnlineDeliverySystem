package com.example.myfirstapp

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.google.android.material.bottomnavigation.BottomNavigationView

class StaffActivity : AppCompatActivity() {

    private lateinit var bottomNav: BottomNavigationView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_staff)

        bottomNav = findViewById(R.id.staffBottomNav)

        // Load default fragment on launch
        if (savedInstanceState == null) {
            loadFragment(StaffDashboard())
            bottomNav.selectedItemId = R.id.nav_staff_orders
        }

        bottomNav.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_staff_orders -> {
                    loadFragment(StaffDashboard())
                    true
                }
                R.id.nav_staff_history -> {
                    loadFragment(StaffDashboard()) // 🔴 replace with your actual history fragment
                    true
                }
                R.id.nav_staff_profile -> {
                    loadFragment(StaffProfileSettingsFragment())
                    true
                }
                R.id.nav_staff_settings -> {
                    loadFragment(StaffSettingsFragment())
                    true
                }
                else -> false
            }
        }
    }

    private fun loadFragment(fragment: Fragment) {
        supportFragmentManager.beginTransaction()
            .replace(R.id.fragment_container, fragment) // 🔴 replace with your actual FrameLayout container ID
            .commit()
    }
}