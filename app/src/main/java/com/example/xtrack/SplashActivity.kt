package com.example.xtrack

import android.annotation.SuppressLint
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import android.view.animation.AnimationUtils
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import com.airbnb.lottie.LottieAnimationView
import java.io.File

import com.bumptech.glide.Glide

class SplashActivity : AppCompatActivity() {
    @SuppressLint("CutPasteId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)

        overridePendingTransition(0, 0)
        // Make status bar and bottom buttons transparent
        window.decorView.systemUiVisibility = (
                View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                        or View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                )
        window.statusBarColor = Color.TRANSPARENT
        window.navigationBarColor = Color.TRANSPARENT

        val lottieView = findViewById<LottieAnimationView>(R.id.lottie_animation)
        lottieView.playAnimation()

        Handler(Looper.getMainLooper()).postDelayed({
            checkUserDataAndNavigate()
        }, 3500) // 5 seconds delay
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
            overridePendingTransition(0, 0)
        } else {
            startActivity(Intent(this, AboutYouActivity::class.java))
        }
        finish()
    }
}
