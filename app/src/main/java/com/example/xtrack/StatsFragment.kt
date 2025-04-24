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
import java.util.Calendar
import java.util.Locale

class StatsFragment : Fragment() {

    private lateinit var barChart: BarChart
    private lateinit var filterSpinner: Spinner
    private lateinit var metricSpinner: Spinner
    private val workoutData = mutableListOf<Workout>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_stats, container, false)

        barChart = view.findViewById(R.id.barChart)
        filterSpinner = view.findViewById(R.id.filterSpinner)
        metricSpinner = view.findViewById(R.id.metricSpinner)

        val filterOptions = arrayOf("Last 7 Days", "This Month", "All Time")
        val metricOptions = arrayOf("Performance", "Strength")

        filterSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>,
                view: View?,
                position: Int,
                id: Long
            ) {
                updateChart()
            }

            override fun onNothingSelected(parent: AdapterView<*>) {}
        }

        metricSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>,
                view: View?,
                position: Int,
                id: Long
            ) {
                updateChart()
            }

            override fun onNothingSelected(parent: AdapterView<*>) {}
        }


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

        loadWorkoutData()

        filterSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>,
                view: android.view.View?,
                position: Int,
                id: Long
            ) {
                updateChart()
            }

            override fun onNothingSelected(parent: AdapterView<*>) {}
        }

        metricSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>,
                view: android.view.View?,
                position: Int,
                id: Long
            ) {
                updateChart()
            }

            override fun onNothingSelected(parent: AdapterView<*>) {}
        }
        return view
    }

    private fun loadWorkoutData() {
        try {
            val inputStream = requireContext().openFileInput("workout_log.txt")
            val reader = BufferedReader(InputStreamReader(inputStream))
            var line: String?

            while (reader.readLine().also { line = it } != null) {
                val parts = line!!.split(", ")
                Log.d("WorkoutData", "Loaded Data: $line") // ✅ Check loaded data

                if (parts.size >= 6) {
                    val cleanedReps = parts[4].replace(" ", "").replace("|", ",").trim()
                    val cleanedWeights = parts[5].replace(" ", "").replace("|", ",").trim()

                    workoutData.add(Workout(parts[0], parts[1], parts[2], parts[3], cleanedReps, cleanedWeights))
                } else {
                    Log.e("WorkoutData", "Invalid Data Format: $line") // 🚨 Data format issue
                }
            }
            reader.close()

            Log.d("WorkoutData", "Total Loaded Workouts: ${workoutData.size}")
        } catch (e: Exception) {
            e.printStackTrace()
            Log.e("WorkoutData", "Error reading data: ${e.message}")
        }
    }


    private fun updateChart() {
        val selectedFilter = filterSpinner.selectedItem.toString()
        val selectedMetric = metricSpinner.selectedItem.toString()

        val filteredData = filterWorkouts(selectedFilter)
        val chartData = calculateMetrics(filteredData, selectedMetric, selectedFilter)

        val entries = chartData.mapIndexed { index, data ->
            BarEntry(index.toFloat(), data.second)
        }

        val dataSet = BarDataSet(entries, selectedMetric).apply {
            color = Color.GREEN
            valueTextColor = Color.WHITE
            valueTextSize = 12f
            barChart.axisLeft.setDrawGridLines(false)
            barChart.axisRight.setDrawGridLines(false)
            barChart.xAxis.setDrawGridLines(false)
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
                xAxis.valueFormatter = object : ValueFormatter() {
                    override fun getFormattedValue(value: Float): String {
                        return chartData.getOrNull(value.toInt())?.first ?: ""
                    }
                }



                granularity = 1f
            }

            barChart.axisLeft.valueFormatter = object : ValueFormatter() {
                override fun getFormattedValue(value: Float): String {
                    val selectedMetric = metricSpinner.selectedItem.toString()
                    return if (selectedMetric == "Performance") {
                        "$value reps"
                    } else {
                        "$value strength"
                    }
                }
            }


            axisLeft.textColor = Color.WHITE
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
            "This Month" -> {
                calendar.set(Calendar.DAY_OF_MONTH, 1)
                val startOfMonth = calendar.time
                workoutData.filter { dateFormat.parse(it.date)!!.after(startOfMonth) }
            }
            else -> workoutData
        }
    }

    private fun calculateMetrics(workouts: List<Workout>, metric: String, filter: String): List<Pair<String, Float>> {
        val dataMap = HashMap<String, Float>()
        val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        val calendar = Calendar.getInstance()

        for (workout in workouts) {
            val date = dateFormat.parse(workout.date)
            calendar.time = date!!

            val key = when (filter) {
                "Last 7 Days" -> SimpleDateFormat("EEE", Locale.getDefault()).format(date)
                "This Month" -> SimpleDateFormat("MMMM", Locale.getDefault()).format(date)
                else -> workout.date.substring(5)
            }

            val sets = workout.sets.toIntOrNull() ?: 0
            val repsList = workout.reps.split(",").mapNotNull { it.toIntOrNull() } // Updated to split by "," and convert to Int
            val totalReps = repsList.sum()

            Log.d("Sets", "Sets: $sets")
            Log.d("Reps", "Reps: $totalReps")

            val value = if (metric == "Performance") {
                sets * totalReps  // Correct total reps calculation
            } else {
                (sets * totalReps) / 10f  // Arbitrary strength calculation
            }

            dataMap[key] = (dataMap[key] ?: 0f) + value.toFloat()
        }

        return dataMap.map { Pair(it.key, it.value) }.sortedBy { it.first }
    }

    private fun calculatePerformanceData(
        workoutList: List<Workout>,
        filterType: String
    ): Map<String, Float> {
        val performanceData = mutableMapOf<String, Float>()

        val groupedData = when (filterType) {
            "Last 7 Days" -> groupByWeek(workoutList)
            "This Month" -> groupByMonth(workoutList)
            else -> emptyMap()
        }

        Log.d("ChartData", "Grouped Data Size: ${groupedData.size}")

        for ((period, workouts) in groupedData) {
            var totalVolume = 0f
            var totalReps = 0f

            for (workout in workouts) {
                val reps = workout.reps.split(",").map { it.toIntOrNull() ?: 0 }
                val weights = workout.weights.split(",").map { it.toIntOrNull() ?: 0 }

                for (i in reps.indices) {
                    totalVolume += reps[i] * weights.getOrElse(i) { 0 }
                    totalReps += reps[i]
                }
            }

            val averageIntensity = if (totalReps > 0) totalVolume / totalReps else 0f
            performanceData[period] = averageIntensity

            Log.d("ChartData", "Period: $period | Avg Intensity: $averageIntensity") // ✅ Debug calculated values
        }

        return performanceData
    }


    private fun groupByWeek(workoutList: List<Workout>): Map<String, List<Workout>> {
        val groupedData = mutableMapOf<String, MutableList<Workout>>()
        val calendar = Calendar.getInstance()

        for (workout in workoutList) {
            val workoutDate = workout.getDateAsDate()
            calendar.time = workoutDate
            val weekOfYear = calendar.get(Calendar.WEEK_OF_YEAR)
            val year = calendar.get(Calendar.YEAR)

            val key = "Week $weekOfYear ($year)"
            groupedData.getOrPut(key) { mutableListOf() }.add(workout)
        }

        return groupedData
    }

    private fun groupByMonth(workoutList: List<Workout>): Map<String, List<Workout>> {
        val groupedData = mutableMapOf<String, MutableList<Workout>>()
        val calendar = Calendar.getInstance()

        for (workout in workoutList) {
            val workoutDate = workout.getDateAsDate()
            calendar.time = workoutDate
            val month = calendar.getDisplayName(Calendar.MONTH, Calendar.LONG, Locale.getDefault())
            val year = calendar.get(Calendar.YEAR)

            val key = "$month $year"
            groupedData.getOrPut(key) { mutableListOf() }.add(workout)
        }

        return groupedData
    }



}

