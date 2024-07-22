package com.example.reviewapp

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import  android.os.Looper
import android.view.View
import android.view.ViewTreeObserver
import androidx.lifecycle.lifecycleScope
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import androidx.appcompat.app.AppCompatActivity as AppCompatActivity1

// Placeholder for ViewModel class and isReady property.
class ViewModel {
    private var _isDataLoaded: Boolean = false
    init {
        loadData()
    }
    private fun loadData(){
        _isDataLoaded = true
    }
val isReady: Boolean
        get() = _isDataLoaded
}

class SplashActivity : AppCompatActivity1() {

    private lateinit var mAuth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash) // Add a layout for SplashActivity

        mAuth = FirebaseAuth.getInstance()

        val SPLASH_TIME_OUT: Long = 2000

        // Use Handler to delay navigation
        Handler().postDelayed({
            // Check if user is already authenticated
            val currentUser = mAuth.currentUser
            if (currentUser != null) {
                // User is already authenticated, navigate to ReviewActivity or main activity
                startActivity(Intent(this, ReviewActivity::class.java))
            } else {
                // User is not authenticated, navigate to Login or Registration
                startActivity(Intent(this, Registration::class.java))
            }
            finish()
        }, SPLASH_TIME_OUT)
    }
}
