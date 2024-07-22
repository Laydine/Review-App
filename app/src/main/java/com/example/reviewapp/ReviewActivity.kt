package com.example.reviewapp

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.util.Log
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.Spinner
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.gson.Gson
import com.google.gson.JsonSyntaxException
import com.google.gson.reflect.TypeToken
import java.io.File
import java.io.IOException
import android.view.MenuItem
import androidx.activity.OnBackPressedCallback
import androidx.activity.addCallback
import androidx.appcompat.widget.Toolbar


data class AppReview(val reviews: List<Review>)

data class Review(val reviewId: String, val authorName: String, val appName: String, val comments: List<Comment>)

data class Comment(val userComment: UserComment?, val developerComment: DeveloperComment?)

data class UserComment(val text: String, val lastModified: LastModified, val starRating: Int)

data class DeveloperComment(val text: String, val lastModified: LastModified)

data class LastModified(val seconds: String, val nanos: String)


class ReviewActivity : AppCompatActivity() {

    private lateinit var appReview: AppReview
    private lateinit var recyclerView: RecyclerView
    private lateinit var spinner: Spinner
    private lateinit var btnReview: Button
    private lateinit var btnDownload: Button
    private lateinit var reviewAdapter: ReviewAdapter
    private lateinit var toolbar: Toolbar

    private var negativeReviews = listOf<Review>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_review)

        btnReview = findViewById(R.id.BTTN)
        btnDownload = findViewById(R.id.btn_download)
        spinner = findViewById(R.id.spinner)
        recyclerView = findViewById(R.id.rv1)
        toolbar = findViewById(R.id.toolbar)

        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        // Set up back button in toolbar
        toolbar.setNavigationOnClickListener {
            navigateToLogin()
        }

        // Handle back press using OnBackPressedDispatcher
        onBackPressedDispatcher.addCallback(this, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                // Navigate to the login page
                navigateToLogin()
            }
        })

        // Initialize RecyclerView with an empty adapter
        recyclerView.layoutManager = LinearLayoutManager(this)
        reviewAdapter = ReviewAdapter(emptyList())
        recyclerView.adapter = reviewAdapter

        try {
            // Load data from JSON
            val jsonString = assets.open("reviews.json").bufferedReader().use { it.readText() }
            Log.d("ReviewActivity", "JSON Data: $jsonString")
            val type = object : TypeToken<AppReview>() {}.type
            appReview = Gson().fromJson(jsonString, type) ?: throw JsonSyntaxException("Parsed object is null")

            Log.d("ReviewActivity", "Parsed AppReview: $appReview")

            // Load Spinner
            val appNames = appReview.reviews.map { it.appName }.distinct()
            val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, appNames)
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            spinner.adapter = adapter
            Log.d("ReviewActivity", "App Names: $appNames")
        } catch (e: Exception) {
            e.printStackTrace()
            // Handle the error, e.g., show a message to the user
            Log.e("ReviewActivity", "Error loading JSON: ${e.message}")
        }

        // View negative reviews
        btnReview.setOnClickListener {
            val selectedAppName = spinner.selectedItem.toString()
            Log.d("ReviewActivity", "Selected App: $selectedAppName")

            // Log the full list of reviews
            Log.d("ReviewActivity", "All Reviews: $appReview.reviews")

            negativeReviews = appReview.reviews.filter { it.appName == selectedAppName }.mapNotNull { review ->
                val negativeComments = review.comments.filter { comment ->
                    comment.userComment?.let { userComment ->
                        userComment.starRating < 3
                    } ?: false
                }
                if (negativeComments.isNotEmpty()) {
                    Log.d("ReviewActivity", "Negative Comments for ${review.appName}: $negativeComments")
                    Review(
                        review.reviewId,
                        review.authorName,
                        review.appName,
                        negativeComments
                    )
                } else {
                        Toast.makeText(this, "There are no negative reviews for this app", Toast.LENGTH_LONG).show()
                    null
                }
            }

            // Log the filtered negative reviews
            Log.d("ReviewActivity", "Negative Reviews: $negativeReviews")
            reviewAdapter.updateData(negativeReviews)
            Log.d("ReviewActivity", "All Reviews: $appReview.reviews")
        }

        btnDownload.setOnClickListener {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                downloadCsvFile()
            } else {
                if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_DENIED) {
                    ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE), 1)
                } else {
                    downloadCsvFile()
                }
            }
            Toast.makeText(this, "downloaded to downloads", Toast.LENGTH_SHORT).show()
        }
    }

    private fun navigateToLogin() {
            val intent = Intent(this, Login::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK
            startActivity(intent)
            finish()
        }


    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> {
                navigateToLogin()
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }

    private fun downloadCsvFile() {
        val downloadFolder = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
        val csvFile = File(downloadFolder, "negative_review.csv")

        try {
            csvFile.printWriter().use { out ->
                out.println("AppName,Rating,Comment")

                negativeReviews.forEach { review ->
                    Log.d("ReviewActivity", "Processing review: ${review.appName}")

                    review.comments.forEach { comment ->
                        val userComment = comment.userComment
                        if (userComment != null) {
                            Log.d("ReviewActivity", "Processing comment: ${userComment.text}")
                            out.println("${review.appName},${userComment.starRating},${userComment.text}")
                        } else {
                            Log.d("ReviewActivity", "User comment is null for review: ${review.reviewId}")
                        }
                    }
                }
            }

            Log.d("ReviewActivity", "Negative reviews: $negativeReviews")
        } catch (e: IOException) {
            Log.e("ReviewActivity", "Error writing CSV file", e)
        }
    }


    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == 1 && grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            downloadCsvFile()
        } else {
            Toast.makeText(this, "Permission Denied", Toast.LENGTH_SHORT).show()
        }
    }
}


