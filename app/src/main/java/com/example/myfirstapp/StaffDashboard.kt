package com.example.myfirstapp

import android.graphics.Color
import android.os.Bundle
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.cardview.widget.CardView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.FloatingActionButton

// ─── Models ───────────────────────────────────────────────────────────────────

enum class OrderType { PICKUP, DELIVERY }
enum class OrderStatus { NEW, PREPARING, READY, DONE }

data class OrderItem(val name: String, val note: String? = null)

data class Order(
    val id: String,
    val customerName: String,
    val timeAgo: String,
    val isUrgent: Boolean = false,
    val items: List<OrderItem>,
    val type: OrderType,
    val status: OrderStatus
)

// ─── Adapter (cards built fully programmatically) ─────────────────────────────

class OrderCardAdapter(
    private var orders: List<Order>,
    private val onStartPreparing: (Order) -> Unit
) : RecyclerView.Adapter<OrderCardAdapter.VH>() {

    inner class VH(val card: CardView) : RecyclerView.ViewHolder(card)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH {
        val card = CardView(parent.context).apply {
            layoutParams = RecyclerView.LayoutParams(
                RecyclerView.LayoutParams.MATCH_PARENT,
                RecyclerView.LayoutParams.WRAP_CONTENT
            ).also { it.bottomMargin = 48 }
            radius = 36f
            cardElevation = 4f
            setCardBackgroundColor(Color.WHITE)
        }
        return VH(card)
    }

    override fun getItemCount() = orders.size

    override fun onBindViewHolder(h: VH, position: Int) {
        val o   = orders[position]
        val ctx = h.card.context
        val dp  = ctx.resources.displayMetrics.density

        h.card.removeAllViews()

        // Accent bar color: red = pickup/urgent, blue = delivery
        val accentColor = if (o.type == OrderType.DELIVERY)
            Color.parseColor("#3A8DCC") else Color.parseColor("#BE1028")

        // Root row: accent bar | content
        val root = LinearLayout(ctx).apply {
            orientation = LinearLayout.HORIZONTAL
            layoutParams = ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            )
        }

        // Left accent bar
        root.addView(View(ctx).apply {
            layoutParams = LinearLayout.LayoutParams(
                (4 * dp).toInt(), LinearLayout.LayoutParams.MATCH_PARENT
            )
            setBackgroundColor(accentColor)
        })

        // Content column
        val content = LinearLayout(ctx).apply {
            orientation = LinearLayout.VERTICAL
            layoutParams = LinearLayout.LayoutParams(
                0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f
            )
            val p = (14 * dp).toInt()
            setPadding(p, p, p, p)
        }

        // ── Header: order number | time + urgent badge ──
        val headerRow = LinearLayout(ctx).apply {
            orientation = LinearLayout.HORIZONTAL
            gravity = Gravity.CENTER_VERTICAL
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            )
        }

        headerRow.addView(TextView(ctx).apply {
            text     = "#${o.id}"
            textSize = 26f
            setTypeface(typeface, android.graphics.Typeface.BOLD)
            setTextColor(Color.parseColor("#1A1A1A"))
            layoutParams = LinearLayout.LayoutParams(
                0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f
            )
        })

        // Time + optional URGENT badge stacked on the right
        val timeCol = LinearLayout(ctx).apply {
            orientation = LinearLayout.VERTICAL
            gravity = Gravity.END
        }

        val timeRow = LinearLayout(ctx).apply {
            orientation = LinearLayout.HORIZONTAL
            gravity = Gravity.CENTER_VERTICAL
        }
        timeRow.addView(ImageView(ctx).apply {
            setImageResource(R.drawable.ic_cart)
            val iconColor = if (o.isUrgent) Color.parseColor("#BE1028")
            else Color.parseColor("#9A9A9A")
            setColorFilter(iconColor)
            layoutParams = LinearLayout.LayoutParams(
                (14 * dp).toInt(), (14 * dp).toInt()
            ).also { it.marginEnd = (4 * dp).toInt() }
        })
        timeRow.addView(TextView(ctx).apply {
            text     = o.timeAgo
            textSize = 12f
            setTypeface(typeface, android.graphics.Typeface.BOLD)
            setTextColor(
                if (o.isUrgent) Color.parseColor("#BE1028")
                else Color.parseColor("#9A9A9A")
            )
        })
        timeCol.addView(timeRow)

        if (o.isUrgent) {
            timeCol.addView(TextView(ctx).apply {
                text     = "URGENT"
                textSize = 10f
                setTypeface(typeface, android.graphics.Typeface.BOLD)
                setTextColor(Color.parseColor("#BE1028"))
                setBackgroundResource(R.drawable.bg_badge_urgent)
                gravity = Gravity.CENTER
                val hP = (8 * dp).toInt(); val vP = (2 * dp).toInt()
                setPadding(hP, vP, hP, vP)
                layoutParams = LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.WRAP_CONTENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
                ).also { it.topMargin = (4 * dp).toInt() }
            })
        }

        headerRow.addView(timeCol)
        content.addView(headerRow)

        // Customer name
        content.addView(TextView(ctx).apply {
            text     = o.customerName
            textSize = 13f
            setTextColor(Color.parseColor("#6A6A6A"))
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            ).also { it.topMargin = (2 * dp).toInt() }
        })

        // Thin divider
        content.addView(View(ctx).apply {
            setBackgroundColor(Color.parseColor("#F2F2F2"))
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, (1 * dp).toInt()
            ).also { it.topMargin = (10 * dp).toInt(); it.bottomMargin = (8 * dp).toInt() }
        })

        // Order items (name + optional note on same row)
        o.items.forEach { item ->
            val row = LinearLayout(ctx).apply {
                orientation = LinearLayout.HORIZONTAL
                gravity = Gravity.CENTER_VERTICAL
                layoutParams = LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
                ).also { it.bottomMargin = (3 * dp).toInt() }
            }
            row.addView(TextView(ctx).apply {
                text     = item.name
                textSize = 13f
                setTextColor(Color.parseColor("#2A2A2A"))
                layoutParams = LinearLayout.LayoutParams(
                    0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f
                )
            })
            if (!item.note.isNullOrBlank()) {
                row.addView(TextView(ctx).apply {
                    text     = item.note
                    textSize = 12f
                    setTypeface(typeface, android.graphics.Typeface.BOLD)
                    setTextColor(Color.parseColor("#BE1028"))
                })
            }
            content.addView(row)
        }

        // Order type badge (Pickup = blue, Delivery = teal)
        content.addView(TextView(ctx).apply {
            text     = if (o.type == OrderType.PICKUP) "🛍  Pickup" else "🛵  Delivery"
            textSize = 11f
            setTypeface(typeface, android.graphics.Typeface.BOLD)
            setTextColor(Color.WHITE)
            setBackgroundResource(
                if (o.type == OrderType.PICKUP) R.drawable.bg_badge_pickup
                else R.drawable.bg_badge_delivery
            )
            val hP = (12 * dp).toInt(); val vP = (5 * dp).toInt()
            setPadding(hP, vP, hP, vP)
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            ).also { it.topMargin = (10 * dp).toInt() }
        })

        // START PREPARING button
        content.addView(Button(ctx).apply {
            text     = "START PREPARING"
            textSize = 13f
            setTypeface(typeface, android.graphics.Typeface.BOLD)
            setTextColor(Color.WHITE)
            isAllCaps = false
            setBackgroundResource(R.drawable.bg_btn_red_solid)
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, (48 * dp).toInt()
            ).also { it.topMargin = (12 * dp).toInt() }
            setOnClickListener { onStartPreparing(o) }
        })

        root.addView(content)
        h.card.addView(root)
    }

    fun updateOrders(newList: List<Order>) {
        orders = newList
        notifyDataSetChanged()
    }
}

// ─── Fragment ─────────────────────────────────────────────────────────────────

class StaffDashboard : Fragment(R.layout.fragment_staff_dashboard) {

    private lateinit var adapter: OrderCardAdapter
    private var activeStatus = OrderStatus.NEW

    // ── Dummy data — one list, covers all four tabs ──
    private val allOrders = listOf(

        // ── NEW (8) ──
        Order(
            id = "1234", customerName = "Jane D.", timeAgo = "5m ago", isUrgent = true,
            items = listOf(OrderItem("2x Classic Burger", "No Onions"), OrderItem("1x Large Fries")),
            type = OrderType.PICKUP, status = OrderStatus.NEW
        ),
        Order(
            id = "1235", customerName = "Michael R.", timeAgo = "12m ago",
            items = listOf(OrderItem("1x Veggie Bowl", "Extra Avocado"), OrderItem("1x Iced Tea")),
            type = OrderType.DELIVERY, status = OrderStatus.NEW
        ),
        Order(
            id = "1236", customerName = "Sarah K.", timeAgo = "2m ago",
            items = listOf(OrderItem("4x Margherita Pizza"), OrderItem("1x Family Salad")),
            type = OrderType.PICKUP, status = OrderStatus.NEW
        ),
        Order(
            id = "1237", customerName = "David L.", timeAgo = "18m ago",
            items = listOf(OrderItem("1x Chicken Sandwich"), OrderItem("1x Iced Coffee")),
            type = OrderType.DELIVERY, status = OrderStatus.NEW
        ),
        Order(
            id = "1238", customerName = "Carla M.", timeAgo = "7m ago", isUrgent = true,
            items = listOf(OrderItem("3x Pepperoni Pizza", "Extra Cheese"), OrderItem("2x Garlic Bread")),
            type = OrderType.PICKUP, status = OrderStatus.NEW
        ),
        Order(
            id = "1239", customerName = "Rico T.", timeAgo = "9m ago",
            items = listOf(OrderItem("1x BBQ Ribs Platter"), OrderItem("1x Coleslaw")),
            type = OrderType.DELIVERY, status = OrderStatus.NEW
        ),
        Order(
            id = "1240", customerName = "Mia F.", timeAgo = "3m ago",
            items = listOf(OrderItem("2x Hawaiian Pizza"), OrderItem("1x Sprite")),
            type = OrderType.PICKUP, status = OrderStatus.NEW
        ),
        Order(
            id = "1241", customerName = "Ben A.", timeAgo = "15m ago",
            items = listOf(OrderItem("1x Pasta Carbonara", "No Bacon"), OrderItem("1x Garlic Knots")),
            type = OrderType.DELIVERY, status = OrderStatus.NEW
        ),

        // ── PREPARING (4) ──
        Order(
            id = "1230", customerName = "Ana S.", timeAgo = "8m ago",
            items = listOf(OrderItem("2x Pepperoni Pizza"), OrderItem("1x Coke")),
            type = OrderType.PICKUP, status = OrderStatus.PREPARING
        ),
        Order(
            id = "1231", customerName = "Ken T.", timeAgo = "14m ago",
            items = listOf(OrderItem("1x BBQ Chicken Wings", "Extra Sauce"), OrderItem("1x Fries")),
            type = OrderType.DELIVERY, status = OrderStatus.PREPARING
        ),
        Order(
            id = "1232", customerName = "Paula G.", timeAgo = "11m ago",
            items = listOf(OrderItem("2x Cheese Burger"), OrderItem("2x Onion Rings")),
            type = OrderType.PICKUP, status = OrderStatus.PREPARING
        ),
        Order(
            id = "1233", customerName = "James O.", timeAgo = "6m ago",
            items = listOf(OrderItem("1x Veggie Wrap"), OrderItem("1x Orange Juice")),
            type = OrderType.DELIVERY, status = OrderStatus.PREPARING
        ),

        // ── READY (3) ──
        Order(
            id = "1228", customerName = "Lisa M.", timeAgo = "20m ago",
            items = listOf(OrderItem("1x Hawaiian Pizza"), OrderItem("1x Caesar Salad")),
            type = OrderType.PICKUP, status = OrderStatus.READY
        ),
        Order(
            id = "1229", customerName = "Tom G.", timeAgo = "25m ago",
            items = listOf(OrderItem("2x Fish & Chips"), OrderItem("2x Lemonade")),
            type = OrderType.DELIVERY, status = OrderStatus.READY
        ),
        Order(
            id = "1226", customerName = "Nina R.", timeAgo = "30m ago",
            items = listOf(OrderItem("3x Meat Lover Pizza")),
            type = OrderType.PICKUP, status = OrderStatus.READY
        ),

        // ── DONE (12) ──
        Order(
            id = "1220", customerName = "Chris B.", timeAgo = "35m ago",
            items = listOf(OrderItem("1x Margherita Pizza"), OrderItem("1x Tiramisu")),
            type = OrderType.PICKUP, status = OrderStatus.DONE
        ),
        Order(
            id = "1221", customerName = "Sofia P.", timeAgo = "40m ago",
            items = listOf(OrderItem("2x Chicken Wrap"), OrderItem("2x Water")),
            type = OrderType.DELIVERY, status = OrderStatus.DONE
        ),
        Order(
            id = "1222", customerName = "Liam C.", timeAgo = "42m ago",
            items = listOf(OrderItem("1x BBQ Platter"), OrderItem("1x Coleslaw")),
            type = OrderType.PICKUP, status = OrderStatus.DONE
        ),
        Order(
            id = "1223", customerName = "Emma W.", timeAgo = "50m ago",
            items = listOf(OrderItem("4x Garlic Knots"), OrderItem("2x Pepsi")),
            type = OrderType.DELIVERY, status = OrderStatus.DONE
        ),
        Order(
            id = "1224", customerName = "Noah K.", timeAgo = "55m ago",
            items = listOf(OrderItem("1x Pasta Bolognese"), OrderItem("1x Garlic Bread")),
            type = OrderType.PICKUP, status = OrderStatus.DONE
        ),
        Order(
            id = "1215", customerName = "Ava J.", timeAgo = "1h ago",
            items = listOf(OrderItem("2x Cheese Pizza", "Thin Crust"), OrderItem("1x Iced Tea")),
            type = OrderType.DELIVERY, status = OrderStatus.DONE
        ),
        Order(
            id = "1216", customerName = "Oliver D.", timeAgo = "1h ago",
            items = listOf(OrderItem("3x Classic Burger"), OrderItem("3x Large Fries")),
            type = OrderType.PICKUP, status = OrderStatus.DONE
        ),
        Order(
            id = "1217", customerName = "Isabella F.", timeAgo = "1h 10m ago",
            items = listOf(OrderItem("1x Veggie Bowl"), OrderItem("1x Smoothie")),
            type = OrderType.DELIVERY, status = OrderStatus.DONE
        ),
        Order(
            id = "1218", customerName = "Ethan S.", timeAgo = "1h 15m ago",
            items = listOf(OrderItem("2x Spicy Chicken Sandwich", "Extra Mayo")),
            type = OrderType.PICKUP, status = OrderStatus.DONE
        ),
        Order(
            id = "1210", customerName = "Mia L.", timeAgo = "1h 20m ago",
            items = listOf(OrderItem("1x Hawaiian Pizza"), OrderItem("1x Garlic Bread")),
            type = OrderType.DELIVERY, status = OrderStatus.DONE
        ),
        Order(
            id = "1211", customerName = "Lucas N.", timeAgo = "1h 30m ago",
            items = listOf(OrderItem("2x Pepperoni Pizza")),
            type = OrderType.PICKUP, status = OrderStatus.DONE
        ),
        Order(
            id = "1212", customerName = "Amelia H.", timeAgo = "1h 35m ago",
            items = listOf(OrderItem("1x Fish Tacos", "No Sour Cream"), OrderItem("1x Lemonade")),
            type = OrderType.DELIVERY, status = OrderStatus.DONE
        )
    )

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Set up RecyclerView with our OrderCardAdapter
        val rv = view.findViewById<RecyclerView>(R.id.rvOrders)
        rv.layoutManager = LinearLayoutManager(requireContext())
        adapter = OrderCardAdapter(filteredOrders()) { order ->
            Toast.makeText(requireContext(), "Preparing order #${order.id}", Toast.LENGTH_SHORT).show()
        }
        rv.adapter = adapter

        // Tabs
        val chipAll       = view.findViewById<TextView>(R.id.chipCatAll)
        val chipIncoming  = view.findViewById<TextView>(R.id.chipCatIncoming)
        val chipPreparing = view.findViewById<TextView>(R.id.chipCatPreparing)
        val chipRider     = view.findViewById<TextView>(R.id.chipCatRider)

        val tabs = listOf(
            chipAll       to OrderStatus.NEW,
            chipIncoming  to OrderStatus.PREPARING,
            chipPreparing to OrderStatus.READY,
            chipRider     to OrderStatus.DONE
        )

        tabs.forEach { (chip, status) ->
            chip.setOnClickListener {
                activeStatus = status
                tabs.forEach { (c, _) -> applyTabStyle(c, false) }
                applyTabStyle(chip, true)
                adapter.updateOrders(filteredOrders())
            }
        }

        // Select "New" tab on load
        applyTabStyle(chipAll, true)

        // FAB
        view.findViewById<FloatingActionButton>(R.id.fabFilter).setOnClickListener {
            Toast.makeText(requireContext(), "Filter options", Toast.LENGTH_SHORT).show()
        }

        StaffBottomNavHelper.bind(this, view, R.id.nav_staff_orders)
    }

    private fun filteredOrders() = allOrders.filter { it.status == activeStatus }

    private fun applyTabStyle(chip: TextView, selected: Boolean) {
        if (selected) {
            chip.setBackgroundResource(R.drawable.bg_staff_active_tab)
            chip.setTextColor(Color.parseColor("#BE1028"))
        } else {
            chip.setBackgroundResource(R.drawable.bg_staff_tab_container)
            chip.setTextColor(Color.parseColor("#5E5E5E"))
        }
    }
}