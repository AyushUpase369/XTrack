package com.example.xtrack

import android.content.Intent
import android.graphics.Color
import android.graphics.LinearGradient
import android.graphics.Shader
import android.os.Bundle
import android.view.View
import android.view.animation.ScaleAnimation
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import java.io.File

class AboutYouActivity : AppCompatActivity() {
    private lateinit var maleLayout: LinearLayout
    private lateinit var femaleLayout: LinearLayout
    private lateinit var maleIcon: ImageView
    private lateinit var femaleIcon: ImageView
    private lateinit var maleText: TextView
    private lateinit var femaleText: TextView
    private lateinit var btnConfirm: Button
    private lateinit var name: EditText

    private var selectedGender: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_aboutyou)

        // Make status bar and bottom buttons transparent
        window.decorView.systemUiVisibility = (
                View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                        or View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                )
        window.statusBarColor = android.graphics.Color.TRANSPARENT
        window.navigationBarColor = android.graphics.Color.TRANSPARENT

        maleLayout = findViewById(R.id.maleLayout)
        femaleLayout = findViewById(R.id.femaleLayout)
        maleIcon = findViewById(R.id.maleIcon)
        femaleIcon = findViewById(R.id.femaleIcon)
        maleText = findViewById(R.id.maleText)
        femaleText = findViewById(R.id.femaleText)
        btnConfirm = findViewById(R.id.btnConfirm)
        name = findViewById(R.id.name)

        // Gender selection
        maleLayout.setOnClickListener {
            selectGender("Male")
        }

        femaleLayout.setOnClickListener {
            selectGender("Female")
        }

        //  Listen to name input changes
        name.addTextChangedListener(object : android.text.TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                updateConfirmButton()
            }
            override fun afterTextChanged(s: android.text.Editable?) {}
        })

        btnConfirm.setOnClickListener {
            // Save user details
            val userData = "${name.text}\n$selectedGender"
            val userDir = File(getExternalFilesDir(null), "XTrack")
            if (!userDir.exists()) userDir.mkdirs()
            val userFile = File(userDir, "user_details.txt")
            userFile.writeText(userData)

            // Navigate to MainActivity
            startActivity(Intent(this, MainActivity::class.java)
                .putExtra("name", name.text.toString())
                .putExtra("gender", selectedGender))
            overridePendingTransition(R.anim.fade_in, R.anim.fade_out)
            finish()
        }



    }

    private fun selectGender(gender: String) {
        selectedGender = gender
        resetIconSize()
        resetTextColor()
        resetBorderColor()

        if (gender == "Male") {
            zoomInAnimation(maleIcon)
            maleText.setTextColor(Color.WHITE)


            val startColor = resources.getColor(R.color.gradient_start, theme)
            val endColor = resources.getColor(R.color.gradient_end, theme)
            applyGradientBorder(maleLayout, startColor, endColor) // Apply border

        } else {
            zoomInAnimation(femaleIcon)
            femaleText.setTextColor(Color.WHITE)


            val startColor = resources.getColor(R.color.gradient_start_female, theme)
            val endColor = resources.getColor(R.color.gradient_end_female, theme)

            applyGradientBorder(femaleLayout, startColor, endColor) // Apply border
        }

        updateConfirmButton()

    }



    private fun applyGradientBorder(view: View, startColor: Int, endColor: Int) {
        val gradientDrawable = android.graphics.drawable.GradientDrawable(
            android.graphics.drawable.GradientDrawable.Orientation.LEFT_RIGHT,
            intArrayOf(startColor, endColor)
        )
        gradientDrawable.setStroke(6, startColor) // Set stroke width and color
        gradientDrawable.cornerRadius = 24f
        gradientDrawable.setColor(Color.TRANSPARENT) // Keep inside transparent

        view.background = gradientDrawable
    }


    private fun resetTextColor() {
        maleText.setTextColor(resources.getColor(R.color.gray))
        femaleText.setTextColor(resources.getColor(R.color.gray))
    }


    private fun resetBorderColor() {
        maleLayout.setBackgroundResource(R.drawable.border_gray)
        femaleLayout.setBackgroundResource(R.drawable.border_gray)
    }


    private fun resetIconSize() {
        maleIcon.animate().scaleX(1.0f).scaleY(1.0f).setDuration(200).start()
        femaleIcon.animate().scaleX(1.0f).scaleY(1.0f).setDuration(200).start()
    }


    private fun zoomInAnimation(view: View) {
        view.animate().scaleX(1.3f).scaleY(1.3f).setDuration(200).start()
    }


    private fun updateConfirmButton() {
        val isNameNotEmpty = name.text.toString().trim().isNotEmpty()

        if (selectedGender != null && isNameNotEmpty) {
            btnConfirm.isEnabled = true
            btnConfirm.setBackgroundResource(R.drawable.btn_enabled)
        } else {
            btnConfirm.isEnabled = false
            btnConfirm.setBackgroundResource(R.drawable.btn_disabled)
        }
    }

}
