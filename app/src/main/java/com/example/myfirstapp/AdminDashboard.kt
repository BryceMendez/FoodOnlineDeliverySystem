package com.example.myfirstapp

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.fragment.app.Fragment

// Requirement: Data Class (Renamed to avoid conflict with your other Order class)
data class AdminOrder(val id: String, val status: String, val amount: String)

class AdminDashboard : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? = inflater.inflate(R.layout.fragment_admin_dashboard, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Requirement: ListView
        val lvOrders = view.findViewById<ListView>(R.id.llRecentOrders)

        val ordersList = listOf(
            AdminOrder("#FD-8821", "Preparing", "$42.50"),
            AdminOrder("#FD-8819", "Delivered", "$128.00"),
            AdminOrder("#FD-8818", "Delivered", "$56.20")
        )

        // Requirement: ArrayAdapter
        val adapter = object : ArrayAdapter<AdminOrder>(requireContext(), android.R.layout.simple_list_item_1, ordersList) {
            override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
                val tv = super.getView(position, convertView, parent) as TextView
                val item = getItem(position)
                tv.text = "${item?.id}  -  ${item?.amount}"
                return tv
            }
        }
        lvOrders.adapter = adapter

        // Requirement: Data Passing
        lvOrders.setOnItemClickListener { _, _, position, _ ->
            val order = ordersList[position]

            val bundle = Bundle()
            bundle.putString("order_id", order.id) // <--- THIS IS THE DATA BEING PASSED

            val analyticsFrag = AdminAnalytics()
            analyticsFrag.arguments = bundle

            parentFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, analyticsFrag)
                .addToBackStack(null)
                .commit()
        }
    }
}