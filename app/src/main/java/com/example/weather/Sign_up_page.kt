package com.example.weather


import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Patterns
import android.widget.Button
import android.widget.EditText
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity


class Sign_up_page : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.signup_layout)

        val usernameval = findViewById<EditText>(R.id.username_edit_text)
        val passwordval = findViewById<EditText>(R.id.password_edit_text)
        val passwordval2 = findViewById<EditText>(R.id.confirm_pass)
        val emailval = findViewById<EditText>(R.id.e_mail)
        val cityval=findViewById<EditText>(R.id.city)
        val signupbuttonval = findViewById<Button>(R.id.signup_button)
        val loginbuttonval =findViewById<Button>(R.id.sign_in)


        val sharedPreferences =getSharedPreferences("Userprefs",Context.MODE_PRIVATE)

        signupbuttonval.setOnClickListener{
            val user_name = usernameval.text.toString().trim()
            val pass = passwordval.text.toString().trim()
            val pass2 = passwordval2.text.toString()
            val e_mail = emailval.text.toString().trim()
            val cityname = cityval.text.toString().trim()
            if (user_name.isBlank()) {
                usernameval.error = "Enter the username"
            } else if (pass.isEmpty()) {
                passwordval.error = "Enter the password"

            } else {
                if (pass == pass2) {
                    if (!isValidemail(e_mail)) {
                        emailval.error = "enter a valid email"
                    } else {
                        if(cityname.isNotBlank()){
                            val editor =sharedPreferences.edit()
                            editor.putString("username",user_name)
                            editor.putString("city",cityname)
                            editor.putString("password",pass)
                            editor.putString("email",e_mail)
                            editor.apply()

                            val intent = Intent(this, Home_page::class.java)
                            startActivity(intent)
                        }else{
                            cityval.error="Enter the city name"
                        }

                    }
                }else {
                    passwordval2.error = "Password doesn't match"
                }
            }

        }
        val savedEmail=sharedPreferences.getString("email","")
        val savedpassword=sharedPreferences.getString("password","")
        val savedusername=sharedPreferences.getString("username","")
        val savedcity=sharedPreferences.getString("city","")
        emailval.setText(savedEmail)
        passwordval.setText(savedpassword)
        usernameval.setText(savedusername)
        cityval.setText(savedcity)

        loginbuttonval.setOnClickListener{
            val Login_intent=Intent(this,Log_in_page::class.java)
            startActivity(Login_intent)
        }
    }
}

private fun isValidemail(email: String): Boolean {
    return Patterns.EMAIL_ADDRESS.matcher(email).matches()

}
