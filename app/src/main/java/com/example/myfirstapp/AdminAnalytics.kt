package com.example.myfirstapp

import android.content.Context
import android.graphics.*
import android.os.Bundle
import android.util.AttributeSet
import android.view.*
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment

// ── CUSTOM VIEW 1: SalesChartView ──
class SalesChartView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null
) : View(context, attrs) {
    private val data = floatArrayOf(120f, 95f, 130f, 160f, 280f, 340f, 310f)
    private val linePaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = Color.parseColor("#BE1028")
        strokeWidth = 5f
        style = Paint.Style.STROKE
    }
    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        // Simple line drawing logic
        val w = width.toFloat()
        val h = height.toFloat()
        if (w == 0f || h == 0f) return
        val path = Path()
        path.moveTo(0f, h)
        for (i in data.indices) {
            val x = i * (w / (data.size - 1))
            val y = h - (data[i] / 400f * h)
            if (i == 0) path.moveTo(x, y) else path.lineTo(x, y)
        }
        canvas.drawPath(path, linePaint)
    }
}

// ── CUSTOM VIEW 2: HeatmapView ──
class HeatmapView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null
) : View(context, attrs) {
    private val cellPaint = Paint(Paint.ANTI_ALIAS_FLAG)
    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        val rows = 3
        val cols = 10
        val cellW = width.toFloat() / cols
        val cellH = height.toFloat() / rows
        for (r in 0 until rows) {
            for (c in 0 until cols) {
                cellPaint.color = if ((r + c) % 2 == 0) Color.parseColor("#FFCDD2") else Color.parseColor("#BE1028")
                canvas.drawRect(c * cellW, r * cellH, (c + 1) * cellW, (r + 1) * cellH, cellPaint)
            }
        }
    }
}

// ── THE FRAGMENT ──
class AdminAnalytics : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? = inflater.inflate(R.layout.fragment_admin_analytics, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // ── REQUIREMENT: RECEIVING DATA (Data Passing) ──
        // This receives the "order_id" sent from the AdminDashboard
        val passedOrderId = arguments?.getString("order_id")

        if (passedOrderId != null) {
            // Update a title or show a toast to prove data was passed
            view.findViewById<TextView>(R.id.tvTotalRevenue).text = "Order: $passedOrderId"
            Toast.makeText(requireContext(), "Data Received: $passedOrderId", Toast.LENGTH_SHORT).show()
        }

        view.findViewById<View>(R.id.btnDownloadReport).setOnClickListener {
            Toast.makeText(requireContext(), "Downloading Report...", Toast.LENGTH_SHORT).show()
        }
    }
}