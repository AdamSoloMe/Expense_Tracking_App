package com.adamsolomon.bcs_421_final_project

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.graphics.Color
import android.icu.math.BigDecimal
import android.icu.text.DecimalFormat
import android.icu.text.NumberFormat
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.net.ParseException
import androidx.preference.PreferenceManager
import com.github.mikephil.charting.charts.BarChart
import com.github.mikephil.charting.components.Legend
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.*
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter
import com.github.mikephil.charting.formatter.ValueFormatter
import com.github.mikephil.charting.highlight.Highlight
import com.github.mikephil.charting.interfaces.datasets.IBarDataSet
import com.github.mikephil.charting.listener.OnChartValueSelectedListener
import com.google.firebase.firestore.FirebaseFirestore
import java.time.ZoneId
import java.util.*
import kotlin.collections.ArrayList

class BarChartActivity : AppCompatActivity(),SharedPreferences.OnSharedPreferenceChangeListener {
    private lateinit var barChartdata: ArrayList<String>
    private lateinit var barChartlayout: ConstraintLayout

    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        setAppTheme()
        super.onCreate(savedInstanceState)
        setContentView(R.layout.bar_chart_activity_prime)
        val barchart: BarChart = findViewById(R.id.barchart)
        var hasButtonBeenClicked: Boolean = false

        val sharedPref = getSharedPreferences("myPreference", Context.MODE_PRIVATE)
        sharedPref.registerOnSharedPreferenceChangeListener(this)
        val bgColor =
            sharedPref.getInt("bgColor", resources.getColor(R.color.default_background_color, null))

        barChartlayout = findViewById(R.id.barChartLayout)
        // Set the background color of the activity
        barChartlayout.setBackgroundColor(bgColor)

        // Set up the chart
        barchart.description.isEnabled = false
        barchart.setPinchZoom(false)
        barchart.setDrawBarShadow(false)
        barchart.setDrawGridBackground(false)

        // Set up the left axis
        val leftAxis = barchart.axisLeft
        leftAxis.setDrawGridLines(false)
        leftAxis.setDrawAxisLine(false)
        leftAxis.setDrawLabels(true)
        leftAxis.axisMinimum = 0f

        // Set up the right axis
        val rightAxis = barchart.axisRight
        rightAxis.setDrawGridLines(false)
        rightAxis.setDrawAxisLine(false)
        rightAxis.setDrawLabels(false)
        rightAxis.axisMinimum = 0f

        // Set up the x-axis labels
        val xLabels = listOf(
            getString(R.string.Jan),
            getString(R.string.Feb),
            getString(R.string.Mar),
            getString(R.string.Apr),
            getString(R.string.May),
            getString(R.string.Jun),
            getString(R.string.Jul),
            getString(R.string.Aug),
            getString(R.string.Sept),
            getString(R.string.Oct),
            getString(R.string.Nov),
            getString(R.string.Dec)
        )
        val xAxis = barchart.xAxis
        xAxis.position = XAxis.XAxisPosition.BOTTOM
        xAxis.setDrawGridLines(false)
        xAxis.setDrawAxisLine(false)
        xAxis.labelCount = xLabels.size
        xAxis.valueFormatter = IndexAxisValueFormatter(xLabels)
        xAxis.granularity = 1f

        // Retrieve data from Firebase Firestore
        val db = FirebaseFirestore.getInstance()
        val expensesRef = db.collection("Hwk4FinalExpenses")
        expensesRef.get().addOnSuccessListener { documents ->

            // Parse and convert the data
            val monthTotals = mutableMapOf<Int, Float>()
            for (doc in documents) {
                val date = doc.getDate("date") ?: continue
                val amount = doc.getString("expense cost")?.let { parse(it, Locale.US) }?.toFloat()
                    ?: continue
                val month = date.toInstant().atZone(ZoneId.systemDefault()).monthValue
                monthTotals[month] = (monthTotals[month] ?: 0f) + amount
            }
            val entries = mutableListOf<BarEntry>()
            for (month in 1..12) {
                val total = monthTotals[month] ?: 0f
                entries.add(BarEntry(month.toFloat() - 1f, total))
            }
            // Set up the bar data
            val dataSet = BarDataSet(entries, getString(R.string.MonthlyTotals))
            dataSet.valueFormatter = object : ValueFormatter() {
                override fun getFormattedValue(value: Float): String {
                    return if (value == 0f) "" else String.format(Locale.US, "%.2f", value)
                }
            }
            dataSet.valueTextColor = Color.BLACK
            dataSet.valueTextSize = 12f

            dataSet.color = Color.BLUE
            barchart.data = BarData(dataSet)
            barchart.invalidate()
            hasButtonBeenClicked = true
        }
        val resultIntent = Intent()
        resultIntent.putExtra("myData", hasButtonBeenClicked)
        setResult(RESULT_OK, resultIntent)
    }


    @Throws(ParseException::class)
    fun parse(amount: String, locale: Locale): BigDecimal {
        val format = NumberFormat.getNumberInstance(locale)
        if (format is DecimalFormat) {
            format.isParseBigDecimal = true
        }
        return format.parse(amount.replace("[^\\d.,]".toRegex(), "")) as BigDecimal
    }

    private fun setAppTheme() {
        val sharedPrefs = PreferenceManager.getDefaultSharedPreferences(this)
        val themePref = sharedPrefs.getString("theme", "default")

        // Set the app theme based on the selected theme
        when (themePref) {
            "blue" -> setTheme(R.style.AppTheme_Blue)
            "green" -> setTheme(R.style.AppTheme_Green)
            "purple" -> setTheme(R.style.AppTheme_Purple)
            else -> setTheme(R.style.AppTheme_Default)
        }
    }

    override fun onSharedPreferenceChanged(sharedPreferences: SharedPreferences?, key: String?) {
        if (key == "theme") {
            // Update the app theme
            setAppTheme()
            recreate() // Recreate the activity to apply the new theme
        }
        if (key == "bgColor") {
            // Retrieve the new background color from shared preferences and update the activity's background
            val bgColor = sharedPreferences?.getInt(
                key,
                resources.getColor(R.color.default_background_color, null)
            )
            barChartlayout.setBackgroundColor(
                bgColor ?: resources.getColor(R.color.default_background_color, null)
            )
        }
    }
}




