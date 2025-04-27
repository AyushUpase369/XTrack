package com.example.xtrack

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.util.AttributeSet
import com.github.mikephil.charting.charts.BarChart

class CustomBarChart(context: Context, attrs: AttributeSet) : BarChart(context, attrs) {

    private val noDataPaint = Paint().apply {
        color = Color.RED      // Text Color
        textSize = 60f            // Text Size (make it bigger if you want)
        textAlign = Paint.Align.CENTER
        isAntiAlias = true        // Smooth text
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        if (data == null || data.entryCount == 0) {
            // If there's no data, draw the no data message
            canvas.drawText(
                "Workout data Not Available",  // This is the default message
                width / 2f,
                height / 2f,
                noDataPaint
            )
        }
    }
}
