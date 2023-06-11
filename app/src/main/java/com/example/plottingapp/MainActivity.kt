package com.example.plottingapp

import android.content.ContentValues
import android.content.Context
import android.os.Bundle
import android.util.TypedValue
import android.view.Gravity
import android.view.LayoutInflater
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import kotlin.math.cos


class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val functionSpinner = findViewById<Spinner>(R.id.functionSpinner)
        val startIntervalInput = findViewById<EditText>(R.id.start_interval_input)
        val endIntervalInput = findViewById<EditText>(R.id.end_interval_input)
        val stepInput = findViewById<EditText>(R.id.step_input)
        val plotButton = findViewById<Button>(R.id.plot_button)
        val infoButton: Button = findViewById(R.id.infoButton)


        val functions = arrayOf("y = x^2", "y = x^3", "y = cos(x)")
        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, functions)
        functionSpinner.adapter = adapter

        plotButton.setOnClickListener {
            val function = functionSpinner.selectedItem.toString()
            val start = startIntervalInput.text.toString().toInt()
            val end = endIntervalInput.text.toString().toInt()
            val step = stepInput.text.toString().toInt()

            if (step <= 0) {
                Toast.makeText(this, "Step must be positive and greater than 0", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val entries = ArrayList<Entry>()

            for (x in generateSequence(start) { it + if (end >= start) step else -step }) {
                val y = when (function) {
                    "y = x^2" -> x * x.toFloat()
                    "y = x^3" -> x * x.toFloat() * x.toFloat()
                    "y = cos(x)" -> cos(x.toFloat())
                    else -> 0f
                }

                entries.add(Entry(x.toFloat(), y))

                if ((end >= start && x + step > end) || (end < start && x - step < end)) break
            }

            val lineDataSet = LineDataSet(entries, "Plot")
            val lineData = LineData(lineDataSet)
            val chart = findViewById<LineChart>(R.id.chart)
            chart.data = lineData
            chart.invalidate()

            val valuesTable = findViewById<TableLayout>(R.id.valuesTable)
            valuesTable.removeAllViews()

            val xValuesRow = TableRow(this)
            val yValuesRow = TableRow(this)

            for (entry in entries) {
                val params = TableRow.LayoutParams().apply {
                    setMargins(2, 1, 2, 1)
                }


                val paddingInPixels = TypedValue.applyDimension(
                    TypedValue.COMPLEX_UNIT_DIP, 8f,
                    resources.displayMetrics
                ).toInt()

                val xTextView = TextView(this)
                xTextView.text = String.format("%.2f", entry.x)
                xTextView.gravity = Gravity.CENTER
                xTextView.setBackgroundResource(R.drawable.cell_border)
                xTextView.setPadding(paddingInPixels, paddingInPixels, paddingInPixels, paddingInPixels)
                xValuesRow.addView(xTextView, params)

                val yTextView = TextView(this)
                yTextView.text = String.format("%.2f", entry.y)
                yTextView.gravity = Gravity.CENTER
                yTextView.setBackgroundResource(R.drawable.cell_border)
                yTextView.setPadding(paddingInPixels, paddingInPixels, paddingInPixels, paddingInPixels)
                yValuesRow.addView(yTextView, params)

            }

            valuesTable.addView(xValuesRow)
            valuesTable.addView(yValuesRow)
        }

        infoButton.setOnClickListener {
            val inflater = getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
            val popupView = inflater.inflate(R.layout.info_popup, null)

            val popupWindow = PopupWindow(popupView, LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT, true)

            val dimAmount = 0.3f
            val container = window.decorView.rootView
            val originalAlpha = container.alpha
            container.alpha = dimAmount

            popupWindow.setOnDismissListener {
                container.alpha = originalAlpha
            }

            val closeButton: Button = popupView.findViewById(R.id.closeButton)
            closeButton.setOnClickListener {
                popupWindow.dismiss()
            }
            popupWindow.showAtLocation(it, Gravity.CENTER, 0, 0)
        }


    }
}
