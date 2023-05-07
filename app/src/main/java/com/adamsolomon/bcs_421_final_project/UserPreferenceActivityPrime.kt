package com.adamsolomon.bcs_421_final_project

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.preference.PreferenceManager
import yuku.ambilwarna.AmbilWarnaDialog
class UserPreferenceActivityPrime : AppCompatActivity() ,SharedPreferences.OnSharedPreferenceChangeListener {
    var hasActivityOpened: Boolean = false
    private lateinit var settingslayout: ConstraintLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        setAppTheme()

        super.onCreate(savedInstanceState)



        // Get the selected theme and set it


        setContentView(R.layout.activity_user_preference_prime)
        // Load the preferences from XML
        supportFragmentManager
            .beginTransaction()
            .replace(android.R.id.content, UserPreferenceFragment())
            .commit()
         val sharedPref = getSharedPreferences("myPreference", Context.MODE_PRIVATE)
        sharedPref.registerOnSharedPreferenceChangeListener(this)
        val bgColor = sharedPref.getInt("bgColor", resources.getColor(R.color.default_background_color, null))
        settingslayout = findViewById<ConstraintLayout>(R.id.settings_layout) as ConstraintLayout
        settingslayout.setBackgroundColor(bgColor)

        val resultIntent = Intent()
        resultIntent.putExtra("myData", hasActivityOpened)
        setResult(RESULT_OK, resultIntent)
        // setAppTheme()


    }

    // Show the color picker dialog when the preference is clicked
    fun showColorPickerDialog() {
        val preferences = PreferenceManager.getDefaultSharedPreferences(this)

        // Get the current color from preferences, default to white if not set
        var initialColor = preferences.getInt("color_preference", 0xFFFFFFFF.toInt())

        // Create the color picker dialog
        val colorPicker = AmbilWarnaDialog(
            this,
            initialColor,
            object : AmbilWarnaDialog.OnAmbilWarnaListener {
                override fun onCancel(dialog: AmbilWarnaDialog?) {}

                override fun onOk(dialog: AmbilWarnaDialog?, color: Int) {
                    // Save the selected color to preferences
                    preferences.edit().putInt("color_preference", color).apply()
                    hasActivityOpened = true
                    // Update the color preview in the preference fragment
                    val fragment =
                        supportFragmentManager.findFragmentById(android.R.id.content)
                    if (fragment is UserPreferenceFragment) {
                        fragment.updateColorPreview()

                    }
                }
            })
        // Show the color picker dialog
        colorPicker.show()
    }


    override fun onResume() {
        super.onResume()

        // Retrieve the current background color from shared preferences
        val sharedPref = getSharedPreferences("myPreference", Context.MODE_PRIVATE)
        val bgColor = sharedPref.getInt("bgColor", resources.getColor(R.color.default_background_color, null))

        settingslayout.setBackgroundColor(bgColor)
    }
    // Get the selected theme from preferences and set it
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




    // Called when a preference is changed
    override fun onSharedPreferenceChanged(sharedPreferences: SharedPreferences?, key: String?) {
        if (key == "theme") {
            // Update the app theme
            setAppTheme()
          //  recreate() // Recreate the activity to apply the new theme
        }
        if (key == "bgColor") {
       // Retrieve the new background color from shared preferences and update the activity's background
         val bgColor = sharedPreferences?.getInt(key, resources.getColor(R.color.default_background_color, null))
             settingslayout.setBackgroundColor(bgColor ?: resources.getColor(R.color.default_background_color, null))
         }
    }

}



