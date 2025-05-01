package com.example.xtrack

import android.annotation.SuppressLint
import android.app.Dialog
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter
import java.util.*
import android.content.Context
import android.graphics.Color
import android.graphics.Typeface
import android.os.Bundle
import android.util.Log
import android.view.Gravity
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.view.WindowManager
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.ImageView
import android.widget.RadioGroup
import android.widget.Spinner
import android.widget.TextView
import androidx.core.graphics.ColorUtils
import androidx.core.graphics.drawable.toDrawable
import androidx.fragment.app.Fragment
import com.github.mikephil.charting.animation.Easing
import com.github.mikephil.charting.charts.BarChart
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.BarData
import com.github.mikephil.charting.data.BarDataSet
import com.github.mikephil.charting.data.BarEntry
import com.github.mikephil.charting.formatter.ValueFormatter
import java.io.BufferedReader
import java.io.InputStreamReader
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale
import androidx.core.graphics.toColorInt
import com.github.mikephil.charting.charts.RadarChart
import com.github.mikephil.charting.data.RadarData
import com.github.mikephil.charting.data.RadarDataSet
import com.github.mikephil.charting.data.RadarEntry
import com.github.mikephil.charting.components.XAxis.XAxisPosition


class StatsFragment : Fragment() {

    private lateinit var barChart: CustomBarChart
    private var currentFilter = "Last 7 Days"
    private var currentMetric = "Performance"
    private var selectedCategory: String = "All"
    private var selectedSubExercise: String = "All"
    private lateinit var radarChart: RadarChart


    private val workoutData = mutableListOf<Workout>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_stats, container, false)

        barChart = view.findViewById(R.id.barChart)
        radarChart = view.findViewById(R.id.radarChart)

        val filterIcon = view.findViewById<ImageView>(R.id.filterIcon)

        // Disable the button on click and prevent multiple clicks
        filterIcon.setOnClickListener {
            filterIcon.isEnabled = false  // Disable the button to prevent multiple clicks
            showFilterPopup(requireContext()) {
                filterIcon.isEnabled = true  // Re-enable the button once the dialog is dismissed
            }
        }
        val clickAnimation = View.OnTouchListener { v, event ->
            when (event.action) {
                MotionEvent.ACTION_DOWN -> {
                    v.animate().scaleX(1.5f).scaleY(1.5f).setDuration(100).start()
                }
                MotionEvent.ACTION_UP, MotionEvent.ACTION_CANCEL -> {
                    v.animate().scaleX(1f).scaleY(1f).setDuration(100).start()
                }
            }
            false
        }

// Apply the animation listener to both icon and text
        filterIcon.setOnTouchListener(clickAnimation)

        loadWorkoutData()
        updateChart()
        updateRadarChart(currentFilter)


        return view
    }

    private fun loadWorkoutData() {
        try {
            val inputStream = requireContext().openFileInput("workout_log.txt")
            val reader = BufferedReader(InputStreamReader(inputStream))
            var line: String?

            while (reader.readLine().also { line = it } != null) {
                val parts = line!!.split(", ")
                if (parts.size >= 6) {
                    val cleanedReps = parts[4].replace(" ", "").replace("|", ",").trim()
                    val cleanedWeights = parts[5].replace(" ", "").replace("|", ",").trim()
                    workoutData.add(Workout(parts[0], parts[1], parts[2], parts[3], cleanedReps, cleanedWeights))
                } else {
                    Log.e("WorkoutData", "Invalid Data Format: $line")
                }
            }
            reader.close()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun updateChart() {
        val filteredData = filterWorkouts(currentFilter)
        val chartData = calculateMetrics(filteredData, currentMetric, currentFilter)

        if (chartData.isEmpty()) {
            // No Data Case
            barChart.clear()
            barChart.setNoDataText("")
            barChart.axisLeft.isEnabled = false
            barChart.xAxis.isEnabled = false
            barChart.legend.isEnabled = false
            barChart.invalidate()
            return
        }

        // Data Available Case
        barChart.axisLeft.isEnabled = true
        barChart.xAxis.isEnabled = true
        barChart.legend.isEnabled = true

        val entries = chartData.mapIndexed { index, data ->
            BarEntry(index.toFloat(), data.second)
        }

        val dataSet = BarDataSet(entries, currentMetric).apply {
            val startColor = "#B3E428".toColorInt() // Lighter Green
            val endColor = "#80A907".toColorInt()
            color = ColorUtils.blendARGB(startColor, endColor, 0f)
            valueTextColor = Color.WHITE
            valueTextSize = 12f
        }

        val barData = BarData(dataSet)
        barChart.apply {
            barChart.renderer = RoundedBarChartRenderer(barChart, barChart.animator, barChart.viewPortHandler)

            data = barData
            description.isEnabled = false
            setFitBars(true)
            animateY(1000, Easing.EaseInBounce)
            setScaleEnabled(false)           // Disable zoom via pinch or axis dragging
            setPinchZoom(false)              // Disable pinch zoom specifically
            isDoubleTapToZoomEnabled = false // Disable double tap to zoom
            setDragEnabled(false)             // (Optional) Still allow dragging if you want to scroll

            xAxis.apply {
                position = XAxis.XAxisPosition.BOTTOM
                setDrawGridLines(false)
                textColor = Color.WHITE
                granularity = 1f
                valueFormatter = object : ValueFormatter() {
                    override fun getFormattedValue(value: Float): String {
                        return chartData.getOrNull(value.toInt())?.first ?: ""
                    }
                }
            }

            axisLeft.apply {
                setDrawGridLines(false)
                textColor = Color.WHITE
                valueFormatter = object : ValueFormatter() {
                    override fun getFormattedValue(value: Float): String {
                        return if (currentMetric == "Performance") "${value.toInt()} reps" else "${value.toInt()}%"
                    }
                }
            }

            axisRight.isEnabled = false
            legend.textColor = Color.WHITE
            invalidate()
        }
    }

    private fun filterWorkouts(filter: String): List<Workout> {
        val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        val calendar = Calendar.getInstance()

        val filteredByTime = when (filter) {
            "Last 7 Days" -> {
                calendar.add(Calendar.DAY_OF_YEAR, -7)
                val sevenDaysAgo = calendar.time
                workoutData.filter { dateFormat.parse(it.date)!!.after(sevenDaysAgo) }
            }
            "Months" -> workoutData
            "All Time" -> workoutData
            else -> workoutData
        }

        return filteredByTime.filter {
            (selectedCategory == "All" || it.exercise == selectedCategory) &&
                    (selectedSubExercise == "All" || it.subexercise == selectedSubExercise)
        }
    }

    private fun calculateMetrics(workouts: List<Workout>, metric: String, filter: String): List<Pair<String, Float>> {
        val dataMap = HashMap<String, Float>()
        val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())

        for (workout in workouts) {
            val date = dateFormat.parse(workout.date) ?: continue
            val calendar = Calendar.getInstance().apply { time = date }

            val key = when (filter) {
                "Last 7 Days" -> SimpleDateFormat("EEE", Locale.getDefault()).format(date)
                "Months" -> SimpleDateFormat("MMM", Locale.getDefault()).format(date)
                "All Time" -> calendar.get(Calendar.YEAR).toString()
                else -> workout.date.substring(5)
            }

            val sets = workout.sets.toIntOrNull() ?: 0
            val repsList = workout.reps.split(",").mapNotNull { it.toIntOrNull() }
            val totalReps = repsList.sum()

            val value = if (metric == "Performance") {
                sets * totalReps
            } else {
                (sets * totalReps) / 10f
            }

            dataMap[key] = (dataMap[key] ?: 0f) + value.toFloat()
        }

        return when (filter) {
            "Months" -> dataMap.toSortedMap(compareBy { getMonthIndex(it) }).map { Pair(it.key, it.value) }
            "All Time" -> dataMap.toSortedMap(compareBy { it.toInt() }).map { Pair(it.key, it.value) }
            else -> dataMap.map { Pair(it.key, it.value) }
        }
    }

    private fun getMonthIndex(month: String): Int {
        return try {
            val date = SimpleDateFormat("MMM", Locale.getDefault()).parse(month)
            val calendar = Calendar.getInstance().apply { time = date!! }
            calendar.get(Calendar.MONTH)
        } catch (_: Exception) {
            0
        }
    }

    @SuppressLint("InflateParams")
    fun showFilterPopup(context: Context, onDismissCallback: () -> Unit) {
        val dialog = Dialog(context)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setContentView(R.layout.layout_filter_bottom_sheet)
        dialog.window?.setDimAmount(0.5f)

        dialog.setCancelable(true)
        dialog.setCanceledOnTouchOutside(true)

        dialog.window?.setBackgroundDrawable(Color.TRANSPARENT.toDrawable())
        dialog.window?.setLayout(
            WindowManager.LayoutParams.MATCH_PARENT,
            WindowManager.LayoutParams.WRAP_CONTENT
        )
        dialog.window?.setGravity(Gravity.CENTER)

        dialog.show()

        val bottomSheetView = layoutInflater.inflate(R.layout.layout_filter_bottom_sheet, null)

        val categorySpinner = bottomSheetView.findViewById<Spinner>(R.id.exerciseCategorySpinner)
        val subExerciseSpinner = bottomSheetView.findViewById<Spinner>(R.id.subExerciseSpinner)

        val categories = mutableSetOf("All")
        val subExercises = mutableSetOf("All")

        val closeIcon = bottomSheetView.findViewById<ImageView>(R.id.closeIcon)

        closeIcon.setOnClickListener {
            dialog.dismiss() // Dismiss the dialog
            onDismissCallback() // Re-enable the filter button (defined in the activity/fragment)
        }


        workoutData.forEach {
            categories.add(it.exercise)
            if (selectedCategory == "All" || it.exercise == selectedCategory) {
                subExercises.add(it.subexercise)
            }
        }

        val categoryAdapter = ArrayAdapter(requireContext(), R.layout.spinner_item, categories.toList())
        categoryAdapter.setDropDownViewResource(R.layout.spinner_dropdown_item)
        categorySpinner.adapter = categoryAdapter

        val subExerciseAdapter = ArrayAdapter(requireContext(), R.layout.spinner_item, subExercises.toList())
        subExerciseAdapter.setDropDownViewResource(R.layout.spinner_dropdown_item)
        subExerciseSpinner.adapter = subExerciseAdapter


        categorySpinner.setSelection(categories.indexOf(selectedCategory))
        subExerciseSpinner.setSelection(subExercises.indexOf(selectedSubExercise))

        categorySpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View?, position: Int, id: Long) {
                selectedCategory = parent.getItemAtPosition(position).toString()
                val updatedSubExercises = mutableListOf("All")
                updatedSubExercises.addAll(
                    workoutData
                        .filter { selectedCategory == "All" || it.exercise == selectedCategory }
                        .map { it.subexercise }
                        .distinct()
                )
                val updatedAdapter = ArrayAdapter(requireContext(), R.layout.spinner_item, updatedSubExercises)
                updatedAdapter.setDropDownViewResource(R.layout.spinner_dropdown_item)
                subExerciseSpinner.adapter = updatedAdapter

            }

            override fun onNothingSelected(parent: AdapterView<*>) {}
        }

        dialog.setContentView(bottomSheetView)

        val timeGroup = bottomSheetView.findViewById<RadioGroup>(R.id.timeFilterGroup)
        val metricGroup = bottomSheetView.findViewById<RadioGroup>(R.id.metricGroup)
        val applyButton = bottomSheetView.findViewById<Button>(R.id.applyFilterButton)

        when (currentFilter) {
            "Last 7 Days" -> timeGroup.check(R.id.last7DaysOption)
            "Months" -> timeGroup.check(R.id.monthsOption)
            "All Time" -> timeGroup.check(R.id.allTimeOption)
        }

        when (currentMetric) {
            "Performance" -> metricGroup.check(R.id.performanceOption)
            "Strength" -> metricGroup.check(R.id.strengthOption)
        }

        applyButton.setOnClickListener {
            selectedCategory = categorySpinner.selectedItem.toString()
            selectedSubExercise = subExerciseSpinner.selectedItem.toString()

            currentFilter = when (timeGroup.checkedRadioButtonId) {
                R.id.last7DaysOption -> "Last 7 Days"
                R.id.monthsOption -> "Months"
                R.id.allTimeOption -> "All Time"
                else -> "Last 7 Days"
            }

            currentMetric = when (metricGroup.checkedRadioButtonId) {
                R.id.performanceOption -> "Performance"
                R.id.strengthOption -> "Strength"
                else -> "Performance"
            }

            updateChart()
            updateRadarChart(currentFilter)
            dialog.dismiss()
            onDismissCallback()
        }

        dialog.setOnDismissListener {
            onDismissCallback()
        }
    }

    fun updateRadarChart(selectedTimeRange: String) {
        val customTypeface = Typeface.createFromAsset(context?.assets, "fonts/orbitron_medium.ttf")

        val categories = listOf("Chest Exercises", "Back Exercises", "Shoulder Exercises", "Biceps Exercises",
            "Triceps Exercises", "Leg Exercises", "Core & Abs Exercises", "Cardio & Functional Exercises")
        val categoriesForDis = listOf("Chest", "Back", "Shoulder", "Biceps",
            "Triceps", "Leg", "Core", "Cardio")
        val categoryValues = mutableMapOf<String, Float>()

        // Initialize with zero
        categories.forEach { categoryValues[it] = 0f }

        val filtered = filterWorkouts(selectedTimeRange)

        for (workout in filtered) {
            if (categories.contains(workout.exercise)) {
                val sets = workout.sets.toIntOrNull() ?: 0
                val repsList = workout.reps.split(",").mapNotNull { it.toIntOrNull() }
                val totalReps = repsList.sum()
                val value = if (currentMetric == "Performance") {
                    sets * totalReps
                } else {
                    (sets * totalReps) / 10f
                }

                categoryValues[workout.exercise] = categoryValues[workout.exercise]!! + value.toFloat()
            }
        }

        val entries = categories.map { RadarEntry(categoryValues[it] ?: 0f) }

        val dataSet = RadarDataSet(entries, "Muscle Analysis").apply {
            color = Color.CYAN
            fillColor = Color.CYAN
            fillAlpha = 70
            setDrawFilled(true)
            lineWidth = 3f
            valueTextColor = Color.TRANSPARENT
        }

        val radarData = RadarData(dataSet)

        radarChart.apply {
            data = radarData
            description.isEnabled = false
            setTouchEnabled(false)
            setWebLineWidth(1.5f)
            setWebColor(Color.DKGRAY)
            setWebLineWidthInner(1.2f)
            setWebColorInner(Color.GRAY)
            setWebAlpha(100)

            xAxis.apply {
                valueFormatter = IndexAxisValueFormatter(categoriesForDis)
                textColor = Color.WHITE
                textSize = 12.5f
                textColor = Color.LTGRAY
                position = XAxis.XAxisPosition.TOP_INSIDE
                typeface = customTypeface
                yOffset = 0f
                xOffset = 0f
            }
            yAxis.apply {
                textColor = Color.WHITE
                axisMinimum = 0f
                setDrawLabels(false)
            }
            legend.isEnabled = false
            animateXY(800, 800)
            invalidate()
        }
    }

}
