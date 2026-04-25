package com.example.myfirstapp

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.fragment.app.Fragment

data class AdminCustomer(val name: String, val email: String, val status: String)

class AdminCustomers : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? = inflater.inflate(R.layout.fragment_admin_customers, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val lvCustomers = view.findViewById<ListView>(R.id.rvCustomers)

        val customers = listOf(
            AdminCustomer("Eleanor Parker", "eleanor.p@gmail.com", "ACTIVE"),
            AdminCustomer("Cody Fischer", "cody.f@gmail.com", "SUSPENDED")
        )

        val adapter = object : ArrayAdapter<AdminCustomer>(requireContext(), android.R.layout.simple_list_item_2, customers) {
            override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
                val v = super.getView(position, convertView, parent)
                v.findViewById<TextView>(android.R.id.text1).text = customers[position].name
                v.findViewById<TextView>(android.R.id.text2).text = customers[position].email
                return v
            }
        }
        lvCustomers.adapter = adapter
    }
}