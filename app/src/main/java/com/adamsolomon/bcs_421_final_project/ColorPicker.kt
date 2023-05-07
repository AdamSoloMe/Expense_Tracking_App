package com.adamsolomon.bcs_421_final_project

import android.content.Context
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import yuku.ambilwarna.AmbilWarnaDialog

class ColorPicker(private val context: Context) {
    private var currentColor: Int = Color.BLACK

    fun show(onColorPicked: (color: Int) -> Unit) {
        val colorPicker = AmbilWarnaDialog(context, currentColor, object : AmbilWarnaDialog.OnAmbilWarnaListener {
            override fun onCancel(dialog: AmbilWarnaDialog?) {
                // Do nothing on cancel
            }

            override fun onOk(dialog: AmbilWarnaDialog?, color: Int) {
                currentColor = color
                onColorPicked(color)
            }
        })

        colorPicker.show()
    }

    fun setCurrentColor(color: Int) {
        currentColor = color
    }

    fun getCurrentColor(): Int {
        return currentColor
    }
}
