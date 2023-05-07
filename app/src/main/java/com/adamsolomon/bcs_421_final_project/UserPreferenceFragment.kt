package com.adamsolomon.bcs_421_final_project


import android.content.Context
import android.os.Bundle
import android.view.ContextThemeWrapper
import android.view.LayoutInflater
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.preference.EditTextPreference
import androidx.preference.Preference
import androidx.preference.PreferenceFragmentCompat
import androidx.preference.PreferenceManager
import yuku.ambilwarna.AmbilWarnaDialog

class UserPreferenceFragment : PreferenceFragmentCompat() {

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.preferences, rootKey)


        val colorPickerPreference = findPreference<Preference>("color_preference")
        colorPickerPreference?.setOnPreferenceClickListener {
            showColorPickerDialog()
            true
        }

    }


    private fun showColorPickerDialog() {
        val defaultColor = 0xFFFFF8 // Set default color here
        val dialog = AmbilWarnaDialog(requireContext(), defaultColor, object : AmbilWarnaDialog.OnAmbilWarnaListener {
            override fun onCancel(dialog: AmbilWarnaDialog?) {}

            override fun onOk(dialog: AmbilWarnaDialog?, color: Int) {
                // Save selected color to shared preferences
                val sharedPrefs = preferenceManager.sharedPreferences
                val editor = sharedPrefs!!.edit()
                editor.putInt("color_preference", color)
                editor.apply()

                // Update the color preview
                updateColorPreview()

                // Update the activity's shared preferences
                val activity = activity as AppCompatActivity
                val activitySharedPrefs = activity.getSharedPreferences("myPreference", Context.MODE_PRIVATE)
                val activityEditor = activitySharedPrefs.edit()
                activityEditor.putInt("bgColor", color)
                activityEditor.apply()


            }

        })
        dialog.show()
    }


    // Update the color preview in the preference fragment
    fun updateColorPreview() {
        val preferences = PreferenceManager.getDefaultSharedPreferences(requireContext())
        val colorPreference = findPreference<androidx.preference.Preference>("color_preference")

        // Set the summary text to the current color value
        colorPreference?.summary = String.format("#%06X", 0xFFFFFF and preferences.getInt("color_preference", 0xFFFFFFFF.toInt()))

        // Update the icon to show the current color value
        val icon = colorPreference?.icon?.mutate()
        icon?.setTint(preferences.getInt("color_preference", 0xFFFFFFFF.toInt()))
        colorPreference?.icon = icon
    }
}