package com.example.xtrack

import android.graphics.Color
import android.os.Bundle
import android.view.View
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.fragment.app.Fragment
import java.util.Calendar
import com.example.xtrack.HomeFragment
import com.example.xtrack.WorkoutFragment
import com.example.xtrack.StatsFragment
import com.example.xtrack.ProfileFragment


class MainActivity : AppCompatActivity() {

    private lateinit var textusername: TextView
    private lateinit var iconperson_emoji: ImageView
    private lateinit var navBar: LinearLayout
    private lateinit var navHome: LinearLayout
    private lateinit var navPlan: LinearLayout
    private lateinit var navStatistics: LinearLayout
    private lateinit var navProfile: LinearLayout

    private lateinit var homeText: TextView
    private lateinit var workoutText: TextView
    private lateinit var statsText: TextView
    private lateinit var profileText: TextView

    private lateinit var homeIcon: ImageView
    private lateinit var workoutIcon: ImageView
    private lateinit var statsIcon: ImageView
    private lateinit var profileIcon: ImageView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)


        // Make status bar and bottom buttons transparent
        window.decorView.systemUiVisibility = (
                View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                        or View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                )
        window.statusBarColor = android.graphics.Color.TRANSPARENT
        window.navigationBarColor = android.graphics.Color.TRANSPARENT

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }


        // Initializing navigation items
        navBar = findViewById(R.id.bottom_nav_bar)

        navHome = findViewById(R.id.nav_home)
        navPlan = findViewById(R.id.nav_workout)
        navStatistics = findViewById(R.id.nav_statistics)
        navProfile = findViewById(R.id.nav_profile)

        homeText = findViewById(R.id.home_text)
        workoutText = findViewById(R.id.workout_text)
        statsText = findViewById(R.id.stats_text)
        profileText = findViewById(R.id.profile_text)

        homeIcon = findViewById(R.id.home_icon)
        workoutIcon = findViewById(R.id.workout_icon)
        statsIcon = findViewById(R.id.stats_icon)
        profileIcon = findViewById(R.id.profile_icon)

        // Set click listeners
        navHome.setOnClickListener { setActiveTab(navHome, homeIcon, homeText) }
        navPlan.setOnClickListener { setActiveTab(navPlan, workoutIcon, workoutText) }
        navStatistics.setOnClickListener { setActiveTab(navStatistics, statsIcon, statsText) }
        navProfile.setOnClickListener { setActiveTab(navProfile, profileIcon, profileText) }

        // Set default selection
        setActiveTab(navHome, homeIcon, homeText)
        loadFragment(HomeFragment())

        navHome.setOnClickListener {
            setActiveTab(navHome, homeIcon, homeText)
            loadFragment(HomeFragment())
        }

        navPlan.setOnClickListener {
            setActiveTab(navPlan, workoutIcon, workoutText)
            loadFragment(WorkoutFragment())
        }

        navStatistics.setOnClickListener {
            setActiveTab(navStatistics, statsIcon, statsText)
            loadFragment(StatsFragment())
        }

        navProfile.setOnClickListener {
            setActiveTab(navProfile, profileIcon, profileText)
            loadFragment(ProfileFragment())
        }

    }



    private fun setActiveTab(selectedNav: LinearLayout, selectedIcon: ImageView, selectedText: TextView) {
        // Reset all items
        resetAllTabs()

        // Apply selected styling
        selectedNav.setBackgroundResource(R.drawable.selected_background) // Change to your drawable
        selectedIcon.setColorFilter(Color.BLACK) // Change icon color
        selectedText.setTextColor(Color.BLACK) // Change text color
        selectedText.visibility = View.VISIBLE // Show text
    }

    private fun resetAllTabs() {
        val navItems = listOf(navHome, navPlan, navStatistics, navProfile)
        val icons = listOf(homeIcon, workoutIcon, statsIcon, profileIcon)
        val texts = listOf(homeText, workoutText, statsText, profileText)

        for (i in navItems.indices) {
            navItems[i].setBackgroundResource(0) // Remove background
            icons[i].setColorFilter(Color.WHITE) // Default icon color
            texts[i].setTextColor(Color.WHITE) // Default text color
            texts[i].visibility = View.GONE // Hide text
        }
    }

    private fun loadFragment(fragment: Fragment) {
        supportFragmentManager.beginTransaction()
            .replace(R.id.fragment_container, fragment)
            .commit()
    }

}