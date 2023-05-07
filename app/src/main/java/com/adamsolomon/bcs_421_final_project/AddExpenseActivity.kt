package com.adamsolomon.bcs_421_final_project

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.app.DatePickerDialog
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.icu.text.DecimalFormat
import android.icu.text.NumberFormat
import android.location.Address
import android.location.Geocoder
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.os.Bundle
import android.text.Editable
import android.text.InputType
import android.text.TextWatcher
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.preference.PreferenceManager
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.firebase.firestore.FirebaseFirestore
import java.text.SimpleDateFormat
import java.util.*


class AddExpenseActivity : AppCompatActivity(),SharedPreferences.OnSharedPreferenceChangeListener  {
    private lateinit var db : FirebaseFirestore
    private  lateinit var  expenseNameData: EditText
    private  lateinit var expenseLocationData: EditText
    private  lateinit var  businessNameData: EditText
    private  lateinit var  expenseCostData: EditText
    private  lateinit var expenseCategoryData: EditText
    private  lateinit var  dateData: EditText
    private  lateinit var descriptionData: EditText
    private  lateinit var additionalNotesData: EditText
    private  lateinit var datePicker: DatePickerDialog
    var locationManager:LocationManager?=null
    var locationListener:LocationListener?=null
    private lateinit var addExpenselayout: ConstraintLayout





    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        setAppTheme()
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_expense)

        val sharedPref = getSharedPreferences("myPreference", Context.MODE_PRIVATE)
        sharedPref.registerOnSharedPreferenceChangeListener(this)
        val bgColor = sharedPref.getInt("bgColor", resources.getColor(R.color.default_background_color, null))

        addExpenselayout = findViewById(R.id.add_expense_layout)
        // Set the background color of the activity
        addExpenselayout.setBackgroundColor(bgColor)
        db = FirebaseFirestore.getInstance()


        expenseNameData=findViewById(R.id.expenseNameText)
       expenseLocationData=findViewById(R.id.expenseLoactionText)
        expenseCostData=findViewById(R.id.expenseCost)
        businessNameData=findViewById(R.id.businessNameText)
        expenseCategoryData=findViewById(R.id.expenseCategoryText)
        dateData = findViewById(R.id.dateText);
        descriptionData=findViewById(R.id.descriptionText)
       additionalNotesData=findViewById(R.id.additionalNotesText)

        var hasButtonBeenClicked: Boolean=false
        var  addExpenseButton=findViewById<Button>(R.id.addExpenseButton)
        val calendar = Calendar.getInstance()
        val day = calendar[Calendar.DAY_OF_MONTH]
        val year = calendar[Calendar.YEAR]
        val month = calendar[Calendar.MONTH]






        val currencyFormat = NumberFormat.getCurrencyInstance()

        val watcher = object : TextWatcher {
            private var current = ""

            override fun afterTextChanged(s: Editable?) {}

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                if (s.toString() != current) {
                    expenseCostData.removeTextChangedListener(this)

                    val cleanString = s.toString().replace("\\D+".toRegex(), "")
                    val parsed = cleanString.toDouble()
                    val formatted = currencyFormat.format(parsed / 100)

                    current = formatted
                    expenseCostData.setText(formatted)
                    expenseCostData.setSelection(formatted.length)

                    expenseCostData.addTextChangedListener(this)
                }
            }
        }

        expenseCostData.addTextChangedListener(watcher)


        dateData.inputType = InputType.TYPE_NULL



        expenseLocationData.setOnFocusChangeListener { v, hasFocus ->
            if (hasFocus) {
                val builder = AlertDialog.Builder(this)
                builder.setTitle( getString(R.string.ExpenselocationAlertText),)
                builder.setMessage(getString(R.string.ExpenselocationAlertTitle))

                builder.setPositiveButton(android.R.string.yes) { dialog, which ->
                    fetchLocation()
                }

                builder.setNegativeButton(android.R.string.no) { dialog, which ->

                }

                builder.show()

            }
        }

        dateData.setOnFocusChangeListener { v, hasFocus ->
            if (hasFocus) {
                val datePickerDialog = DatePickerDialog(
                    this@AddExpenseActivity,
                    { view, year, month, dayOfMonth ->
                        val formattedMonth = String.format("%02d", month + 1)
                        val formattedDayOfMonth = String.format("%02d", dayOfMonth)
                        dateData.setText("$formattedMonth/$formattedDayOfMonth/$year")
                    }, year, month, day)
                datePickerDialog.show()
            }
        }



        addExpenseButton.setOnClickListener {

            var nameData: String =  expenseNameData.text.toString()
            val priceFormatter = NumberFormat.getCurrencyInstance()
            var expenseCostTextData = expenseCostData.text.toString()
            var expenseCostNumberData = priceFormatter.parse(expenseCostTextData)?.toDouble() ?: 0.0
            var formattedCurrency = priceFormatter.format(expenseCostNumberData)
            var locationData = expenseLocationData.text.toString()
            var businessNameTextData =  businessNameData.text.toString()
            var expenseCategoryTextData = expenseCategoryData.text.toString()
            val formatter = SimpleDateFormat("MM/dd/yyyy")
            var dateTextData =  formatter.parse(dateData.text.toString());
            var descriptionTextData =  descriptionData.text.toString()
            var notesData =  additionalNotesData.text.toString()
             hasButtonBeenClicked=true

            if (nameData.isEmpty() || locationData.isEmpty()|| expenseCostTextData.isEmpty() || businessNameTextData.isEmpty()  ||  expenseCategoryTextData.isEmpty() || descriptionTextData.isEmpty() || notesData.isEmpty() ) {
                Toast.makeText(
                    this@AddExpenseActivity,
                    getString(R.string.seconderrorMessage),
                    Toast.LENGTH_SHORT

                )
                    .show()

                 nameData =  expenseNameData.text.toString()
                expenseCostTextData = expenseCostData.text.toString()
                expenseCostNumberData = priceFormatter.parse(expenseCostTextData)?.toDouble() ?: 0.0
                formattedCurrency = priceFormatter.format(expenseCostNumberData)
                locationData = expenseLocationData.text.toString()
                businessNameTextData =  businessNameData.text.toString()
                expenseCategoryTextData= expenseCategoryData.text.toString()
                 val formatter = SimpleDateFormat("MM/dd/yyyy")
                 dateTextData =  formatter.parse(dateData.text.toString());
                 descriptionTextData =  descriptionData.text.toString()
                 notesData =  additionalNotesData.text.toString()
            }

            else{
            val data = hashMapOf(
                "expense name" to "${nameData}",
                "expense cost" to   "${formattedCurrency}" ,
                "location" to "${locationData}",
                "business name" to "${businessNameTextData}",
                "category name" to "${expenseCategoryTextData}",
                "date" to dateTextData,
                "description" to "${descriptionTextData}",
                "notes" to "${notesData}"
            )
            db.collection("Hwk4FinalExpenses")
                .add(data)
                .addOnSuccessListener { documentReference ->
                    Log.d("MYDEBUG", "DocumentSnapshot added with ID:${documentReference.id}")

                    finish()
                }
                .addOnFailureListener { e ->
                    Log.w("MYDEBUG", "Error adding document", e)
                }
            val resultIntent = Intent()
            resultIntent.putExtra("myData", hasButtonBeenClicked)
            setResult(RESULT_OK, resultIntent)
           finish()
        }



        }

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
            addExpenselayout.setBackgroundColor(bgColor ?: resources.getColor(R.color.default_background_color, null))
        }
    }

    override fun onResume() {
        super.onResume()

        // Retrieve the current background color from shared preferences
        val sharedPref = getSharedPreferences("myPreference", Context.MODE_PRIVATE)
        val bgColor = sharedPref.getInt("bgColor", resources.getColor(R.color.default_background_color, null))

        addExpenselayout.setBackgroundColor(bgColor)
    }

    override fun onDestroy() {
        super.onDestroy()

        // Unregister this activity as a listener for shared preferences changes
        val sharedPref = getSharedPreferences("myPreference", Context.MODE_PRIVATE)
        sharedPref.unregisterOnSharedPreferenceChangeListener(this)
    }



    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if(grantResults.size>0 && grantResults[0] ==PackageManager.PERMISSION_GRANTED ){

            if(ContextCompat.checkSelfPermission(this,android.Manifest.permission.ACCESS_FINE_LOCATION)==
                PackageManager.PERMISSION_GRANTED){
                locationManager?.requestLocationUpdates(LocationManager.GPS_PROVIDER,0,0f,
                    locationListener!!)
            }
        }
    }



    private fun fetchLocation() {

        locationManager=this.getSystemService(LOCATION_SERVICE) as LocationManager
        locationListener= LocationListener {
                location ->
            Log.d("location",location.toString())
            val addresses: List<Address>
            val geocoder = Geocoder(this, Locale.getDefault())

            addresses = geocoder.getFromLocation(location.latitude, location.longitude, 1) as List<Address>
            var currentAddress:String=addresses.get(0).getAddressLine(0)
            expenseLocationData.setText(currentAddress)
            //Toast.makeText(applicationContext, "${addresses.get(0).getAddressLine(0)}" ,Toast.LENGTH_SHORT).show()


        }
        if(ContextCompat.checkSelfPermission(this,android.Manifest.permission.ACCESS_FINE_LOCATION)!=PackageManager.PERMISSION_GRANTED)
        {
            ActivityCompat.requestPermissions(this, arrayOf(android.Manifest.permission.ACCESS_FINE_LOCATION),1)
        }
        else{
            locationManager!!.requestLocationUpdates(LocationManager.GPS_PROVIDER,0,10f,
                locationListener!!)
        }


    }


}