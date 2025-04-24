package com.example.xtrack

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import android.widget.Button
import android.widget.DatePicker
import android.widget.Spinner
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.button.MaterialButton
import java.io.BufferedReader
import java.io.InputStreamReader
import java.util.Calendar

class WorkoutFragment : Fragment() {

    private lateinit var datePicker: DatePicker
    private lateinit var workoutRecyclerView: RecyclerView
    private lateinit var fabToday: Button
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_workout, container, false)

        datePicker = view.findViewById(R.id.datePicker)
        workoutRecyclerView = view.findViewById(R.id.workoutRecyclerView)
        workoutRecyclerView.layoutManager = LinearLayoutManager(requireContext())

        val calendar = Calendar.getInstance()
        val year = calendar.get(Calendar.YEAR)
        val month = calendar.get(Calendar.MONTH)
        val day = calendar.get(Calendar.DAY_OF_MONTH)

        fabToday =view.findViewById(R.id.fabToday)

        fabToday.setOnClickListener {
            val today = Calendar.getInstance()
            val year = today.get(Calendar.YEAR)
            val month = today.get(Calendar.MONTH)
            val day = today.get(Calendar.DAY_OF_MONTH)

            datePicker.updateDate(year, month, day)

            val formattedDate = String.format("%04d-%02d-%02d", year, month + 1, day)
            displayWorkoutDataForDate(formattedDate)
        }


        // Format today's date
        val formattedDate = String.format("%04d-%02d-%02d", year, month + 1, day)
        displayWorkoutDataForDate(formattedDate) // 🔥 Show current date workout data on launch

        // Initialize date picker and set listener
        datePicker.init(year, month, day) { _, selectedYear, selectedMonth, selectedDay ->
            val selectedDate = String.format("%04d-%02d-%02d", selectedYear, selectedMonth + 1, selectedDay)
            displayWorkoutDataForDate(selectedDate)
        }
        return view
    }

    private fun readWorkoutDataFromFile(): List<Workout> {
        val workouts = mutableListOf<Workout>()
        try {
            val inputStream = requireContext().openFileInput("workout_log.txt")
            val reader = BufferedReader(InputStreamReader(inputStream))
            var line: String?

            while (reader.readLine().also { line = it } != null) {
                val parts = line!!.split(", ")
                if (parts.size == 6) {
                    val workout = Workout(parts[0], parts[1], parts[2], parts[3], parts[4], parts[5])
                    workouts.add(workout)
                }
            }
            reader.close()
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return workouts
    }

    private fun displayWorkoutDataForDate(selectedDate: String) {
        val workoutData = readWorkoutDataFromFile()
        val workoutForSelectedDate = workoutData.filter { it.date == selectedDate }

        Log.d("WorkoutData", "Filtered for date $selectedDate: ${workoutForSelectedDate.size} items")

        val adapter = WorkoutAdapter(workoutForSelectedDate)
        workoutRecyclerView.adapter = adapter
    }

}
