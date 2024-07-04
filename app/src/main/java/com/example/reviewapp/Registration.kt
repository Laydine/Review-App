package com.example.reviewapp

import android.annotation.SuppressLint
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button

class Registration : AppCompatActivity() {
    private var registration: Button? = null
    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_registration)

        registration = findViewById(R.id.REGISTER)
        registration?. setOnClickListener(){
            startActivity(Intent(this, Login::class.java))
        }
    }
}