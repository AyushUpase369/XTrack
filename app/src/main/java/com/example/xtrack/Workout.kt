package com.example.xtrack

import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

data class Workout(
    val date: String,
    val exercise: String,
    val subexercise: String,
    val sets: String,
    val reps: String,
    val weights: String
) {
    fun getDateAsDate(): Date {
        val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        return dateFormat.parse(date) ?: Date()
    }
}


