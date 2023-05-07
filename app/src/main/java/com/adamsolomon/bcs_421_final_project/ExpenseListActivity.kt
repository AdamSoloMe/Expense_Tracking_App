package com.adamsolomon.bcs_421_final_project

import android.annotation.SuppressLint
import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import androidx.activity.result.ActivityResult
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.preference.PreferenceManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.firestore.FirebaseFirestore

class ExpenseListActivity : AppCompatActivity(),SharedPreferences.OnSharedPreferenceChangeListener {
    private lateinit var recyclerViewNames : RecyclerView
    private lateinit var data : ArrayList<Expense>
    private lateinit var  expenseprimaryAdapter: ExpenseAdapter
    private lateinit var db : FirebaseFirestore
    lateinit var dataActivityLauncher: ActivityResultLauncher<Intent>
    private lateinit var homelayout: ConstraintLayout
    override fun onCreate(savedInstanceState: Bundle?) {
        setAppTheme()
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_expense_list)

        val sharedPref = getSharedPreferences("myPreference", Context.MODE_PRIVATE)
        sharedPref.registerOnSharedPreferenceChangeListener(this)
        val bgColor = sharedPref.getInt("bgColor", resources.getColor(R.color.default_background_color, null))

        homelayout = findViewById(R.id.layout_home)
        // Set the background color of the activity
        homelayout .setBackgroundColor(bgColor)

        recyclerViewNames = findViewById<RecyclerView>(R.id.recyclerViewExpenses)
        recyclerViewNames.layoutManager = LinearLayoutManager(this)
        db = FirebaseFirestore.getInstance()
        data = ArrayList<Expense>()
        db.collection("Hwk4FinalExpenses")
            .get()
            .addOnSuccessListener { documents ->
                for (document in documents) {
                    Log.d(ContentValues.TAG, "${document.id} => ${document.data}")
                    var expenseName = document.getString("expense name")
                    var expenseCost = document.getString("expense cost").toString()
                    var expenseLocation = document.getString("location")
                    var expenseBusinessName = document.getString("business name")
                    var expenseCategory = document.getString("expense name")
                    var expenseDate = document.getDate("date").toString()
                    var expenseDesc = document.getString("description")
                    var expenseNotes = document.getString("notes")
                    data.add(
                        Expense(
                            expenseName,
                            expenseCost,
                            expenseLocation,
                            expenseBusinessName,
                            expenseCategory,
                            expenseDate,
                            expenseDesc,
                            expenseNotes
                        )
                    )

                    expenseprimaryAdapter= ExpenseAdapter(data)
                    recyclerViewNames.adapter = expenseprimaryAdapter
                }
            }
            //if it breaks remove this
            .addOnFailureListener { exception ->
                Log.w(ContentValues.TAG, "Error getting documents: ", exception)
            }
        dataActivityLauncher = registerForActivityResult(
            ActivityResultContracts.StartActivityForResult()
        )
        { result: ActivityResult ->
            if (result.resultCode == RESULT_OK) {
                val resultIntent: Intent? = result.data
                if (resultIntent != null) {
                    db.collection("Hwk4FinalExpenses")
                        .get()
                        .addOnSuccessListener { documents ->
                            for (document in documents) {
                                Log.d(ContentValues.TAG, "${document.id} => ${document.data}")
                                var expenseName = document.getString("expense name")
                                var expenseCost = document.getString("expense cost").toString()
                                var expenseLocation= document.getString("location")
                                var expenseBusinessName= document.getString("business name")
                                var expenseCategory= document.getString("expense name")
                                var expenseDate = document.getDate("date").toString()
                                var expenseDesc = document.getString("description")
                                var expenseNotes= document.getString("notes")

                                data.add(
                                    Expense(
                                        expenseName,
                                        expenseCost,
                                        expenseLocation,
                                        expenseBusinessName,
                                        expenseCategory,
                                        expenseDate,
                                        expenseDesc,
                                        expenseNotes
                                    )
                                )
                                //recyclerViewNames.post { expenseprimaryAdapter.notifyDataSetChanged() }

                                expenseprimaryAdapter = ExpenseAdapter(data)
                                recyclerViewNames.adapter =  expenseprimaryAdapter
                                expenseprimaryAdapter.notifyDataSetChanged()

                            }
                        }
                        //if it breaks remove this
                        .addOnFailureListener { exception ->
                            Log.w(ContentValues.TAG, "Error getting documents: ", exception)
                        }



                }
            }
        }
    }



    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        val inflater = menuInflater
        inflater.inflate(R.menu.expenseappmenu, menu)


        return true
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
            val bgColor = sharedPreferences?.getInt(key, resources.getColor(R.color.default_background_color, null))
            homelayout.setBackgroundColor(bgColor ?: resources.getColor(R.color.default_background_color, null))
        }
        }


    override fun onResume() {
        super.onResume()

        // Retrieve the current background color from shared preferences
        val sharedPref = getSharedPreferences("myPreference", Context.MODE_PRIVATE)
        val bgColor = sharedPref.getInt("bgColor", resources.getColor(R.color.default_background_color, null))

        homelayout.setBackgroundColor(bgColor)
    }

    override fun onDestroy() {
        super.onDestroy()

        // Unregister this activity as a listener for shared preferences changes
        val sharedPref = getSharedPreferences("myPreference", Context.MODE_PRIVATE)
        sharedPref.unregisterOnSharedPreferenceChangeListener(this)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.getItemId()) {
            R.id.add_expense_menuitem -> {
                data.clear()
                val intent = Intent(this,AddExpenseActivity::class.java)
                dataActivityLauncher.launch(intent)

                true
            }
            R.id.bar_chart_view_menuitem -> {
                  data.clear()
                val intent = Intent(this,BarChartActivity::class.java)
                dataActivityLauncher.launch(intent)

                true
            }
            R.id.pie_chart_view_menuitem -> {
                data.clear()
                //   resturantprimaryAdapter = ResturantAdapter(data)
                // recyclerViewNames.adapter = resturantprimaryAdapter
                val intent = Intent(this,piechartActivity::class.java)
                dataActivityLauncher.launch(intent)
                true
            }
            R.id.settings_menu_item -> {
                data.clear()
                val intent = Intent(this,UserPreferenceActivityPrime::class.java)
                dataActivityLauncher.launch(intent)
                true
            }

            else -> super.onOptionsItemSelected(item)
        }

        }

    }
