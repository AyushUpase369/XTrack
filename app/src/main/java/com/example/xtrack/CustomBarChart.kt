package com.example.xtrack

import android.R
import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Typeface
import android.util.AttributeSet
import com.github.mikephil.charting.charts.BarChart

class CustomBarChart(context: Context, attrs: AttributeSet) : BarChart(context, attrs) {

    private val noDataPaint = Paint().apply {
        color = Color.RED      // Text Color
        textSize = 55f // Text Size (make it bigger if you want)
        val typeface = Typeface.createFromAsset(context.assets, "fonts/orbitron_extrabold.ttf") // <- load from assets
        this.typeface = typeface
        textAlign = Paint.Align.CENTER
        isAntiAlias = true        // Smooth text
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        if (data == null || data.entryCount == 0) {
            canvas.drawText(
                "Workout data Not Available",  // This is the default message
                width / 2f,
                height / 2f,
                noDataPaint
            )
        }
    }
}
