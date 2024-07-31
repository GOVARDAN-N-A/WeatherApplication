package com.example.navbar_fragment

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import com.example.weather.LoginActivity
import com.example.weatherapplication.R
import com.example.weatherapplication.Activity.WeatherActivity
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout

class SignupActivity : AppCompatActivity() {

    lateinit var usernameLayout: TextInputLayout
    lateinit var passwordLayout: TextInputLayout
    lateinit var confirmPasswordLayout: TextInputLayout
    lateinit var emailLayout: TextInputLayout
    lateinit var cityLayout: TextInputLayout
    lateinit var editTextUsername: TextInputEditText
    lateinit var editTextPassword: TextInputEditText
    lateinit var editTextConfirmPassword: TextInputEditText
    lateinit var editTextEmail: TextInputEditText
    lateinit var editTextCity: TextInputEditText

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_signup)

        usernameLayout = findViewById(R.id.username_layout)
        passwordLayout = findViewById(R.id.password_layout)
        confirmPasswordLayout = findViewById(R.id.conform_pass_layout)
        emailLayout = findViewById(R.id.email_layout)
        cityLayout = findViewById(R.id.city_layout)
        editTextUsername = usernameLayout.findViewById(R.id.username_edit_text)
        editTextPassword = passwordLayout.findViewById(R.id.password_edit_text)
        editTextConfirmPassword = confirmPasswordLayout.findViewById(R.id.confirm_pass)
        editTextEmail = emailLayout.findViewById(R.id.e_mail)
        editTextCity = cityLayout.findViewById(R.id.city)

        val signupButton = findViewById<Button>(R.id.signup_button)
        val loginButton = findViewById<Button>(R.id.sign_in)

        editTextPassword.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(s: Editable?) {
                val password = s.toString().trim()
                validatePassword(password)
            }
        })

        signupButton.setOnClickListener {
            val username = editTextUsername.text.toString().trim()
            val password = editTextPassword.text.toString().trim()
            val confirmPassword = editTextConfirmPassword.text.toString().trim()
            val email = editTextEmail.text.toString().trim()
            val city = editTextCity.text.toString().trim()

            // Reset errors
            usernameLayout.error = null
            passwordLayout.error = null
            confirmPasswordLayout.error = null
            emailLayout.error = null
            cityLayout.error = null

            // Validate inputs
            var isValid = true

            if (username.isBlank()) {
                usernameLayout.error = "Please Enter Username"
                isValid = false
            }

            if (password.isEmpty()) {
                passwordLayout.error = "Please Enter Password"
                isValid = false
            }

            if (confirmPassword.isEmpty()) {
                confirmPasswordLayout.error = "Please Confirm Password"
                isValid = false
            } else if (password != confirmPassword) {
                confirmPasswordLayout.error = "Passwords do not match"
                isValid = false
            }

            if (email.isBlank() || !android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                emailLayout.error = "Please Enter a Valid Email"
                isValid = false
            }

            if (city.isBlank()) {
                cityLayout.error = "Please Enter City"
                isValid = false
            }

            if (isValid && passwordLayout.error == null) {
                // Save user data in SharedPreferences
                val sharedPreferences = getSharedPreferences("User", Context.MODE_PRIVATE)
                with(sharedPreferences.edit()) {
                    putString("username", username)
                    putString("password", password)
                    putString("email", email)
                    putBoolean("isLoggedIn", true) // Set login session flag
                    putString("city", city) // Set login session flag
                    apply()
                }

                // Show success message
                android.widget.Toast.makeText(this, "Signup successful", android.widget.Toast.LENGTH_SHORT).show()

                // Navigate to WeatherActivity after signup
                startActivity(Intent(this, WeatherActivity::class.java))
                finish()
            }
        }

        loginButton.setOnClickListener {
            startActivity(Intent(this, LoginActivity::class.java))
            finish()
        }
    }

    private fun validatePassword(password: String) {
        when {
            password.isEmpty() -> passwordLayout.error = "Password must not be empty"
            password.length < 6 -> passwordLayout.error = "Password must be at least 6 characters"
            !password.matches(Regex(".*[A-Z].*")) -> passwordLayout.error = "Password must contain at least one uppercase letter"
            !password.matches(Regex(".*[a-z].*")) -> passwordLayout.error = "Password must contain at least one lowercase letter"
            !password.matches(Regex(".*\\d.*")) -> passwordLayout.error = "Password must contain at least one digit"
            !password.matches(Regex(".*[!@#\$%^&*(),.?\":{}|<>].*")) -> passwordLayout.error = "Password must contain at least one special character"
            else -> passwordLayout.error = null
        }
    }
}
