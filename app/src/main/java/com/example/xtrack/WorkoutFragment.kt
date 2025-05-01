package com.example.xtrack

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import android.widget.Button
import android.widget.DatePicker
import android.widget.LinearLayout
import android.widget.Spinner
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContentProviderCompat.requireContext
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
    private lateinit var fabToday: LinearLayout
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

        val clickAnimation = View.OnTouchListener { v, event ->
            when (event.action) {
                MotionEvent.ACTION_DOWN -> {
                    v.animate().scaleX(0.8f).scaleY(0.8f).setDuration(100).start()
                }
                MotionEvent.ACTION_UP, MotionEvent.ACTION_CANCEL -> {
                    v.animate().scaleX(1f).scaleY(1f).setDuration(100).start()
                }
            }
            false
        }

// Apply the animation listener to both icon and text
        fabToday.setOnTouchListener(clickAnimation)

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

        val adapter = WorkoutAdapter(workoutForSelectedDate.toMutableList()){ workout ->
            showDeleteConfirmationDialog(workout)
        }
        workoutRecyclerView.adapter = adapter
    }

    private fun showDeleteConfirmationDialog(workout: Workout) {
        val dialogView = LayoutInflater.from(requireContext()).inflate(R.layout.dialog_delete_confirmation, null)

        val alertDialog = AlertDialog.Builder(requireContext(), R.style.MaterialAlertDialog_Rounded)
            .setView(dialogView) // ✅ Use only the custom layout
            .create()

        dialogView.findViewById<Button>(R.id.btnCancel).setOnClickListener {
            alertDialog.dismiss()
        }

        dialogView.findViewById<Button>(R.id.btnDelete).setOnClickListener {
            deleteWorkout(workout)
            alertDialog.dismiss()
        }

        alertDialog.show()
    }




    private fun deleteWorkout(workout: Workout) {
        // Remove workout from the list
        val workoutData = readWorkoutDataFromFile().toMutableList()
        workoutData.remove(workout)

        // Update RecyclerView
        (workoutRecyclerView.adapter as WorkoutAdapter).updateData(workoutData)

        // Delete the workout from the file
        writeWorkoutDataToFile(workoutData)
    }
    private fun writeWorkoutDataToFile(workoutData: List<Workout>) {
        try {
            val outputStream = requireContext().openFileOutput("workout_log.txt", Context.MODE_PRIVATE)
            val writer = outputStream.bufferedWriter()

            for (workout in workoutData) {
                writer.write("${workout.date}, ${workout.exercise}, ${workout.subexercise}, ${workout.sets}, ${workout.reps}, ${workout.weights}\n")
            }

            writer.close()
            outputStream.close()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

}
