package com.example.reviewapp

import android.annotation.SuppressLint
import android.content.Intent
import android.graphics.drawable.Drawable
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.Toast
import android.widget.EditText
import android.text.method.HideReturnsTransformationMethod
import android.text.method.PasswordTransformationMethod
import android.view.MotionEvent
import android.widget.ProgressBar
import android.widget.TextView
import com.google.firebase.auth.FirebaseAuth

class Login : AppCompatActivity() {
    private lateinit var mAuth: FirebaseAuth
    private lateinit var emailEditText: EditText
    private lateinit var passwordEditText: EditText
    private lateinit var registerLinkTextView: TextView
    private lateinit var progressBar: ProgressBar
    private lateinit var loginButton: Button
    private var isPasswordVisible: Boolean = false

    @SuppressLint("WrongViewCast", "MissingInflatedId", "ClickableViewAccessibility")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        mAuth = FirebaseAuth.getInstance()

        loginButton = findViewById<Button>(R.id.LOGIN)
        emailEditText = findViewById<EditText>(R.id.EMAIL)
        passwordEditText = findViewById<EditText>(R.id.PASSWORD)
        registerLinkTextView = findViewById<TextView>(R.id.registerLink)
        progressBar = findViewById(R.id.progressBar)


        loginButton.setOnClickListener {
            val email = emailEditText.text.toString().trim()
            val password = passwordEditText.text.toString().trim()

            if (email.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "Please enter email and password", Toast.LENGTH_SHORT).show()
            } else {
                progressBar.visibility = android.view.View.VISIBLE
                loginUser(email, password)
            }
        }

        registerLinkTextView.setOnClickListener {
            startActivity(Intent(this, Registration::class.java))
            finish()
        }


        passwordEditText.setOnTouchListener { v, event ->
            if (event.action == MotionEvent.ACTION_UP) {
                val drawableEnd: Drawable? = passwordEditText.compoundDrawablesRelative[2]
                if (drawableEnd != null) {
                    if (event.rawX >= (passwordEditText.right - drawableEnd.bounds.width())) {
                        // Toggle password visibility
                        if (isPasswordVisible) {
                            // Hide the password
                            passwordEditText.transformationMethod =
                                PasswordTransformationMethod.getInstance()
                        } else {
                            // Show the password
                            passwordEditText.transformationMethod =
                                null // Set to null to show the text
                        }
                        isPasswordVisible = !isPasswordVisible

                        // Move the cursor to the end of the text
                        passwordEditText.setSelection(passwordEditText.text.length)
                        return@setOnTouchListener true
                    }
                }
            }
            false
        }
    }



        private fun loginUser(email: String, password: String) {
            mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this) { task ->
                    if (task.isSuccessful) {
                        // Login successful
                        Toast.makeText(this, "Login successful", Toast.LENGTH_SHORT).show()

                        // Navigate to ReviewActivity
                        startActivity(Intent(this, ReviewActivity::class.java))
                        finish()
                    } else {
                        // Login failed
                        Toast.makeText(this, "Login failed: ${task.exception?.message}", Toast.LENGTH_SHORT).show()
                    }
                }
        }
    }


