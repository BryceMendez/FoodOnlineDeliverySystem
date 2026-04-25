package com.example.myfirstapp

import android.graphics.Color
import android.graphics.Typeface
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.fragment.app.Fragment
import com.google.android.material.button.MaterialButton

class PreparingOrderFragment : Fragment() {

    // ── Arguments ────────────────────────────────────────────────────────────
    private var order: Order? = null

    companion object {
        private const val ARG_ORDER = "arg_order"

        /** Call this from StaffDashboard when "START PREPARING" is tapped. */
        fun newInstance(order: Order): PreparingOrderFragment {
            return PreparingOrderFragment().apply {
                arguments = Bundle().apply {
                    // Pass individual fields since Order is a data class (not Parcelable yet)
                    putString("order_id",       order.id)
                    putString("order_customer", order.customer)
                    putString("order_time",     order.time)
                    putBoolean("order_urgent",  order.urgent)
                    putString("order_type",     order.type.name)
                    // Flatten items as parallel arrays
                    putStringArray("item_names",  order.items.map { it.name }.toTypedArray())
                    putStringArray("item_notes",  order.items.map { it.note ?: "" }.toTypedArray())
                }
            }
        }
    }

    // ── Elapsed-time ticker ───────────────────────────────────────────────────
    private val handler = Handler(Looper.getMainLooper())
    private var elapsedSeconds = 0
    private lateinit var tvElapsed: TextView

    private val ticker = object : Runnable {
        override fun run() {
            elapsedSeconds++
            val mins = elapsedSeconds / 60
            val secs = elapsedSeconds % 60
            tvElapsed.text = String.format("%02d:%02d", mins, secs)
            handler.postDelayed(this, 1000)
        }
    }

    // ── Lifecycle ─────────────────────────────────────────────────────────────
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? = inflater.inflate(R.layout.fragment_preparing_order, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Restore order from arguments
        val args = requireArguments()
        val orderId   = args.getString("order_id",       "???")
        val customer  = args.getString("order_customer", "Unknown")
        val urgent    = args.getBoolean("order_urgent",  false)
        val orderType = OrderType.valueOf(args.getString("order_type", "PICKUP"))
        val itemNames = args.getStringArray("item_names") ?: emptyArray()
        val itemNotes = args.getStringArray("item_notes") ?: emptyArray()

        // ── Order ID ──────────────────────────────────────────────────────────
        view.findViewById<TextView>(R.id.tvOrderId).text = "#$orderId"

        // ── Elapsed timer ─────────────────────────────────────────────────────
        tvElapsed = view.findViewById(R.id.tvElapsedTime)
        handler.postDelayed(ticker, 1000)

        // ── Customer info card ────────────────────────────────────────────────
        view.findViewById<TextView>(R.id.tvCustomerLabel).text = customer
        // Urgent note shown in customer note field if flagged
        val noteText = if (urgent) "⚠ URGENT ORDER — prioritise this ticket." else ""
        view.findViewById<TextView>(R.id.tvCustomerNote).let {
            it.text = noteText
            it.visibility = if (noteText.isNotEmpty()) View.VISIBLE else View.GONE
        }

        // ── Build order item rows dynamically ────────────────────────────────
        val llItems = view.findViewById<LinearLayout>(R.id.llOrderItems)
        itemNames.forEachIndexed { index, name ->
            val note = itemNotes.getOrElse(index) { "" }
            llItems.addView(buildItemRow(name, note))
            // Divider between items
            llItems.addView(buildDivider())
        }

        // ── Mark as Ready button ──────────────────────────────────────────────
        view.findViewById<MaterialButton>(R.id.btnMarkAsReady).setOnClickListener {
            Toast.makeText(
                requireContext(),
                "Order #$orderId marked as Ready!",
                Toast.LENGTH_SHORT
            ).show()

            // Navigate to OrderReadyFragment, passing order data
            val readyFragment = OrderReadyFragment.newInstance(
                orderId = orderId,
                customer = customer,
                orderType = orderType.name,
                urgent = urgent,
                prepMinutes = elapsedSeconds / 60,
                itemNames = itemNames,
                itemNotes = itemNotes
            )
            parentFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, readyFragment)
                .addToBackStack(null)
                .commit()
        }

        // ── Print button ──────────────────────────────────────────────────────
        view.findViewById<MaterialButton>(R.id.btnPrint).setOnClickListener {
            Toast.makeText(requireContext(), "Printing ticket for #$orderId…", Toast.LENGTH_SHORT).show()
            // TODO: hook up actual printer integration
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        handler.removeCallbacks(ticker) // Stop timer when fragment is removed
    }

    // ── Helpers ───────────────────────────────────────────────────────────────

    /**
     * Builds a single item row:
     *
     *   2x  Double Truffle Burger          ≡
     *       + Extra Swiss Cheese  (red)
     *       - No Onions           (red)
     */
    private fun buildItemRow(rawName: String, note: String): LinearLayout {
        // Parse "2x Item Name" format
        val regex = Regex("""^(\d+)[xX]\s+(.+)$""")
        val match  = regex.find(rawName.trim())
        val qty    = match?.groupValues?.get(1) ?: "1"
        val name   = match?.groupValues?.get(2) ?: rawName

        val ctx = requireContext()

        val row = LinearLayout(ctx).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(dpToPx(20), dpToPx(18), dpToPx(20), dpToPx(18))
        }

        // Quantity + name row
        val nameRow = LinearLayout(ctx).apply {
            orientation = LinearLayout.HORIZONTAL
            gravity = android.view.Gravity.CENTER_VERTICAL
        }

        nameRow.addView(TextView(ctx).apply {
            text = "${qty}×"
            setTextColor(Color.parseColor("#1A1A1A"))
            textSize = 16f
            setTypeface(null, Typeface.BOLD)
            layoutParams = LinearLayout.LayoutParams(dpToPx(40), -2)
        })

        nameRow.addView(TextView(ctx).apply {
            text = name
            setTextColor(Color.parseColor("#1A1A1A"))
            textSize = 16f
            layoutParams = LinearLayout.LayoutParams(0, -2, 1f)
        })

        // Drag handle icon (decorative)
        nameRow.addView(TextView(ctx).apply {
            text = "≡"
            setTextColor(Color.parseColor("#CCCCCC"))
            textSize = 20f
        })

        row.addView(nameRow)

        // Parse note lines: lines starting with '+' or '-' get red colouring
        // A plain note (no prefix) renders as italic grey quote
        if (note.isNotEmpty()) {
            note.lines().filter { it.isNotBlank() }.forEach { line ->
                row.addView(buildNoteChip(line, ctx))
            }
        }

        return row
    }

    private fun buildNoteChip(line: String, ctx: android.content.Context): TextView {
        return TextView(ctx).apply {
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            ).also { it.setMargins(dpToPx(40), dpToPx(4), 0, 0) }

            when {
                line.startsWith("+") -> {
                    text = line
                    setTextColor(Color.parseColor("#BE1028"))
                    setTypeface(null, Typeface.BOLD)
                    textSize = 13f
                }
                line.startsWith("-") -> {
                    text = line
                    setTextColor(Color.parseColor("#BE1028"))
                    setTypeface(null, Typeface.BOLD)
                    textSize = 13f
                }
                else -> {
                    // Plain instruction — shown as italic grey quote
                    text = "\"$line\""
                    setTextColor(Color.parseColor("#777777"))
                    setTypeface(null, Typeface.ITALIC)
                    textSize = 13f
                }
            }
        }
    }

    private fun buildDivider(): View = View(requireContext()).apply {
        layoutParams = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT, 1
        )
        setBackgroundColor(Color.parseColor("#F0F0F0"))
    }

    private fun dpToPx(dp: Int): Int =
        (dp * resources.displayMetrics.density).toInt()
}
