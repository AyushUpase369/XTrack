package com.example.xtrack

import android.graphics.Color
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.ImageView
import android.widget.RadioGroup
import android.widget.Spinner
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
import java.util.*
import kotlin.collections.HashMap
import com.google.android.material.bottomsheet.BottomSheetDialog


class StatsFragment : Fragment() {

    private lateinit var barChart: BarChart
    private lateinit var filterSpinner: Spinner
    private lateinit var metricSpinner: Spinner
    private var currentFilter = "Last 7 Days"
    private var currentMetric = "Performance"

    private val workoutData = mutableListOf<Workout>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_stats, container, false)

        barChart = view.findViewById(R.id.barChart)
        filterSpinner = view.findViewById(R.id.filterSpinner)
        metricSpinner = view.findViewById(R.id.metricSpinner)

        val filterOptions = arrayOf("Last 7 Days", "Months", "All Time")
        val metricOptions = arrayOf("Performance", "Strength")

        filterSpinner.adapter = ArrayAdapter(
            requireContext(),
            android.R.layout.simple_spinner_dropdown_item,
            filterOptions
        )

        metricSpinner.adapter = ArrayAdapter(
            requireContext(),
            android.R.layout.simple_spinner_dropdown_item,
            metricOptions
        )

        val filterIcon = view.findViewById<ImageView>(R.id.filterIcon)
        filterIcon.setOnClickListener {
            showFilterBottomSheet()
        }


        filterSpinner.onItemSelectedListener = spinnerListener
        metricSpinner.onItemSelectedListener = spinnerListener

        loadWorkoutData()
        return view
    }

    private val spinnerListener = object : AdapterView.OnItemSelectedListener {
        override fun onItemSelected(parent: AdapterView<*>, view: View?, position: Int, id: Long) {
            updateChart()
        }

        override fun onNothingSelected(parent: AdapterView<*>) {}
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

        val selectedFilter = filterSpinner.selectedItem.toString()
        val selectedMetric = metricSpinner.selectedItem.toString()

        val filteredData = filterWorkouts(currentFilter)
        val chartData = calculateMetrics(filteredData, currentMetric, currentFilter)

        val entries = chartData.mapIndexed { index, data ->
            BarEntry(index.toFloat(), data.second)
        }

        val dataSet = BarDataSet(entries, selectedMetric).apply {
            color = Color.GREEN
            valueTextColor = Color.WHITE
            valueTextSize = 12f
        }

        val barData = BarData(dataSet)
        barChart.apply {
            data = barData
            description.isEnabled = false
            setFitBars(true)
            animateY(1000, Easing.EaseInBounce)

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
                        return if (selectedMetric == "Performance") "$value reps" else "$value strength"
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

        return when (filter) {
            "Last 7 Days" -> {
                calendar.add(Calendar.DAY_OF_YEAR, -7)
                val sevenDaysAgo = calendar.time
                workoutData.filter { dateFormat.parse(it.date)!!.after(sevenDaysAgo) }
            }
            "Months" -> workoutData // No filtering needed for month-wise display
            else -> workoutData
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

        // Sort labels properly
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
        } catch (e: Exception) {
            0
        }
    }

    private fun showFilterBottomSheet() {
        val bottomSheetView = layoutInflater.inflate(R.layout.layout_filter_bottom_sheet, null)
        val dialog = BottomSheetDialog(requireContext())
        dialog.setContentView(bottomSheetView)

        val timeGroup = bottomSheetView.findViewById<RadioGroup>(R.id.timeFilterGroup)
        val metricGroup = bottomSheetView.findViewById<RadioGroup>(R.id.metricGroup)
        val applyButton = bottomSheetView.findViewById<Button>(R.id.applyFilterButton)

        // Set current selection
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
            dialog.dismiss()
        }

        dialog.show()
    }

}
