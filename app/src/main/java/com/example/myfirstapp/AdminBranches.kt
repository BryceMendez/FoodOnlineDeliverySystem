package com.example.myfirstapp

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.fragment.app.Fragment

data class AdminBranch(
    val name: String,
    val address: String,
    val manager: String,
    val status: String
)

class AdminBranches : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? = inflater.inflate(R.layout.fragment_admin_branches, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val lvBranches = view.findViewById<ListView>(R.id.llBranchCards)

        val branches = listOf(
            AdminBranch("Downtown Central", "452 Broadway Ave", "Elena Rodriguez", "OPEN"),
            AdminBranch("Northside Hub", "89 Industrial Pkwy", "Marcus Chen", "RENOVATION")
        )

        val adapter = object : ArrayAdapter<AdminBranch>(requireContext(), android.R.layout.simple_list_item_2, branches) {
            override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
                val row = convertView ?: LayoutInflater.from(context).inflate(android.R.layout.simple_list_item_2, parent, false)
                val item = getItem(position)
                row.findViewById<TextView>(android.R.id.text1).text = item?.name
                row.findViewById<TextView>(android.R.id.text2).text = "${item?.status} - Mgr: ${item?.manager}"
                return row
            }
        }
        lvBranches.adapter = adapter
    }
}