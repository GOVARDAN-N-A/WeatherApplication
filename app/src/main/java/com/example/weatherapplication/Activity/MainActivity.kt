package com.example.weatherapplication.Activity

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.MenuInflater
import android.view.View
import android.widget.Button
import android.widget.PopupMenu
import androidx.appcompat.app.AppCompatActivity
import com.example.weather.LoginActivity
import com.example.weatherapplication.R

class MainActivity : AppCompatActivity() {
    private lateinit var logoutButton: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_weather)

        logoutButton = findViewById(R.id.logout_button)
        val sharedPreferences = getSharedPreferences("User", Context.MODE_PRIVATE)

        logoutButton.setOnClickListener { view ->
            performLogout()
        }
    }


    private fun performLogout() {
        val sharedPreferences = getSharedPreferences("User", Context.MODE_PRIVATE)
        with(sharedPreferences.edit()) {
            putBoolean("isLoggedIn", false)
            apply()
        }
        val intent = Intent(this, LoginActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
        finish()
    }
}
