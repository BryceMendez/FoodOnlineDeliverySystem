package com.example.myfirstapp

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment

class SelectRiderFragment : Fragment() {

    // Order ID passed in from the dashboard when a delivery order is tapped
    private var orderId: String = "1234"

    companion object {
        private const val ARG_ORDER_ID = "order_id"

        fun newInstance(orderId: String): SelectRiderFragment {
            return SelectRiderFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_ORDER_ID, orderId)
                }
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        orderId = arguments?.getString(ARG_ORDER_ID) ?: "1234"
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_select_rider, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Cancel button in bottom bar
        view.findViewById<TextView>(R.id.btnCancelRider).setOnClickListener {
            parentFragmentManager.popBackStack()
        }

        // View Map button
        view.findViewById<Button>(R.id.btnViewMap).setOnClickListener {
            Toast.makeText(requireContext(), "Opening map for Order #$orderId", Toast.LENGTH_SHORT).show()
            // TODO: Launch your map activity or fragment here
        }

        // Assign Rider buttons
        view.findViewById<Button>(R.id.btnAssignMarcus).setOnClickListener {
            assignRider("Marcus Chen", orderId)
        }

        view.findViewById<Button>(R.id.btnAssignSarah).setOnClickListener {
            assignRider("Sarah Jenkins", orderId)
        }

        view.findViewById<Button>(R.id.btnAssignDavid).setOnClickListener {
            assignRider("David Okafor", orderId)
        }

        view.findViewById<Button>(R.id.btnAssignLeo).setOnClickListener {
            assignRider("Leo Thompson", orderId)
        }
    }

    private fun assignRider(riderName: String, orderId: String) {
        Toast.makeText(
            requireContext(),
            "$riderName assigned to Order #$orderId",
            Toast.LENGTH_SHORT
        ).show()

        // TODO: Call your Firestore/API here to save the assignment
        // After saving, navigate back to the dashboard
        parentFragmentManager.popBackStack()
    }
}
