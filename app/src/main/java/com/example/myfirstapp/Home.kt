package com.example.myfirstapp

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment

class Home : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_home, container, false)

        val btnSideMenu = view.findViewById<ImageView>(R.id.btn_side_menu)
        val btnSearch = view.findViewById<ImageView>(R.id.btn_search)
        val btnProfile = view.findViewById<ImageView>(R.id.btn_profile)
        val etSearch = view.findViewById<EditText>(R.id.et_search)
        val tvTitle = view.findViewById<TextView>(R.id.tv_title)

        btnSideMenu.setOnClickListener {
            (activity as? MainActivity)?.openDrawer()
        }

        // Search Button Logic
        btnSearch.setOnClickListener {
            if (etSearch.visibility == View.GONE) {
                etSearch.visibility = View.VISIBLE
                tvTitle.visibility = View.GONE
                etSearch.requestFocus()
                btnSearch.setImageResource(android.R.drawable.ic_menu_close_clear_cancel)
            } else {
                etSearch.visibility = View.GONE
                tvTitle.visibility = View.VISIBLE
                etSearch.text.clear()
                btnSearch.setImageResource(R.drawable.ic_search)
            }
        }

        etSearch.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                Toast.makeText(context, "Searching for: ${etSearch.text}", Toast.LENGTH_SHORT).show()
                true
            } else false
        }

        // Profile Button Redirection
        btnProfile.setOnClickListener {
            parentFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, EditProfile())
                .addToBackStack(null)
                .commit()
        }

        return view
    }
}