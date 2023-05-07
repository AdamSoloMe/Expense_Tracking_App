package com.adamsolomon.bcs_421_final_project

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.preference.PreferenceManager
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class MainActivity : AppCompatActivity(),SharedPreferences.OnSharedPreferenceChangeListener {
    private lateinit var db: FirebaseFirestore
    private lateinit var mAuth: FirebaseAuth
    private lateinit var LoginData: EditText
    private lateinit var Password: EditText
    private lateinit var mainChartlayout: ConstraintLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        setAppTheme()
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val sharedPref = getSharedPreferences("myPreference", Context.MODE_PRIVATE)
        sharedPref.registerOnSharedPreferenceChangeListener(this)
        val bgColor =
            sharedPref.getInt("bgColor", resources.getColor(R.color.default_background_color, null))

        mainChartlayout = findViewById(R.id.main_Layout)
        // Set the background color of the activity
        mainChartlayout.setBackgroundColor(bgColor)

        var loginButton = findViewById(R.id.LoginButton) as Button

        db = FirebaseFirestore.getInstance()
        mAuth = FirebaseAuth.getInstance()

        LoginData = findViewById(R.id.LoginData)
        Password = findViewById(R.id.Password)

        val mCurrentUser = mAuth.currentUser
        var email = LoginData.text
        var password = Password.text

        loginButton.setOnClickListener {

            try {


                if (email.isEmpty() || password.isEmpty()) {
                    Toast.makeText(
                        this@MainActivity,
                        getString(R.string.errorMessage),
                        Toast.LENGTH_SHORT

                    )
                        .show()
                    email = LoginData.text
                    password = Password.text

                } else {
                    mAuth.signInWithEmailAndPassword(email.toString(), password.toString())
                        .addOnCompleteListener(
                            this
                        ) { task ->
// Was the sign in successful?
                            if (task.isSuccessful) {

                                val intent = Intent(this, ExpenseListActivity::class.java)
                                startActivity(intent)

                            } else {
                                var loginErrorMessage = getString(R.string.Loginerror)
                                Toast.makeText(
                                    this@MainActivity,
                                    loginErrorMessage,
                                    Toast.LENGTH_LONG
                                )
                                    .show()
                                email.clear()
                                password.clear()
                            }
                            email.clear()
                            password.clear()
                        }

                }
            } catch (e: Exception) {
                e.message

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
    override fun onResume() {
        super.onResume()

        // Retrieve the current background color from shared preferences
        val sharedPref = getSharedPreferences("myPreference", Context.MODE_PRIVATE)
        val bgColor = sharedPref.getInt("bgColor", resources.getColor(R.color.default_background_color, null))

        mainChartlayout.setBackgroundColor(bgColor)
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
            mainChartlayout.setBackgroundColor(bgColor ?: resources.getColor(R.color.default_background_color, null))
        }

    }
}
