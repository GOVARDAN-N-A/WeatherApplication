package com.example.weather

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class Log_in_page: AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    enableEdgeToEdge()
    setContentView(R.layout.login_layout)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val loginusernameval=findViewById<EditText>(R.id.login_id)
        val loginpassval=findViewById<EditText>(R.id.login_pass)
        val loginbutton=findViewById<Button>(R.id.Login)
        val signupbutton=findViewById<Button>(R.id.new_user_id)

       val sharedPreferences =getSharedPreferences("Userprefs", Context.MODE_PRIVATE);

        loginbutton.setOnClickListener(){
            val user=loginusernameval.text.toString()
            val pass=loginpassval.text.toString()

            val savedusername=sharedPreferences.getString("username","")
            val savedpassword=sharedPreferences.getString("password","")

            if(user==savedusername&&pass==savedpassword){
                val intent_home=Intent(this,Home_page::class.java)
                startActivity(intent_home)
            }else{
                Toast.makeText(this,"Invalid user or password",Toast.LENGTH_SHORT).show()
            }

        }
        signupbutton.setOnClickListener{
            val intent_signup=Intent(this,Sign_up_page::class.java)
            startActivity(intent_signup)
        }
    }
}
