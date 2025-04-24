        package com.example.xtrack

        import android.annotation.SuppressLint
        import android.content.Context.MODE_APPEND
        import android.os.Bundle
        import android.text.Editable
        import android.text.InputType
        import android.text.TextWatcher
        import android.view.Gravity
        import android.view.LayoutInflater
        import android.view.View
        import android.view.ViewGroup
        import android.widget.*
        import androidx.fragment.app.Fragment
        import java.io.BufferedReader
        import java.io.InputStreamReader
        import java.text.SimpleDateFormat
        import java.util.Calendar
        import java.util.Date
        import java.util.Locale
        import android.content.Intent
        import android.widget.AdapterView
        import android.widget.ArrayAdapter
        import android.widget.Button
        import android.widget.EditText
        import android.widget.LinearLayout
        import android.widget.Spinner
        import android.widget.Toast
        import androidx.appcompat.app.AppCompatActivity
        import androidx.recyclerview.widget.LinearLayoutManager
        import androidx.recyclerview.widget.RecyclerView
        import java.io.FileOutputStream

        class AddWorkoutFragment : Fragment() {

            private lateinit var exerciseCategorySpinner: Spinner
            private lateinit var exerciseSpinner: Spinner
            private lateinit var workoutAdapter: WorkoutAdapter
            private lateinit var inputSets: EditText
            private lateinit var addWorkoutButton: Button
            private lateinit var analysisButton: Button
            private lateinit var datepicker: Button
            private lateinit var repsContainer: LinearLayout
            private val workoutList = mutableListOf<Workout>()
            private val filteredList = mutableListOf<Workout>()
            private val exerciseCategories = arrayOf(
                "Chest Exercises", "Back Exercises", "Shoulder Exercises", "Biceps Exercises",
                "Triceps Exercises", "Leg Exercises", "Core & Abs Exercises", "Cardio & Functional Exercises"
            )

            private val subExercises = mapOf(
                "Chest Exercises" to arrayOf(
                    "Barbell Bench Press (Flat)", "Barbell Bench Press (Incline)", "Barbell Bench Press (Decline)",
                    "Dumbbell Bench Press (Flat)", "Dumbbell Bench Press (Incline)", "Dumbbell Bench Press (Decline)",
                    "Machine Chest Press (Seated)", "Machine Chest Press (Incline)", "Machine Chest Press (Decline)",
                    "Smith Machine Bench Press", "Push-Ups (Standard)", "Push-Ups (Wide)", "Push-Ups (Diamond)",
                    "Push-Ups (Plyometric)", "Push-Ups (Weighted)", "Dumbbell Chest Fly (Flat)",
                    "Dumbbell Chest Fly (Incline)", "Dumbbell Chest Fly (Decline)", "Cable Chest Fly (High to Low)",
                    "Cable Chest Fly (Low to High)", "Cable Chest Fly (Middle)", "Machine Pec Deck Fly",
                    "Chest Dips", "Parallel Bar Dips"
                ),
                "Back Exercises" to arrayOf(
                    "Pull-Ups (Standard)", "Pull-Ups (Wide-Grip)", "Pull-Ups (Close-Grip)", "Pull-Ups (Neutral-Grip)",
                    "Pull-Ups (Weighted)", "Chin-Ups", "Lat Pulldown (Wide-Grip)", "Lat Pulldown (Close-Grip)",
                    "Lat Pulldown (Neutral-Grip)", "Lat Pulldown (Reverse-Grip)", "Straight-Arm Pulldown",
                    "Barbell Bent-Over Row (Overhand)", "Barbell Bent-Over Row (Underhand)",
                    "Barbell Bent-Over Row (Wide-Grip)", "Dumbbell Bent-Over Row (Single-Arm)",
                    "Dumbbell Bent-Over Row (Double-Arm)", "T-Bar Row", "Seated Cable Row (Wide-Grip)",
                    "Seated Cable Row (Close-Grip)", "Seated Cable Row (Neutral-Grip)", "Chest-Supported Machine Row",
                    "Conventional Deadlift", "Sumo Deadlift", "Romanian Deadlift", "Trap Bar Deadlift", "Good Mornings"
                ),
                "Shoulder Exercises" to arrayOf(
                    "Barbell Overhead Press (Standing)", "Barbell Overhead Press (Seated)", "Dumbbell Overhead Press (Standing)",
                    "Dumbbell Overhead Press (Seated)", "Arnold Press", "Machine Shoulder Press",
                    "Lateral Raises (Dumbbell)", "Lateral Raises (Cable)", "Lateral Raises (Machine)",
                    "Front Raises (Dumbbell)", "Front Raises (Barbell)", "Front Raises (Cable)",
                    "Rear Delt Fly (Dumbbell)", "Rear Delt Fly (Cable)", "Rear Delt Fly (Machine)",
                    "Barbell Shrugs", "Dumbbell Shrugs", "Trap Bar Shrugs"
                ),
                "Biceps Exercises" to arrayOf(
                    "Barbell Curl (Standard)", "Barbell Curl (EZ-Bar)", "Dumbbell Curl (Alternating)",
                    "Dumbbell Curl (Seated)", "Dumbbell Curl (Standing)", "Hammer Curl", "Preacher Curl (Barbell)",
                    "Preacher Curl (Dumbbell)", "Concentration Curl", "Cable Biceps Curl"
                ),
                "Triceps Exercises" to arrayOf(
                    "Close-Grip Bench Press", "Skull Crushers (Barbell)", "Skull Crushers (Dumbbell)", "Skull Crushers (EZ-Bar)",
                    "Triceps Dips", "Triceps Pushdown (Rope)", "Triceps Pushdown (Bar)", "Triceps Pushdown (V-Bar)",
                    "Overhead Triceps Extension (Dumbbell)", "Overhead Triceps Extension (Cable)", "Triceps Kickbacks"
                ),
                "Leg Exercises" to arrayOf(
                    "Barbell Squat (Back)", "Barbell Squat (Front)", "Goblet Squat", "Bulgarian Split Squat",
                    "Leg Press", "Lunges (Walking)", "Lunges (Stationary)", "Lunges (Reverse)", "Step-Ups",
                    "Romanian Deadlift", "Stiff-Legged Deadlift", "Leg Curl (Seated)", "Leg Curl (Lying)",
                    "Leg Extension", "Calf Raises (Seated)", "Calf Raises (Standing)"
                ),
                "Core & Abs Exercises" to arrayOf(
                    "Planks (Standard)", "Planks (Side)", "Planks (Weighted)", "Hanging Leg Raises", "Bicycle Crunches",
                    "Russian Twists", "Sit-Ups", "Decline Sit-Ups", "Ab Rollouts", "Cable Woodchoppers"
                ),
                "Cardio & Functional Exercises" to arrayOf(
                    "Jump Rope", "Burpees", "Box Jumps", "Battle Ropes", "Kettlebell Swings",
                    "Rowing Machine", "Treadmill Sprints", "Stair Climber"
                )
            )

            override fun onCreateView(
                inflater: LayoutInflater, container: ViewGroup?,
                savedInstanceState: Bundle?
            ): View? {
                val view = inflater.inflate(R.layout.fragment_add_workout, container, false)

                // Initialize views
                exerciseCategorySpinner = view.findViewById(R.id.exerciseCategorySpinner)
                exerciseSpinner = view.findViewById(R.id.exerciseSpinner)
                inputSets = view.findViewById(R.id.inputSets)
                addWorkoutButton = view.findViewById(R.id.addWorkoutButton)
                analysisButton = view.findViewById(R.id.analysisButton)
                datepicker = view.findViewById(R.id.datePicker)
                repsContainer = view.findViewById(R.id.repsContainer)

                // Add a TextWatcher to listen for changes in sets input
                inputSets.addTextChangedListener(object : TextWatcher {
                    override fun afterTextChanged(s: Editable?) {
                        updateRepsFields()
                        val setCount = s.toString().toIntOrNull() ?: 0  // Ensure it's a valid integer
                        generateRepsAndWeightInputs(setCount) // ✅ Correct usage here
                    }

                    override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
                    override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
                })

                val exerciseAdapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_dropdown_item, exerciseCategories)
                exerciseSpinner.adapter = exerciseAdapter

                loadWorkoutData()

                val categoryAdapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_dropdown_item, exerciseCategories)
                exerciseCategorySpinner.adapter = categoryAdapter

                exerciseCategorySpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                    override fun onItemSelected(parent: AdapterView<*>, view: android.view.View?, position: Int, id: Long) {
                        updateSubExerciseSpinner(exerciseCategories[position])
                    }
                    override fun onNothingSelected(parent: AdapterView<*>) {}
                }

                addWorkoutButton.setOnClickListener {
                    saveWorkoutData()
                }

                return view
            }

            private fun updateRepsFields() {
                repsContainer.removeAllViews() // Clear existing views

                val setsText = inputSets.text.toString()
                val setsCount = setsText.toIntOrNull() ?: 0

                for (i in 1..setsCount) {
                    val repsEditText = EditText(requireContext()).apply {
                        hint = "Reps for Set $i"
                        layoutParams = LinearLayout.LayoutParams(
                            LinearLayout.LayoutParams.MATCH_PARENT,
                            LinearLayout.LayoutParams.WRAP_CONTENT
                        )
                    }

                    val weightEditText = EditText(requireContext()).apply {
                        hint = "Weight for Set $i"
                        layoutParams = LinearLayout.LayoutParams(
                            LinearLayout.LayoutParams.MATCH_PARENT,
                            LinearLayout.LayoutParams.WRAP_CONTENT
                        )
                        inputType = android.text.InputType.TYPE_CLASS_NUMBER
                    }

                    repsContainer.addView(repsEditText)
                    repsContainer.addView(weightEditText)
                }
            }


            private fun loadWorkoutData() {
                try {
                    val inputStream = requireContext().openFileInput("workout_log.txt")

                    val reader = BufferedReader(InputStreamReader(inputStream))
                    var line: String?

                    while (reader.readLine().also { line = it } != null) {
                        val parts = line!!.split(", ")
                        if (parts.size == 6) {
                            workoutList.add(Workout(parts[0], parts[1], parts[2], parts[3], parts[4], parts[5]))
                        }
                    }
                    reader.close()
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }

            private fun generateRepsAndWeightInputs(setCount: Int) {
                repsContainer.removeAllViews() // Clear previous inputs

                for (i in 1..setCount) {
                    val rowLayout = LinearLayout(requireContext()).apply {
                        orientation = LinearLayout.HORIZONTAL
                        layoutParams = LinearLayout.LayoutParams(
                            LinearLayout.LayoutParams.MATCH_PARENT,
                            LinearLayout.LayoutParams.WRAP_CONTENT
                        ).apply {
                            setMargins(0, 0, 0, 12) // Add gap between rows
                        }
                    }

                    // Reps Input
                    val repsInput = EditText(requireContext()).apply {
                        hint = "Reps $i"
                        inputType = InputType.TYPE_CLASS_NUMBER
                        gravity = Gravity.CENTER // Centers text
                        textAlignment = View.TEXT_ALIGNMENT_CENTER
                        setBackgroundResource(R.drawable.input_field_background)
                        layoutParams = LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f).apply {
                            setMargins(0, 0, 8, 0) // Add right margin for spacing
                        }
                        setPadding(12, 12, 12, 12)
                    }

                    // Weight Input
                    val weightInput = EditText(requireContext()).apply {
                        hint = "Weight $i"
                        gravity = Gravity.CENTER // Centers text
                        textAlignment = View.TEXT_ALIGNMENT_CENTER
                        inputType = InputType.TYPE_CLASS_NUMBER or InputType.TYPE_NUMBER_FLAG_DECIMAL
                        setBackgroundResource(R.drawable.input_field_background)
                        layoutParams = LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f)
                        setPadding(12, 12, 12, 12)
                    }

                    rowLayout.addView(repsInput)
                    rowLayout.addView(weightInput)

                    repsContainer.addView(rowLayout)
                }
            }


            private fun updateSubExerciseSpinner(category: String) {
                val subExerciseList = subExercises[category] ?: emptyArray()
                val subExerciseAdapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_dropdown_item, subExerciseList)
                exerciseSpinner.adapter = subExerciseAdapter
            }

            private fun saveWorkoutData() {
                val setsValue = inputSets.text.toString().trim()

                // Check if Sets field is empty or invalid
                if (setsValue.isBlank() || setsValue.toIntOrNull() == null || setsValue.toInt() <= 0) {
                    Toast.makeText(requireContext(), "Please enter a valid Sets value!", Toast.LENGTH_SHORT).show()
                    return
                }

                val repsList = mutableListOf<String>()
                val weightsList = mutableListOf<String>()

                var isEmptyFieldFound = false  // Flag for detecting empty Reps/Weights

                // Collect Reps and Weights values
                for (i in 0 until repsContainer.childCount) {
                    val rowLayout = repsContainer.getChildAt(i) as LinearLayout
                    val repsInput = rowLayout.getChildAt(0) as EditText
                    val weightInput = rowLayout.getChildAt(1) as EditText

                    val repsValue = repsInput.text.toString().trim()
                    val weightValue = weightInput.text.toString().trim()

                    // Check for empty fields
                    if (repsValue.isBlank() || weightValue.isBlank()) {
                        isEmptyFieldFound = true
                        break
                    }

                    repsList.add(repsValue)
                    weightsList.add(weightValue)
                }

                // Show error if any field is empty
                if (isEmptyFieldFound) {
                    Toast.makeText(requireContext(), "Please enter all Reps and Weights details first!", Toast.LENGTH_SHORT).show()
                    return
                }

                // Save the workout
                val formattedReps = repsList.joinToString("|")
                val formattedWeights = weightsList.joinToString("|")

                val workout = Workout(
                    date = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date()),
                    exercise = exerciseCategorySpinner.selectedItem.toString(),
                    subexercise = exerciseSpinner.selectedItem.toString(),
                    sets = setsValue,
                    reps = formattedReps,
                    weights = formattedWeights
                )

                saveToFile(workout)

                // Show success message
                Toast.makeText(requireContext(), "Workout Added Successfully!", Toast.LENGTH_SHORT).show()

                // Clear all input fields
                inputSets.text.clear()
                repsContainer.removeAllViews()  // Clears dynamic Reps & Weights inputs
            }




            private fun saveToFile(workout: Workout) {
                try {
                    val fileOutputStream: FileOutputStream = requireContext().openFileOutput("workout_log.txt", MODE_APPEND)
                    val workoutLine = "${workout.date}, ${workout.exercise}, ${workout.subexercise}, ${workout.sets}, ${workout.reps}, ${workout.weights}\n"
                    fileOutputStream.write(workoutLine.toByteArray())
                    fileOutputStream.close()
                } catch (e: Exception) {
                    e.printStackTrace()
                    Toast.makeText(requireContext(), "Error saving workout!", Toast.LENGTH_SHORT).show()
                }
            }

        }
