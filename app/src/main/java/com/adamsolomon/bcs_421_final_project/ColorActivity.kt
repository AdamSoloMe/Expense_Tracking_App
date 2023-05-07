package com.adamsolomon.bcs_421_final_project

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.RelativeLayout
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import yuku.ambilwarna.AmbilWarnaDialog



class ColorActivity : AppCompatActivity() {

    private lateinit var layout: ConstraintLayout
    private lateinit var colorPicker: ColorPicker
    private lateinit var button: Button
    private var defaultColor: Int = 0

    private var isColor1 = true

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_color)

        layout = findViewById(R.id.layout_color)
        button = findViewById(R.id.button)
        val sharedPref = getSharedPreferences("myPreference", Context.MODE_PRIVATE)
        defaultColor = sharedPref.getInt("bgColor", resources.getColor(R.color.default_background_color, null))
        setBackgroundColor(defaultColor)

        colorPicker = ColorPicker(this)
        button.setOnClickListener {
            val dialog = AmbilWarnaDialog(this@ColorActivity, defaultColor, object : AmbilWarnaDialog.OnAmbilWarnaListener {
                override fun onCancel(dialog: AmbilWarnaDialog?) {}

                override fun onOk(dialog: AmbilWarnaDialog?, color: Int) {
                    setBackgroundColor(color)

                    // Save the selected color to shared preference
                    val editor = sharedPref.edit()
                    editor.putInt("bgColor", color)
                    editor.apply()
                }
            })

            dialog.show()
        }

    }
    private fun setBackgroundColor(color: Int) {
        layout.setBackgroundColor(color)
    }

}