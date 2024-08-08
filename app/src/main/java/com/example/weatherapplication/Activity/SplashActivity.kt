// SplashActivity.kt
package com.example.weatherapplication

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.WindowManager
import androidx.appcompat.app.AppCompatActivity
import com.example.weather.LoginActivity
import com.example.weatherapplication.Activity.WeatherActivity

class SplashActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)
        window.setFlags(
            WindowManager.LayoutParams.FLAG_FULLSCREEN,
            WindowManager.LayoutParams.FLAG_FULLSCREEN
        )


        val serviceIntent = Intent(this, BackgroundService::class.java)
        startService(serviceIntent)

        Handler(Looper.getMainLooper()).postDelayed({
            val sharedPreferences = getSharedPreferences("User", Context.MODE_PRIVATE)
            val isLoggedIn = sharedPreferences.getBoolean("isLoggedIn", false)

            val intent = if (isLoggedIn) {
                Intent(this, WeatherActivity::class.java)
            } else {
                Intent(this, LoginActivity::class.java)
            }
            startActivity(intent)
            finish()
        }, 3000)
    }
}
