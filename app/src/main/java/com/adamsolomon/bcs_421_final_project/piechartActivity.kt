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
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.core.net.ParseException
import androidx.preference.PreferenceManager
import com.github.mikephil.charting.charts.PieChart
import com.github.mikephil.charting.data.*
import com.github.mikephil.charting.formatter.PercentFormatter
import com.github.mikephil.charting.highlight.Highlight
import com.github.mikephil.charting.listener.OnChartValueSelectedListener
import com.google.firebase.firestore.FirebaseFirestore
import java.time.ZoneId
import java.util.*
import kotlin.collections.ArrayList

class piechartActivity : AppCompatActivity(),SharedPreferences.OnSharedPreferenceChangeListener  {
    private  lateinit var selectedExpenseData: TextView
    private lateinit var pieChartlayout: ConstraintLayout
    @SuppressLint("SuspiciousIndentation", "MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        setAppTheme()
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_piechart)
        selectedExpenseData=findViewById(R.id.selectedExpense)
        val pieChart: PieChart = findViewById(R.id.pie_chart)
        pieChart.setUsePercentValues(true)
        pieChart.description.isEnabled = false
        pieChart.legend.isEnabled = false
        pieChart.setDrawEntryLabels(true)
        pieChart.holeRadius = 0f
        pieChart.transparentCircleRadius = 0f
        var hasActivityOpened: Boolean=false


// Set up pie chart data
        val sharedPref = getSharedPreferences("myPreference", Context.MODE_PRIVATE)
        sharedPref.registerOnSharedPreferenceChangeListener(this)
        val bgColor = sharedPref.getInt("bgColor", resources.getColor(R.color.default_background_color, null))

        pieChartlayout = findViewById(R.id.pie_chart_Layout)
        // Set the background color of the activity
        pieChartlayout.setBackgroundColor(bgColor)
        val db = FirebaseFirestore.getInstance()
        val expensesRef = db.collection("Hwk4FinalExpenses")



                expensesRef.get()
            .addOnSuccessListener { documents ->
                // Parse and convert data to list of PieEntry objects
                val entries = ArrayList<PieEntry>()
                for (doc in documents) {
                    val expense = doc.getString("expense name")
                    val amount = doc.getString("expense cost")?.let { parse(it, Locale.US) }?.toFloat()// assume this is a Float value
                    entries.add(PieEntry(amount as Float, expense))
                }


                // Create PieDataSet and set colors
                val dataSet = PieDataSet(entries, "Expenses")

                dataSet.colors = listOf(
                    Color.BLUE, Color.GREEN, Color.RED,
                    Color.YELLOW, Color.CYAN, Color.MAGENTA,Color.LTGRAY
                )
                dataSet.valueFormatter = PercentFormatter(pieChart)

                // Create PieData and set to PieChart
                val data = PieData(dataSet)
                pieChart.data = data
                pieChart.setEntryLabelColor(Color.BLACK);
                pieChart.setOnChartValueSelectedListener(object : OnChartValueSelectedListener {
                    override fun onValueSelected(e: Entry?, h: Highlight?) {
                        val selectedEntry = e as PieEntry
                        val percent = String.format("%.2f", selectedEntry.value / data.yValueSum * 100)
                        selectedExpenseData.text = "${selectedEntry.label}: $percent%"
                    }

                    override fun onNothingSelected() {}
                })

                pieChart.invalidate()
                hasActivityOpened=true
            }



// Set up pie chart layout and display data
        val resultIntent = Intent()
        resultIntent.putExtra("myData", hasActivityOpened)
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
            //  recreate() // Recreate the activity to apply the new theme
        }
        if (key == "bgColor") {
            // Retrieve the new background color from shared preferences and update the activity's background
            val bgColor = sharedPreferences?.getInt(key, resources.getColor(R.color.default_background_color, null))
            pieChartlayout.setBackgroundColor(bgColor ?: resources.getColor(R.color.default_background_color, null))
        }
    }
}



