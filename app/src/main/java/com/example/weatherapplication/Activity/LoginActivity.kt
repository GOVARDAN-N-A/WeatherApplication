package com.example.weather

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Patterns
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import com.example.navbar_fragment.SignupActivity
import com.example.weatherapplication.Activity.WeatherActivity
import com.example.weatherapplication.R
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout

class LoginActivity : AppCompatActivity() {
    private lateinit var emailEditText: TextInputEditText
    private lateinit var passwordEditText: TextInputEditText
    private lateinit var loginButton: Button
    private lateinit var forgotPasswordButton: Button
    private lateinit var signupButton: Button
    private lateinit var emailLayout: TextInputLayout
    private lateinit var passwordLayout: TextInputLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        // Initialize views
        emailEditText = findViewById(R.id.email_edit_text)
        passwordEditText = findViewById(R.id.password_edit_text)
        loginButton = findViewById(R.id.login_button)
        forgotPasswordButton = findViewById(R.id.forgot_password)
        signupButton = findViewById(R.id.signup_button)

        // Initialize TextInputLayout
        emailLayout = findViewById(R.id.email_layout)
        passwordLayout = findViewById(R.id.password_layout)

        // Initialize SharedPreferences
        val sharedPreferences = getSharedPreferences("User", Context.MODE_PRIVATE)
        val editor = sharedPreferences.edit()

        // Check if the user is already logged in
        val isLoggedIn = sharedPreferences.getBoolean("isLoggedIn", false)
        if (isLoggedIn) {
            val intent = Intent(this, WeatherActivity::class.java)
            startActivity(intent)
            finish()
        }

        // Set click listener for login button
        loginButton.setOnClickListener {
            val email = emailEditText.text.toString().trim()
            val password = passwordEditText.text.toString().trim()

            // Reset errors
            emailLayout.error = null
            passwordLayout.error = null

            // Validate email and password
            var isValid = true
            if (email.isEmpty()) {
                emailLayout.error = "Email cannot be empty"
                isValid = false
            } else if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                emailLayout.error = "Enter a valid email"
                isValid = false
            }
            if (password.isEmpty()) {
                passwordLayout.error = "Password cannot be empty"
                isValid = false
            }

            if (isValid) {
                // Retrieve stored email and password from SharedPreferences
                val storedEmail = sharedPreferences.getString("email", null)
                val storedPassword = sharedPreferences.getString("password", null)

                // Check if the entered email matches the stored email
                if (email == storedEmail) {
                    // Check if the password matches the stored password
                    if (password == storedPassword) {
                        // Save login state
                        editor.putBoolean("isLoggedIn", true)
                        editor.apply()

                        // Proceed with login
                        val intent = Intent(this, WeatherActivity::class.java)
                        startActivity(intent)
                        finish()
                    } else {
                        passwordLayout.error = "Incorrect password"
                    }
                } else {
                    emailLayout.error = "Email does not match"
                }
            }
        }

        // Set click listener for forgot password button
//        forgotPasswordButton.setOnClickListener {
//            val intent = Intent(this, ForgotActivity::class.java)
//            startActivity(intent)
//        }

        // Set click listener for signup button
        signupButton.setOnClickListener {
            val intent = Intent(this, SignupActivity::class.java)
            startActivity(intent)
        }

        // Optionally, retrieve and display stored email (not password) for convenience
        val savedEmail = sharedPreferences.getString("email", "")
        emailEditText.setText(savedEmail)
    }
}
