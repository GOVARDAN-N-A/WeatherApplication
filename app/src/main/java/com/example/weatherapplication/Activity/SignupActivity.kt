package com.example.navbar_fragment

import android.app.DatePickerDialog
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.WindowManager
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.weather.LoginActivity
import com.example.weatherapplication.R
import com.example.weatherapplication.Activity.WeatherActivity
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*

class SignupActivity : AppCompatActivity() {

    private lateinit var usernameLayout: TextInputLayout
    private lateinit var passwordLayout: TextInputLayout
    private lateinit var confirmPasswordLayout: TextInputLayout
    private lateinit var emailLayout: TextInputLayout
    private lateinit var cityLayout: TextInputLayout
    private lateinit var dobLayout: TextInputLayout
    private lateinit var editTextUsername: TextInputEditText
    private lateinit var editTextPassword: TextInputEditText
    private lateinit var editTextConfirmPassword: TextInputEditText
    private lateinit var editTextEmail: TextInputEditText
    private lateinit var editTextCity: TextInputEditText
    private lateinit var editTextDob: TextInputEditText

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_signup)

        initializeViews()
        setupListeners()

        window.setFlags(
            WindowManager.LayoutParams.FLAG_FULLSCREEN,
            WindowManager.LayoutParams.FLAG_FULLSCREEN
        )
    }

    private fun initializeViews() {
        usernameLayout = findViewById(R.id.username_layout)
        passwordLayout = findViewById(R.id.password_layout)
        confirmPasswordLayout = findViewById(R.id.confirm_pass_layout)
        emailLayout = findViewById(R.id.email_layout)
        cityLayout = findViewById(R.id.city_layout)
        dobLayout = findViewById(R.id.dob_layout)
        editTextUsername = usernameLayout.findViewById(R.id.username_edit_text)
        editTextPassword = passwordLayout.findViewById(R.id.password_edit_text)
        editTextConfirmPassword = confirmPasswordLayout.findViewById(R.id.confirm_pass)
        editTextEmail = emailLayout.findViewById(R.id.e_mail)
        editTextCity = cityLayout.findViewById(R.id.city)
        editTextDob = dobLayout.findViewById(R.id.dob_edit_text)
    }

    private fun setupListeners() {
        editTextDob.setOnClickListener { showDatePickerDialog() }

        editTextPassword.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(s: Editable?) {
                validatePassword(s.toString().trim())
            }
        })

        findViewById<Button>(R.id.signup_button).setOnClickListener { handleSignup() }
        findViewById<Button>(R.id.sign_in).setOnClickListener {
            startActivity(Intent(this, LoginActivity::class.java))
            finish()
        }
    }

    private fun handleSignup() {
        val username = editTextUsername.text.toString().trim()
        val password = editTextPassword.text.toString().trim()
        val confirmPassword = editTextConfirmPassword.text.toString().trim()
        val email = editTextEmail.text.toString().trim()
        val city = editTextCity.text.toString().trim()
        val dob = editTextDob.text.toString().trim()

        resetErrors()

        val isValid = validateInputs(username, password, confirmPassword, email, city, dob)

        if (isValid) {
            saveUserData(username, password, email, city, dob)
            Toast.makeText(this, "Signup successful", Toast.LENGTH_SHORT).show()
            startActivity(Intent(this, WeatherActivity::class.java))
            finish()
        }
    }

    private fun resetErrors() {
        usernameLayout.error = null
        passwordLayout.error = null
        confirmPasswordLayout.error = null
        emailLayout.error = null
        cityLayout.error = null
        dobLayout.error = null
    }

    private fun validateInputs(username: String, password: String, confirmPassword: String, email: String, city: String, dob: String): Boolean {
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

        if (dob.isBlank()) {
            dobLayout.error = "Please Enter Date of Birth"
            isValid = false
        } else if (!isAgeValid(dob)) {
            dobLayout.error = "You must be at least 18 years old"
            isValid = false
        }

        return isValid
    }

    private fun saveUserData(username: String, password: String, email: String, city: String, dob: String) {
        val sharedPreferences = getSharedPreferences("User", Context.MODE_PRIVATE)
        with(sharedPreferences.edit()) {
            putString("username", username)
            putString("password", password)
            putString("email", email)
            putString("dob", dob)
            putString("city", city)
            putBoolean("isLoggedIn", true)
            apply()
        }
    }

    private fun showDatePickerDialog() {
        val calendar = Calendar.getInstance()
        val year = calendar.get(Calendar.YEAR)
        val month = calendar.get(Calendar.MONTH)
        val day = calendar.get(Calendar.DAY_OF_MONTH)

        DatePickerDialog(this, { _, selectedYear, selectedMonth, selectedDay ->
            val selectedDate = Calendar.getInstance()
            selectedDate.set(selectedYear, selectedMonth, selectedDay)
            val dateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
            editTextDob.setText(dateFormat.format(selectedDate.time))
        }, year, month, day).show()
    }

    private fun isAgeValid(birthdate: String): Boolean {
        val dateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
        return try {
            val dob = dateFormat.parse(birthdate) ?: return false
            val today = Calendar.getInstance()
            val dobCalendar = Calendar.getInstance().apply { time = dob }

            var age = today.get(Calendar.YEAR) - dobCalendar.get(Calendar.YEAR)
            if (today.get(Calendar.DAY_OF_YEAR) < dobCalendar.get(Calendar.DAY_OF_YEAR)) {
                age-- // Adjust age if birthday hasn't occurred yet this year
            }

            age >= 18
        } catch (e: ParseException) {
            false
        }
    }

    private fun validatePassword(password: String) {
        passwordLayout.error = when {
            password.isEmpty() -> "Password must not be empty"
            password.length < 6 -> "Password must be at least 6 characters"
            !password.matches(Regex(".*[A-Z].*")) -> "Password must contain at least one uppercase letter"
            !password.matches(Regex(".*[a-z].*")) -> "Password must contain at least one lowercase letter"
            !password.matches(Regex(".*\\d.*")) -> "Password must contain at least one digit"
            !password.matches(Regex(".*[!@#\$%^&*(),.?\":{}|<>].*")) -> "Password must contain at least one special character"
            else -> null
        }
    }
}
