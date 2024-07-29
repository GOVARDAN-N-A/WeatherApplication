package com.example.weather
//17dea7edca8808ca56672ef5bf7f70e9
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide


import android.widget.ImageView

class SplashActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_splash)

        // Initialize the ImageView
        val loadingGif: ImageView = findViewById(R.id.loading_gif)

        // Load the GIF using Glide
        Glide.with(this)
            .asGif()
            .load(R.drawable.loader) // Your GIF resource
            .into(loadingGif)

        // Start LoginActivity after 3 seconds
        Handler(Looper.getMainLooper()).postDelayed({
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
            finish()
        }, 10000)
    }
}
