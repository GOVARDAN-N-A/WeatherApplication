package com.example.weather

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity
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
        setContentView(R.layout.login_page)

        // Initialize views
        emailEditText = findViewById(R.id.username_edit_text)
        passwordEditText = findViewById(R.id.password_edit_text)
        loginButton = findViewById(R.id.login_button)
        forgotPasswordButton = findViewById(R.id.forgot_password)
        signupButton = findViewById(R.id.signup_button)

        // Initialize TextInputLayout
        emailLayout = findViewById(R.id.username_layout)
        passwordLayout = findViewById(R.id.password_layout)

        // Initialize SharedPreferences
        val sharedPreferences = getSharedPreferences("UserPrefs", Context.MODE_PRIVATE)

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
            }
            if (password.isEmpty()) {
                passwordLayout.error = "Password cannot be empty"
                isValid = false
            }

            if (isValid) {
                // Save email and password to SharedPreferences
                val editor = sharedPreferences.edit()
                editor.putString("email", email)
                editor.putString("password", password)
                editor.apply()

                // Proceed with login
                val intent = Intent(this, MainActivity::class.java)
                startActivity(intent)
                finish()
            }
        }

        // Set click listener for forgot password button
        forgotPasswordButton.setOnClickListener {
            val intent = Intent(this, ForgotActivity::class.java)
            startActivity(intent)
        }

        // Set click listener for signup button
        signupButton.setOnClickListener {
            val intent = Intent(this, SignupActivity::class.java)
            startActivity(intent)
        }

        // Optionally, retrieve and display stored email and password
        val savedEmail = sharedPreferences.getString("email", "")
        val savedPassword = sharedPreferences.getString("password", "")
        emailEditText.setText(savedEmail)
        passwordEditText.setText(savedPassword)
    }
}
