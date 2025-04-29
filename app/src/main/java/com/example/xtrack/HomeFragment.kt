package com.example.xtrack

import android.content.Intent
import android.graphics.Typeface
import android.os.Bundle
import android.util.Log
import android.view.*
import android.widget.*
import androidx.cardview.widget.CardView
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat
import androidx.fragment.app.Fragment
import com.example.xtrack.WorkoutFragment
import com.google.android.material.floatingactionbutton.FloatingActionButton
import java.text.SimpleDateFormat
import java.util.*

class HomeFragment : Fragment() {

    private lateinit var textusername: TextView
    private lateinit var iconperson_emoji: ImageView
    private lateinit var greetingTextView: TextView
    private lateinit var greetingIcon: ImageView
    private lateinit var monthYearTextView: TextView
    private lateinit var weekDaysLayout: LinearLayout
    private lateinit var addworkout: CardView
    private lateinit var absWorkoutCard: CardView
    private lateinit var chestWorkoutCard: CardView
    private lateinit var backWorkoutCard: CardView
    private lateinit var legsWorkoutCard: CardView
    private lateinit var armsWorkoutCard: CardView
    private lateinit var shoulderWorkoutCard: CardView

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_home, container, false)

        // Bind views
        addworkout = view.findViewById(R.id.challenge_card)
        textusername = view.findViewById(R.id.username_tv)
        iconperson_emoji = view.findViewById(R.id.person_emoji_iv)
        monthYearTextView = view.findViewById(R.id.month_year)
        weekDaysLayout = view.findViewById(R.id.week_days)
        absWorkoutCard = view.findViewById(R.id.abs_workout)
        chestWorkoutCard = view.findViewById(R.id.chest_workout)
        backWorkoutCard = view.findViewById(R.id.back_workout)
        legsWorkoutCard = view.findViewById(R.id.legs_workout)
        armsWorkoutCard = view.findViewById(R.id.arms_workout)
        shoulderWorkoutCard = view.findViewById(R.id.shoulders_workout)

//        greetingTextView = view.findViewById(R.id.greeting_tv)
//        greetingIcon = view.findViewById(R.id.greeting_icon)


        // Set username and gender-based icon
        val name = activity?.intent?.getStringExtra("name") ?: "User"
        val gender = activity?.intent?.getStringExtra("gender") ?: "Male"
        val namehi = "HI  " + name
        textusername.text = namehi
        iconperson_emoji.setBackgroundResource(
            if (gender == "Male") R.drawable.home_male_icon else R.drawable.home_female_icon
        )

//        // Set greeting based on time
        val calendar = Calendar.getInstance()
//        val hour = calendar.get(Calendar.HOUR_OF_DAY)
//        val (greetingText, iconRes) = when (hour) {
//            in 5..11 -> "Good Morning" to R.drawable.ic_morning
//            in 12..16 -> "Good Afternoon" to R.drawable.ic_afternoon
//            in 17..20 -> "Good Evening" to R.drawable.ic_evening
//            else -> "Good Night" to R.drawable.ic_night
//        }
//        greetingTextView.text = greetingText
//        greetingIcon.setImageResource(iconRes)
//
        // Set current month and year
        val sdf = SimpleDateFormat("MMMM yyyy", Locale.getDefault())
        monthYearTextView.text = sdf.format(calendar.time)

        // Populate current week days and dates
        updateCurrentWeek(calendar, view)

        addworkout.setOnClickListener {
            loadFragment(AddWorkoutFragment())
        }

        absWorkoutCard.setOnClickListener {
            val videoUrl = "https://youtu.be/IGYQSTFnjzE"
            val intent = Intent(Intent.ACTION_VIEW, android.net.Uri.parse(videoUrl))
            startActivity(intent)
        }

        chestWorkoutCard.setOnClickListener {
            val videoUrl = "https://youtu.be/nc2WOycihQo"
            val intent = Intent(Intent.ACTION_VIEW, android.net.Uri.parse(videoUrl))
            startActivity(intent)
        }

        backWorkoutCard.setOnClickListener {
            val videoUrl = "https://youtu.be/dITxM0MvVjM"
            val intent = Intent(Intent.ACTION_VIEW, android.net.Uri.parse(videoUrl))
            startActivity(intent)
        }

        legsWorkoutCard.setOnClickListener {
            val videoUrl = "https://youtu.be/4ynEJnNXoz4"
            val intent = Intent(Intent.ACTION_VIEW, android.net.Uri.parse(videoUrl))
            startActivity(intent)
        }

        armsWorkoutCard.setOnClickListener {
            val videoUrl = "https://youtu.be/zBnteJi_srs"
            val intent = Intent(Intent.ACTION_VIEW, android.net.Uri.parse(videoUrl))
            startActivity(intent)
        }

        shoulderWorkoutCard.setOnClickListener {
            val videoUrl = "https://youtu.be/fHjC-RVbYQY"
            val intent = Intent(Intent.ACTION_VIEW, android.net.Uri.parse(videoUrl))
            startActivity(intent)
        }

         return view
    }

    private fun loadFragment(fragment: Fragment) {
        parentFragmentManager.beginTransaction()
            .replace(R.id.fragment_container, fragment)
            .commit()
    }


    private fun updateCurrentWeek(calendar: Calendar, view: View) {

        Log.d("HomeFragment", "updateCurrentWeek called")

        val today = Calendar.getInstance()
        val dayFormat = SimpleDateFormat("EEE", Locale.getDefault())
        val dateFormat = SimpleDateFormat("d", Locale.getDefault())
        val orbitronFont = ResourcesCompat.getFont(requireContext(), R.font.orbitron_regular)
        val orbitronFontBold = ResourcesCompat.getFont(requireContext(), R.font.orbitron_extrabold)

        val dayIds = listOf(
            R.id.monday_tv,
            R.id.tuesday_tv,
            R.id.wednesday_tv,
            R.id.thursday_tv,
            R.id.friday_tv,
            R.id.saturday_tv,
            R.id.sunday_tv
        )

        val dateIds = listOf(
            R.id.mondaydate_tv,
            R.id.tuesdaydate_tv,
            R.id.wednesdaydate_tv,
            R.id.thursdaydate_tv,
            R.id.fridaydate_tv,
            R.id.saturdaydate_tv,
            R.id.sundaydate_tv
        )

        // Move to start of the week (Sunday)
        val dayOfWeek = calendar.get(Calendar.DAY_OF_WEEK)
        val startOffset = if (dayOfWeek == Calendar.SUNDAY) 0 else Calendar.SUNDAY - dayOfWeek
        calendar.add(Calendar.DAY_OF_MONTH, startOffset)

        for (i in 0..6) {
            val dayChar = dayFormat.format(calendar.time)[0].uppercaseChar().toString()
            val date = dateFormat.format(calendar.time)

            val dayTextView = view.findViewById<TextView>(dayIds[i])
            val dateTextView = view.findViewById<TextView>(dateIds[i])

            // Set day (S,M,...) text
            dayTextView?.apply {
                text = dayChar
                visibility = View.VISIBLE
                typeface = orbitronFont
                setPadding(0, 16, 0, 16) // Nice vertical padding
                setTextColor(ContextCompat.getColor(requireContext(), android.R.color.white))
            }

            // Set date (8, 9...) text
            dateTextView?.apply {
                text = date
                visibility = View.VISIBLE
                typeface = orbitronFont
                setPadding(0, 16, 0, 16) // Nice vertical padding
                setTextColor(ContextCompat.getColor(requireContext(), android.R.color.white))
                background = null
            }

            // Highlight if today
            if (calendar.get(Calendar.DAY_OF_MONTH) == today.get(Calendar.DAY_OF_MONTH) &&
                calendar.get(Calendar.MONTH) == today.get(Calendar.MONTH) &&
                calendar.get(Calendar.YEAR) == today.get(Calendar.YEAR)
            ) {
                val yellowColor = ContextCompat.getColor(requireContext(), R.color.gradient_start)
                dayTextView.setTextColor(yellowColor)
                dateTextView.setTextColor(yellowColor)

                // Bold with custom font
                dayTextView.typeface = Typeface.create(orbitronFontBold, Typeface.BOLD)
                dateTextView.typeface = Typeface.create(orbitronFontBold, Typeface.BOLD)
            }

            calendar.add(Calendar.DAY_OF_MONTH, 1)
        }
    }



}

