package com.example.myfirstapp

import android.graphics.Color
import android.graphics.Typeface
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.fragment.app.Fragment
import com.google.android.material.button.MaterialButton

class OrderReadyFragment : Fragment() {

    companion object {
        fun newInstance(
            orderId: String,
            customer: String,
            orderType: String,
            urgent: Boolean,
            prepMinutes: Int,
            itemNames: Array<String>,
            itemNotes: Array<String>
        ): OrderReadyFragment {
            return OrderReadyFragment().apply {
                arguments = Bundle().apply {
                    putString("order_id", orderId)
                    putString("order_customer", customer)
                    putString("order_type", orderType)
                    putBoolean("order_urgent", urgent)
                    putInt("prep_minutes", prepMinutes)
                    putStringArray("item_names", itemNames)
                    putStringArray("item_notes", itemNotes)
                }
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? = inflater.inflate(R.layout.fragment_order_ready, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val args = requireArguments()
        val orderId = args.getString("order_id", "???")
        val customer = args.getString("order_customer", "Unknown")
        val orderType = args.getString("order_type", "PICKUP")
        val urgent = args.getBoolean("order_urgent", false)
        val prepMinutes = args.getInt("prep_minutes", 0)
        val itemNames = args.getStringArray("item_names") ?: emptyArray()
        val itemNotes = args.getStringArray("item_notes") ?: emptyArray()

        // ── Prep time ────────────────────────────────────────────────────────
        view.findViewById<TextView>(R.id.tvPrepTime).text = if (prepMinutes > 0) "$prepMinutes mins" else "< 1 min"

        // ── Order type badge ─────────────────────────────────────────────────
        val typeBadge = view.findViewById<TextView>(R.id.tvOrderTypeBadge)
        typeBadge.text = orderType.replace("_", " ").lowercase().replaceFirstChar { it.uppercase() }

        // ── VIP badge ────────────────────────────────────────────────────────
        val vipBadge = view.findViewById<TextView>(R.id.tvVipBadge)
        vipBadge.visibility = if (urgent) View.VISIBLE else View.GONE

        // ── Build order item rows ────────────────────────────────────────────
        val llItems = view.findViewById<LinearLayout>(R.id.llReadyOrderItems)
        itemNames.forEachIndexed { index, name ->
            val note = itemNotes.getOrElse(index) { "" }
            llItems.addView(buildItemCard(name, note))
        }

        // ── Customer delivery info ───────────────────────────────────────────
        view.findViewById<TextView>(R.id.tvCustomerDeliveryName).text = customer

        // ── Special instructions ─────────────────────────────────────────────
        val instructionsCard = view.findViewById<LinearLayout>(R.id.cardSpecialInstructions)
        val instructionsText = view.findViewById<TextView>(R.id.tvSpecialInstructions)
        val hasNotes = itemNotes.any { it.isNotBlank() }
        if (hasNotes) {
            instructionsText.text = itemNotes.filter { it.isNotBlank() }.joinToString("\n")
        } else {
            instructionsCard.visibility = View.GONE
        }

        // ── Assign Rider button ──────────────────────────────────────────────
        view.findViewById<MaterialButton>(R.id.btnAssignRider).setOnClickListener {
            val fragment = SelectRiderFragment.newInstance(orderId)
            parentFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, fragment)
                .addToBackStack(null)
                .commit()
        }

        // ── Notify Customer button ───────────────────────────────────────────
        view.findViewById<MaterialButton>(R.id.btnNotifyCustomer).setOnClickListener {
            Toast.makeText(requireContext(), "Customer notified for Order #$orderId", Toast.LENGTH_SHORT).show()
        }
    }

    // ── Helpers ───────────────────────────────────────────────────────────────

    private fun buildItemCard(rawName: String, note: String): LinearLayout {
        val regex = Regex("""^(\d+)[xX]\s+(.+)$""")
        val match = regex.find(rawName.trim())
        val qty = match?.groupValues?.get(1) ?: "1"
        val name = match?.groupValues?.get(2) ?: rawName

        val ctx = requireContext()

        val card = LinearLayout(ctx).apply {
            orientation = LinearLayout.VERTICAL
            setBackgroundResource(R.drawable.bg_staff_action_tile)
            setPadding(dpToPx(14), dpToPx(12), dpToPx(14), dpToPx(12))
        }

        // Top row: quantity + name
        val topRow = LinearLayout(ctx).apply {
            orientation = LinearLayout.HORIZONTAL
            gravity = android.view.Gravity.CENTER_VERTICAL
        }

        topRow.addView(TextView(ctx).apply {
            text = "${qty}×"
            setTextColor(Color.parseColor("#1A1A1A"))
            textSize = 15f
            setTypeface(null, Typeface.BOLD)
            layoutParams = LinearLayout.LayoutParams(dpToPx(36), -2)
        })

        topRow.addView(TextView(ctx).apply {
            text = name
            setTextColor(Color.parseColor("#1A1A1A"))
            textSize = 15f
            layoutParams = LinearLayout.LayoutParams(0, -2, 1f)
        })

        card.addView(topRow)

        // Note lines
        if (note.isNotEmpty()) {
            note.lines().filter { it.isNotBlank() }.forEach { line ->
                card.addView(TextView(ctx).apply {
                    text = line
                    textSize = 13f
                    when {
                        line.startsWith("+") || line.startsWith("-") -> {
                            setTextColor(Color.parseColor("#BE1028"))
                            setTypeface(null, Typeface.BOLD)
                        }
                        else -> {
                            setTextColor(Color.parseColor("#777777"))
                            setTypeface(null, Typeface.ITALIC)
                        }
                    }
                    layoutParams = LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.WRAP_CONTENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT
                    ).also { it.setMargins(dpToPx(36), dpToPx(4), 0, 0) }
                })
            }
        }

        return card
    }

    private fun dpToPx(dp: Int): Int =
        (dp * resources.displayMetrics.density).toInt()
}
