package com.example.reviewapp

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button

class Login : AppCompatActivity() {
    private var login: Button? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        login = findViewById(R.id.LOGIN)


        login?. setOnClickListener(){
            startActivity( Intent(this, Registration::class.java))
        }
    }
}