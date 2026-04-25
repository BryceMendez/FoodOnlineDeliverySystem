package com.example.myfirstapp

import android.graphics.Color
import android.graphics.Typeface
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

// ── Models ────────────────────────────────────────────────────────────────────
enum class OrderType { PICKUP, DELIVERY }
enum class OrderStatus { NEW, PREPARING, READY, DONE }
data class OrderItem(val name: String, val note: String? = null)
data class Order(
    val id: String,
    val customer: String,
    val time: String,
    val urgent: Boolean,
    val items: List<OrderItem>,
    val type: OrderType,
    val status: OrderStatus
)

// ── Adapter ───────────────────────────────────────────────────────────────────
class OrderCardAdapter(
    private var orders: List<Order>,
    private val onAction: (Order) -> Unit
) : RecyclerView.Adapter<OrderCardAdapter.OrderVH>() {

    class OrderVH(v: View) : RecyclerView.ViewHolder(v) {
        val id            = v.findViewById<TextView>(R.id.tvOrderId)
        val customer      = v.findViewById<TextView>(R.id.tvCustomerName)
        val time          = v.findViewById<TextView>(R.id.tvTimeAgo)
        val urgentBadge   = v.findViewById<TextView>(R.id.tvUrgentBadge)
        val typeBadge     = v.findViewById<TextView>(R.id.tvTypeBadge)
        val accent        = v.findViewById<View>(R.id.viewAccent)
        val itemsContainer = v.findViewById<LinearLayout>(R.id.llItemsContainer)
        val btn           = v.findViewById<android.widget.Button>(R.id.btnAction)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
        OrderVH(
            LayoutInflater.from(parent.context)
                .inflate(R.layout.activity_item_order_card, parent, false)
        )

    override fun onBindViewHolder(holder: OrderVH, position: Int) {
        val o = orders[position]
        holder.id.text       = "#${o.id}"
        holder.customer.text = o.customer
        holder.time.text     = o.time
        holder.urgentBadge.visibility = if (o.urgent) View.VISIBLE else View.GONE

        if (o.type == OrderType.DELIVERY) {
            holder.typeBadge.text = "Delivery"
            holder.typeBadge.setBackgroundColor(Color.parseColor("#00796B"))
            holder.accent.setBackgroundColor(Color.parseColor("#3A8DCC"))
        } else {
            holder.typeBadge.text = "Pickup"
            holder.typeBadge.setBackgroundColor(Color.parseColor("#4A90E2"))
            holder.accent.setBackgroundColor(Color.parseColor("#BE1028"))
        }

        holder.itemsContainer.removeAllViews()
        o.items.forEach { item ->
            val row = LinearLayout(holder.itemView.context).apply {
                orientation = LinearLayout.HORIZONTAL
                setPadding(0, 4, 0, 4)
            }
            row.addView(TextView(holder.itemView.context).apply {
                text = item.name
                layoutParams = LinearLayout.LayoutParams(0, -2, 1f)
                setTextColor(Color.parseColor("#1A1A1A"))
            })
            if (item.note != null) {
                row.addView(TextView(holder.itemView.context).apply {
                    text = item.note
                    setTextColor(Color.parseColor("#BE1028"))
                    setTypeface(null, Typeface.BOLD)
                })
            }
            holder.itemsContainer.addView(row)
        }

        // ── CHANGED: navigate to PreparingOrderFragment ───────────────────────
        holder.btn.text = "START PREPARING"
        holder.btn.setOnClickListener { onAction(o) }
    }

    override fun getItemCount() = orders.size

    fun update(newList: List<Order>) {
        orders = newList
        notifyDataSetChanged()
    }
}

// ── Fragment ──────────────────────────────────────────────────────────────────
class StaffDashboard : Fragment(R.layout.fragment_staff_dashboard) {

    private lateinit var adapter: OrderCardAdapter
    private var currentTab = OrderStatus.NEW

    private val allOrders = listOf(
        Order(
            id = "1234",
            customer = "Table 12 • Jane D.",
            time = "5m ago",
            urgent = true,
            items = listOf(
                OrderItem("2x Classic Burger", "+ Extra Cheese\n- No Onions"),
                OrderItem("1x Large Fries")
            ),
            type = OrderType.PICKUP,
            status = OrderStatus.NEW
        ),
        Order(
            id = "1235",
            customer = "Michael R.",
            time = "12m ago",
            urgent = false,
            items = listOf(
                OrderItem("1x Veggie Bowl", "+ Extra Avocado")
            ),
            type = OrderType.DELIVERY,
            status = OrderStatus.NEW
        )
    )

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val rv = view.findViewById<RecyclerView>(R.id.rvOrders)
        rv.layoutManager = LinearLayoutManager(requireContext())

        adapter = OrderCardAdapter(allOrders.filter { it.status == currentTab }) { order ->
            // Open PreparingOrderFragment, passing the full order
            parentFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, PreparingOrderFragment.newInstance(order))
                .addToBackStack(null)
                .commit()
        }
        rv.adapter = adapter

        view.findViewById<TextView>(R.id.chipNew).setOnClickListener {
            currentTab = OrderStatus.NEW
            adapter.update(allOrders.filter { it.status == currentTab })
        }
        view.findViewById<TextView>(R.id.chipPreparing).setOnClickListener {
            currentTab = OrderStatus.PREPARING
            adapter.update(allOrders.filter { it.status == currentTab })
        }
        view.findViewById<TextView>(R.id.chipReady).setOnClickListener {
            currentTab = OrderStatus.READY
            adapter.update(allOrders.filter { it.status == currentTab })
        }
    }
}
