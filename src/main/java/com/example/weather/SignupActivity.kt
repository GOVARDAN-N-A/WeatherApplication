package com.example.weather

import android.content.Intent
import android.os.Bundle
import android.util.Patterns
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity

class SignupActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.signup_page)

        val user_name_val = findViewById<EditText>(R.id.username_edit_text)
        val password_val = findViewById<EditText>(R.id.password_edit_text)
        val password_val2 = findViewById<EditText>(R.id.confirm_pass)
        val email_val = findViewById<EditText>(R.id.e_mail)
        val signup_button_val = findViewById<Button>(R.id.signup_button)
        val login_button_val = findViewById<Button>(R.id.sign_in)

        signup_button_val.setOnClickListener {
            val user_name = user_name_val.text.toString()
            val pass = password_val.text.toString()
            val pass2 = password_val2.text.toString()
            val e_mail = email_val.text.toString()

            if (user_name.isBlank()) {
                user_name_val.error = "Enter the username"
            } else if (pass.isEmpty()) {
                password_val.error = "Enter the password"
            } else {
                if (pass == pass2) {
                    if (!isValidEmail(e_mail)) {
                        email_val.error = "Enter a valid email"
                    } else {
                        Toast.makeText(this, "Validation Completed", Toast.LENGTH_SHORT).show()
                        val intent = Intent(this, LoginActivity::class.java)
                        startActivity(intent)
                    }
                } else {
                    password_val2.error = "Passwords don't match"
                }
            }
        }

        login_button_val.setOnClickListener {
            val loginIntent = Intent(this, LoginActivity::class.java)
            startActivity(loginIntent)
        }
    }

    private fun isValidEmail(email: String): Boolean {
        return Patterns.EMAIL_ADDRESS.matcher(email).matches()
    }
}
