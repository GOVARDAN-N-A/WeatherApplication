package com.example.weatherapplication

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.appcompat.app.AppCompatActivity
import com.example.weather.LoginActivity
import com.example.weatherapplication.Activity.WeatherActivity

class SplashActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)

        // Delay for 2 seconds to display the splash screen
        Handler(Looper.getMainLooper()).postDelayed({
            // Check login status
            val sharedPreferences = getSharedPreferences("User", Context.MODE_PRIVATE)
            val isLoggedIn = sharedPreferences.getBoolean("isLoggedIn", false)

            if (isLoggedIn) {
                // Navigate to WeatherActivity if logged in
                val intent = Intent(this, WeatherActivity::class.java)
                startActivity(intent)
            } else {
                // Navigate to LoginActivity if not logged in
                val intent = Intent(this, LoginActivity::class.java)
                startActivity(intent)
            }
            // Close SplashActivity
            finish()
        }, 2000) // 2000 milliseconds delay
    }
}
