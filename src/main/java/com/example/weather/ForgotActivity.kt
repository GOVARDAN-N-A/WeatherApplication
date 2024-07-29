package com.example.weather

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class ForgotActivity : AppCompatActivity() {
    private lateinit var emailEditText: EditText
    private lateinit var resetPasswordButton: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.forgot_password)

        // Initialize views
        emailEditText = findViewById(R.id.forgot_email_edit_text)
        resetPasswordButton = findViewById(R.id.reset_password_button)

        // Set click listener for reset password button
        resetPasswordButton.setOnClickListener {
            val email = emailEditText.text.toString()

            // Handle password reset logic here
            if (email.isNotEmpty()) {
                // For demonstration purposes, show a toast message
                // You would typically initiate a password reset process here
                Toast.makeText(this, "Password reset link sent to $email", Toast.LENGTH_LONG).show()

                // Optionally, finish the activity to return to the login screen
                finish()
            } else {
                Toast.makeText(this, "Please enter your email", Toast.LENGTH_LONG).show()
            }
        }
    }
}
