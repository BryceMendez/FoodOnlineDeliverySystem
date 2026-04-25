package com.example.myfirstapp

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.TextView
import android.widget.Toast
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

        drawerLayout     = findViewById(R.id.drawer_layout)
        bottomNav        = findViewById(R.id.bottom_navigation)
        navigationDrawer = findViewById(R.id.navigation_drawer)

        val sharedPref = getSharedPreferences("UserSession", Context.MODE_PRIVATE)
        val role = sharedPref.getString("user_role", "Customer")

        when (role) {
            "Admin"  -> setupAdminUI()
            "Staff"  -> setupStaffUI()
            else     -> setupCustomerUI()
        }

        updateNavHeader()
    }

    // ── Customer ──────────────────────────────────────────────────────────────
    private fun setupCustomerUI() {
        bottomNav.menu.clear()
        bottomNav.inflateMenu(R.menu.bottom_nav_menu)

        replaceFragment(Home())

        bottomNav.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_home     -> replaceFragment(Home())
                R.id.nav_menu     -> replaceFragment(Menu())
                R.id.nav_vouchers -> replaceFragment(Vouchers())
                R.id.nav_tracker  -> replaceFragment(Tracker())
            }
            true
        }
    }

    // ── Staff ─────────────────────────────────────────────────────────────────
    private fun setupStaffUI() {
        bottomNav.menu.clear()
        bottomNav.inflateMenu(R.menu.staff_bottom_nav_menu)

        replaceFragment(StaffDashboard())
        drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED)

        bottomNav.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_staff_orders   -> replaceFragment(StaffDashboard())
                R.id.nav_staff_history  -> replaceFragment(SelectRiderFragment())
                R.id.nav_staff_profile  -> replaceFragment(StaffProfileSettingsFragment())
                R.id.nav_staff_settings -> replaceFragment(StaffSettingsFragment())
            }
            true
        }
    }

    // ── Admin ─────────────────────────────────────────────────────────────────
    private fun setupAdminUI() {
        bottomNav.menu.clear()
        bottomNav.inflateMenu(R.menu.admin_bottom_nav_menu)

        replaceFragment(AdminDashboard())
        drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED)

        bottomNav.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_admin_dashboard  -> replaceFragment(AdminDashboard())
                R.id.nav_admin_branches   -> replaceFragment(AdminBranches())
                R.id.nav_admin_customers  -> replaceFragment(AdminCustomers())
                R.id.nav_admin_analytics  -> replaceFragment(AdminAnalytics())
            }
            true
        }
    }

    // ── Nav header ────────────────────────────────────────────────────────────
    private fun updateNavHeader() {
        val headerView: View = navigationDrawer.getHeaderView(0)
        val tvNavName  = headerView.findViewById<TextView>(R.id.nav_user_name)
        val tvNavEmail = headerView.findViewById<TextView>(R.id.nav_user_email)

        val sharedPref = getSharedPreferences("UserSession", Context.MODE_PRIVATE)
        tvNavName.text  = sharedPref.getString("full_name", "Guest")
        tvNavEmail.text = sharedPref.getString("email", "")
    }

    private fun replaceFragment(fragment: Fragment) {
        supportFragmentManager.beginTransaction()
            .replace(R.id.fragment_container, fragment)
            .commit()
    }

    fun openDrawer() {
        drawerLayout.openDrawer(GravityCompat.START)
    }
}