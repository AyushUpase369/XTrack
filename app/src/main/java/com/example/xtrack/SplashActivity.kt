package com.example.xtrack

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import android.view.animation.AnimationUtils
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import java.io.File

class SplashActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)

        // Make status bar and bottom buttons transparent
        window.decorView.systemUiVisibility = (
                View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                        or View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                )
        window.statusBarColor = Color.TRANSPARENT
        window.navigationBarColor = Color.TRANSPARENT

        val logo = findViewById<ImageView>(R.id.splash_logo)

        // Load animations
        val fadeIn = AnimationUtils.loadAnimation(this, R.anim.fade_in)
        val fadeOut = AnimationUtils.loadAnimation(this, R.anim.fade_out)

        // Fade in logo
        logo.startAnimation(fadeIn)

        Handler(Looper.getMainLooper()).postDelayed({
            checkUserDataAndNavigate()
        }, 3000) // 5 seconds delay
    }

    private fun checkUserDataAndNavigate() {
        val userFile = File(getExternalFilesDir(null), "XTrack/user_details.txt")

        if (userFile.exists() && userFile.readText().isNotBlank()) {
            val data = userFile.readText().split("\n")
            val name = data.getOrNull(0) ?: "User"
            val gender = data.getOrNull(1) ?: "Male"

            val intent = Intent(this, MainActivity::class.java)
            intent.putExtra("name", name)
            intent.putExtra("gender", gender)
            startActivity(intent)
        } else {
            startActivity(Intent(this, AboutYouActivity::class.java))
        }

        overridePendingTransition(R.anim.fade_in, R.anim.fade_out)
        finish()
    }
}
