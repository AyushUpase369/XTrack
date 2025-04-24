package com.example.xtrack

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import android.widget.Spinner
import androidx.fragment.app.Fragment

class WorkoutFragment : Fragment() {

    private lateinit var searchEditText: AutoCompleteTextView

    private lateinit var muscleGroupSpinner: Spinner

    // Sample exercise map for each muscle group
    private val exercisesMap = mapOf(
        "Chest" to listOf("Barbell Bench Press (Flat)", "Barbell Bench Press (Incline)", "Barbell Bench Press (Decline)",
            "Dumbbell Bench Press (Flat)", "Dumbbell Bench Press (Incline)", "Dumbbell Bench Press (Decline)",
            "Machine Chest Press (Seated)", "Machine Chest Press (Incline)", "Machine Chest Press (Decline)",
            "Smith Machine Bench Press", "Push-Ups (Standard)", "Push-Ups (Wide)", "Push-Ups (Diamond)",
            "Push-Ups (Plyometric)", "Push-Ups (Weighted)", "Dumbbell Chest Fly (Flat)",
            "Dumbbell Chest Fly (Incline)", "Dumbbell Chest Fly (Decline)", "Cable Chest Fly (High to Low)",
            "Cable Chest Fly (Low to High)", "Cable Chest Fly (Middle)", "Machine Pec Deck Fly",
            "Chest Dips", "Parallel Bar Dips"),


        "Back" to listOf("Pull-Ups (Standard)", "Pull-Ups (Wide-Grip)", "Pull-Ups (Close-Grip)", "Pull-Ups (Neutral-Grip)",
            "Pull-Ups (Weighted)", "Chin-Ups", "Lat Pulldown (Wide-Grip)", "Lat Pulldown (Close-Grip)",
            "Lat Pulldown (Neutral-Grip)", "Lat Pulldown (Reverse-Grip)", "Straight-Arm Pulldown",
            "Barbell Bent-Over Row (Overhand)", "Barbell Bent-Over Row (Underhand)",
            "Barbell Bent-Over Row (Wide-Grip)", "Dumbbell Bent-Over Row (Single-Arm)",
            "Dumbbell Bent-Over Row (Double-Arm)", "T-Bar Row", "Seated Cable Row (Wide-Grip)",
            "Seated Cable Row (Close-Grip)", "Seated Cable Row (Neutral-Grip)", "Chest-Supported Machine Row",
            "Conventional Deadlift", "Sumo Deadlift", "Romanian Deadlift", "Trap Bar Deadlift", "Good Mornings"),


        "Shoulders" to listOf("Barbell Overhead Press (Standing)", "Barbell Overhead Press (Seated)", "Dumbbell Overhead Press (Standing)",
            "Dumbbell Overhead Press (Seated)", "Arnold Press", "Machine Shoulder Press",
            "Lateral Raises (Dumbbell)", "Lateral Raises (Cable)", "Lateral Raises (Machine)",
            "Front Raises (Dumbbell)", "Front Raises (Barbell)", "Front Raises (Cable)",
            "Rear Delt Fly (Dumbbell)", "Rear Delt Fly (Cable)", "Rear Delt Fly (Machine)",
            "Barbell Shrugs", "Dumbbell Shrugs", "Trap Bar Shrugs"),


        "Biceps" to listOf("Barbell Curl (Standard)", "Barbell Curl (EZ-Bar)", "Dumbbell Curl (Alternating)",
            "Dumbbell Curl (Seated)", "Dumbbell Curl (Standing)", "Hammer Curl", "Preacher Curl (Barbell)",
            "Preacher Curl (Dumbbell)", "Concentration Curl", "Cable Biceps Curl"),


        "Triceps" to listOf("Close-Grip Bench Press", "Skull Crushers (Barbell)", "Skull Crushers (Dumbbell)", "Skull Crushers (EZ-Bar)",
            "Triceps Dips", "Triceps Pushdown (Rope)", "Triceps Pushdown (Bar)", "Triceps Pushdown (V-Bar)",
            "Overhead Triceps Extension (Dumbbell)", "Overhead Triceps Extension (Cable)", "Triceps Kickbacks"),


        "Legs" to listOf("Barbell Squat (Back)", "Barbell Squat (Front)", "Goblet Squat", "Bulgarian Split Squat",
            "Leg Press", "Lunges (Walking)", "Lunges (Stationary)", "Lunges (Reverse)", "Step-Ups",
            "Romanian Deadlift", "Stiff-Legged Deadlift", "Leg Curl (Seated)", "Leg Curl (Lying)",
            "Leg Extension", "Calf Raises (Seated)", "Calf Raises (Standing)"),


        "Core & Abs" to listOf("Planks (Standard)", "Planks (Side)", "Planks (Weighted)", "Hanging Leg Raises", "Bicycle Crunches",
            "Russian Twists", "Sit-Ups", "Decline Sit-Ups", "Ab Rollouts", "Cable Woodchoppers"),


        "Cardio & Functional Exercises" to listOf("Jump Rope", "Burpees", "Box Jumps", "Battle Ropes", "Kettlebell Swings",
            "Rowing Machine", "Treadmill Sprints", "Stair Climber")
    )

    private val muscleItems = listOf(
        MuscleItem("Chest", R.drawable.chest_icon),
        MuscleItem("Back", R.drawable.back_icon),
        MuscleItem("Shoulders", R.drawable.shoulders_icon),
        MuscleItem("Biceps", R.drawable.bicep_icon),
        MuscleItem("Triceps", R.drawable.triceps_icon),
        MuscleItem("Legs", R.drawable.legs_iocn),
        MuscleItem("Core & Abs", R.drawable.abs_icon),
        MuscleItem("Cardio & Functional Exercises", R.drawable.cardio_icon),
    )

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_workout, container, false)

        searchEditText = view.findViewById(R.id.searchExerciseEditText)
        muscleGroupSpinner = view.findViewById(R.id.muscleGroupSpinner)
        setSpinnerPlaceholder()

        val adapter = CustomMuscleAdapter(requireContext(), muscleItems)
        searchEditText.setAdapter(adapter)

        searchEditText.setOnItemClickListener { _, _, position, _ ->
            val selectedItem = adapter.getItem(position)
            val muscleName = selectedItem?.name ?: ""
            searchEditText.setText(muscleName)

            // Set Spinner with "Back Exercise" style
            val exercises = exercisesMap[muscleName] ?: emptyList()
            val spinnerList = listOf("$muscleName Exercise") + exercises

            val spinnerAdapter = ArrayAdapter(
                requireContext(),
                R.layout.item_spinner_selected, // main view with white text
                spinnerList
            )
            spinnerAdapter.setDropDownViewResource(R.layout.item_spinner_dropdown) // dropdown with black text
            muscleGroupSpinner.adapter = spinnerAdapter

        }



        return view
    }

    private fun setSpinnerPlaceholder() {
        val defaultAdapter = ArrayAdapter(
            requireContext(),
            R.layout.item_spinner_selected, // for main view
            listOf("Exercise")
        )
        defaultAdapter.setDropDownViewResource(R.layout.item_spinner_dropdown) // for dropdown list
        muscleGroupSpinner.adapter = defaultAdapter
    }


}
