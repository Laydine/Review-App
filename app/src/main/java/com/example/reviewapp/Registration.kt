package com.example.reviewapp

import android.annotation.SuppressLint
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.graphics.drawable.Drawable
import android.text.method.HideReturnsTransformationMethod
import android.text.method.PasswordTransformationMethod
import android.view.MotionEvent
import android.os.Bundle
import android.widget.ProgressBar
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth



class Registration : AppCompatActivity() {

    private lateinit var mAuth: FirebaseAuth
    private lateinit var passwordEditText: EditText
    private lateinit var firstNameEditText: EditText
    private lateinit var confirmPasswordEditText: EditText
    private lateinit var secondNameEditText: EditText
    private lateinit var registerButton: Button
    private lateinit var loginTextView: TextView
    private lateinit var emailEditText: EditText
    private lateinit var progressBar: ProgressBar


    @SuppressLint("MissingInflatedId", "WrongViewCast", "CutPasteId", "ClickableViewAccessibility")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_registration)

        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        mAuth = FirebaseAuth.getInstance()

        firstNameEditText = findViewById(R.id.FN)
        secondNameEditText = findViewById(R.id.SN)
        emailEditText = findViewById(R.id.EM)
        passwordEditText = findViewById(R.id.PSWRD)
        confirmPasswordEditText = findViewById<EditText>(R.id.PSRD)
        registerButton = findViewById(R.id.REGISTER)
        loginTextView = findViewById(R.id.loginLink)
        progressBar = findViewById(R.id.progressbar)



        registerButton.setOnClickListener {
            val firstName = firstNameEditText.text?.toString()?.trim()
            val secondName = secondNameEditText.text?.toString()?.trim()
            val email = emailEditText.text?.toString()?.trim()
            val password = passwordEditText.text?.toString()?.trim()
            val confirmPassword = confirmPasswordEditText.text?.toString()?.trim()

            if (email.isNullOrEmpty() || password.isNullOrEmpty() || confirmPassword.isNullOrEmpty() || firstName.isNullOrEmpty() || secondName.isNullOrEmpty()) {
                Toast.makeText(this, "Please fill in all fields", Toast.LENGTH_SHORT).show()
            } else if (password != confirmPassword) {
                Toast.makeText(this, "Passwords do not match", Toast.LENGTH_SHORT).show()
            } else {
                progressBar.visibility = android.view.View.VISIBLE
                registerUser(firstName, secondName, email, password)
            }
        }
        loginTextView.setOnClickListener {
            startActivity(Intent(this, Login::class.java))
            finish()
        }
        // Set password visibility toggle for password EditText
        setPasswordVisibilityToggle(passwordEditText)

        // Set password visibility toggle for confirm password EditText
        setPasswordVisibilityToggle(confirmPasswordEditText)
    }

    @SuppressLint("ClickableViewAccessibility")
    private fun setPasswordVisibilityToggle(editText: EditText?) {
        editText?.setCompoundDrawablesRelativeWithIntrinsicBounds(0, 0, R.drawable.ic_visibility_off, 0)
            editText?.tag = false

            editText?.setOnTouchListener { _, event ->
                if (event.action == MotionEvent.ACTION_UP) {
                    val drawableEnd: Drawable? = editText.compoundDrawablesRelative[2]
                    if (drawableEnd != null) {
                        if (event.rawX >= (editText.right - drawableEnd.bounds.width())) {
                            val isVisible = editText.tag as Boolean
                            if (isVisible) {
                                editText.transformationMethod = PasswordTransformationMethod.getInstance()
                                editText.setCompoundDrawablesRelativeWithIntrinsicBounds(0, 0, R.drawable.ic_visibility_off, 0)
                            } else {
                                editText.transformationMethod = HideReturnsTransformationMethod.getInstance()
                                editText.setCompoundDrawablesRelativeWithIntrinsicBounds(0, 0, R.drawable.ic_visibility, 0)
                            }
                            editText.tag = !isVisible
                            editText.setSelection(editText.text.length)
                            return@setOnTouchListener true
                        }
                    }
                }
                false
            }
        }


    private fun registerUser(firstName: String, secondName: String, email: String, password: String) {
        mAuth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) { task ->
                progressBar.visibility = android.view.View.GONE

                if (task.isSuccessful) {
                    Toast.makeText(this, "Registration successful", Toast.LENGTH_SHORT).show()
                    // Redirect to login activity
                    startActivity(Intent(this, Login::class.java))
                    finish()
                } else {
                    Toast.makeText(this, "Registration failed: ${task.exception?.message}", Toast.LENGTH_SHORT).show()
                }
            }
    }
}
