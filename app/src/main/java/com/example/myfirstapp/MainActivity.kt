package com.example.myfirstapp

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.fragment.app.Fragment
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.navigation.NavigationView

class MainActivity : AppCompatActivity() {

    lateinit var drawerLayout: DrawerLayout
    private lateinit var bottomNav: BottomNavigationView
    private lateinit var navigationDrawer: NavigationView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Initialize Views
        drawerLayout = findViewById(R.id.drawer_layout)
        bottomNav = findViewById(R.id.bottom_navigation)
        navigationDrawer = findViewById(R.id.navigation_drawer)

        // Load user data into the sidebar header immediately
        updateNavHeader()

        // Set default fragment (Home)
        if (savedInstanceState == null) {
            replaceFragment(Home())
            bottomNav.selectedItemId = R.id.nav_home
        }

        // Bottom Navigation Click Listener
        bottomNav.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_home -> replaceFragment(Home())
                R.id.nav_menu -> replaceFragment(Menu())
                R.id.nav_vouchers -> replaceFragment(Vouchers())
                R.id.nav_tracker -> replaceFragment(Tracker())
            }
            true
        }

        // Side Menu Click Listener
        navigationDrawer.setNavigationItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_logout -> {
                    // Clear session and go to Login
                    val intent = Intent(this, Login::class.java)
                    intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                    startActivity(intent)
                    finish()
                }
                // Handle other side menu items here if needed
            }
            drawerLayout.closeDrawer(GravityCompat.START)
            true
        }
    }

    // Function to update Name and Email in the Side Menu
    private fun updateNavHeader() {
        val headerView: View = navigationDrawer.getHeaderView(0)
        val tvNavName = headerView.findViewById<TextView>(R.id.nav_user_name)
        val tvNavEmail = headerView.findViewById<TextView>(R.id.nav_user_email)

        // Fetch data saved during Login/Sign-up
        val sharedPref = getSharedPreferences("UserSession", Context.MODE_PRIVATE)
        val savedName = sharedPref.getString("full_name", "Welcome Guest")
        val savedEmail = sharedPref.getString("email_address", "Please sign in")

        tvNavName.text = savedName
        tvNavEmail.text = savedEmail
    }

    private fun replaceFragment(fragment: Fragment) {
        supportFragmentManager.beginTransaction()
            .replace(R.id.fragment_container, fragment)
            .commit()
    }

    // Called by Home Fragment to open the sidebar
    fun openDrawer() {
        updateNavHeader() // Refresh info every time drawer opens
        drawerLayout.openDrawer(GravityCompat.START)
    }

    override fun onBackPressed() {
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START)
        } else {
            super.onBackPressed()
        }
    }
}