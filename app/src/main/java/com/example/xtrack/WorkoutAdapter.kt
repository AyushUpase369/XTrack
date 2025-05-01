package com.example.xtrack

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class WorkoutAdapter(
    private var workoutList: MutableList<Workout>,
    private val onDeleteClick: (Workout) -> Unit
) : RecyclerView.Adapter<WorkoutAdapter.WorkoutViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): WorkoutViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_workout_card, parent, false)
        return WorkoutViewHolder(view)
    }
    fun updateData(newList: List<Workout>) {
        workoutList = newList.toMutableList()
        notifyDataSetChanged()
    }

    override fun onBindViewHolder(holder: WorkoutViewHolder, position: Int) {
        if (workoutList.isEmpty()) {
            holder.bindNoData()
            holder.itemView.findViewById<ImageView>(R.id.DeleteWorkout).apply {
                visibility = View.INVISIBLE // or View.GONE to completely remove it from layout
            }
        } else {
            val workout = workoutList[position]
            holder.bind(workout)
            holder.bind(workoutList[position])
            holder.itemView.findViewById<ImageView>(R.id.DeleteWorkout).apply {
                visibility = View.VISIBLE // Make sure it's visible when there is workout data
            }

            holder.itemView.findViewById<ImageView>(R.id.DeleteWorkout).setOnClickListener {
                Log.d("hii", "Run......")
                onDeleteClick(workout)
            }
        }
    }

    override fun getItemCount(): Int {
        return if (workoutList.isEmpty()) 1 else workoutList.size
    }

    class WorkoutViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val tvExercise: TextView = itemView.findViewById(R.id.tvExercise)
        private val tvSubexercise: TextView = itemView.findViewById(R.id.tvSubexercise)
        private val tvSets: TextView = itemView.findViewById(R.id.tvSets)
        private val tvReps: TextView = itemView.findViewById(R.id.tvReps)
        private val tvWeights: TextView = itemView.findViewById(R.id.tvWeights)

        fun bind(workout: Workout) {
            tvExercise.text = "${workout.exercise}"
            tvSubexercise.text = "${workout.subexercise}"
            tvSets.text = "Sets: ${workout.sets}"
            tvReps.text = "Reps: ${workout.reps}"
            tvWeights.text = "Weights: ${workout.weights}"
        }

        fun bindNoData() {
            tvExercise.text = "No workout data available"
            tvSubexercise.text = ""
            tvSets.text = ""
            tvReps.text = ""
            tvWeights.text = ""
        }
    }
}
