package com.example.weather

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Patterns
import android.view.WindowManager
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import com.example.navbar_fragment.SignupActivity
import com.example.weatherapplication.Activity.ForgotActivity
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

    private val sharedPreferences by lazy {
        getSharedPreferences("User", Context.MODE_PRIVATE)
    }

    private val editor by lazy {
        sharedPreferences.edit()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        initializeViews()
        checkIfLoggedIn()
        setupListeners()
        displaySavedEmail()

        window.setFlags(
            WindowManager.LayoutParams.FLAG_FULLSCREEN,
            WindowManager.LayoutParams.FLAG_FULLSCREEN
        )
    }



    private fun initializeViews() {
        emailEditText = findViewById(R.id.email_edit_text)
        passwordEditText = findViewById(R.id.password_edit_text)
        loginButton = findViewById(R.id.login_button)
        forgotPasswordButton = findViewById(R.id.forgot_password)
        signupButton = findViewById(R.id.signup_button)

        emailLayout = findViewById(R.id.email_layout)
        passwordLayout = findViewById(R.id.password_layout)
    }

    private fun checkIfLoggedIn() {
        val isLoggedIn = sharedPreferences.getBoolean("isLoggedIn", false)
        if (isLoggedIn) {
            navigateToWeatherActivity()
        }
    }

    private fun setupListeners() {
        loginButton.setOnClickListener { handleLogin() }

         forgotPasswordButton.setOnClickListener {
             startActivity(Intent(this, ForgotActivity::class.java))
         }

        signupButton.setOnClickListener {
            startActivity(Intent(this, SignupActivity::class.java))
        }
    }

    private fun handleLogin() {
        val email = emailEditText.text.toString().trim()
        val password = passwordEditText.text.toString().trim()

        resetErrors()

        if (validateInputs(email, password)) {
            if (authenticateUser(email, password)) {
                editor.putBoolean("isLoggedIn", true)
                editor.apply()
                navigateToWeatherActivity()
            }
        }
    }

    private fun validateInputs(email: String, password: String): Boolean {
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

        return isValid
    }

    private fun authenticateUser(email: String, password: String): Boolean {
        val storedEmail = sharedPreferences.getString("email", null)
        val storedPassword = sharedPreferences.getString("password", null)

        return when {
            email != storedEmail -> {
                emailLayout.error = "Email does not match"
                false
            }
            password != storedPassword -> {
                passwordLayout.error = "Incorrect password"
                false
            }
            else -> true
        }
    }

    private fun resetErrors() {
        emailLayout.error = null
        passwordLayout.error = null
    }

    private fun navigateToWeatherActivity() {
        startActivity(Intent(this, WeatherActivity::class.java))
        finish()
    }

    private fun displaySavedEmail() {
        val savedEmail = sharedPreferences.getString("email", "")
        emailEditText.setText(savedEmail)
    }
}
