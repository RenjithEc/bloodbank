package com.example.bloodbank

import UserPostAdapter
import android.app.DatePickerDialog
import android.content.ContentValues.TAG
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.Spinner
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import java.time.LocalDateTime
import java.util.Calendar
import com.example.bloodbank.UserPost
import java.time.LocalDate
import java.time.format.DateTimeFormatter

class DonateActivity : AppCompatActivity() {
    private lateinit var auth: FirebaseAuth
    private lateinit var firestore: FirebaseFirestore
    private lateinit var city: EditText
    private lateinit var needByDate: TextView
    private lateinit var bloodGroupSpinner: Spinner
    private val userPosts = mutableListOf<UserPost>()
    private lateinit var uPostAdapter: UserPostAdapter
    private lateinit var recyclerView: RecyclerView
    private lateinit var searchBtn: Button
    var needByLocalDate: LocalDateTime = LocalDateTime.now()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_donate)

        // Find the ImageView within the included toolbar layout
        val toolbar: androidx.appcompat.widget.Toolbar = findViewById(R.id.toolbar)
        val logoHome: ImageView = toolbar.findViewById(R.id.logoHome)
        val logoAccount: ImageView = toolbar.findViewById(R.id.logoAccount)

        logoHome.setOnClickListener {
            val intent = Intent(this, HomeActivity::class.java)
            startActivity(intent)
        }

        logoAccount.setOnClickListener {
            val intent = Intent(this, ProfileActivity::class.java)
            startActivity(intent)
        }

        auth = FirebaseAuth.getInstance()
        firestore = FirebaseFirestore.getInstance()

        bloodGroupSpinner = findViewById(R.id.blood_groupTextField)
        city = findViewById(R.id.city)
        needByDate = findViewById(R.id.needByDate)
        searchBtn = findViewById(R.id.Search)
        recyclerView = findViewById(R.id.recyclerview)
        needByLocalDate = LocalDateTime.now()

        // Get the logged-in user's ID
        val loggedInUserId = auth.currentUser?.uid

        if (loggedInUserId != null) {
            uPostAdapter = UserPostAdapter(userPosts, loggedInUserId)
            recyclerView.layoutManager = LinearLayoutManager(this)
            recyclerView.adapter = uPostAdapter
        }

        val cancelBtn: Button = findViewById(R.id.cancel)
        val bloodGroups = arrayOf("Select Blood Group", "A+", "A-", "B+", "B-", "AB+", "AB-", "O+", "O-", "A2", "A2B", "CisAB")

        val adapter = BloodGroupAdapter(this, android.R.layout.simple_spinner_item, bloodGroups)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        bloodGroupSpinner.adapter = adapter

        needByDate.setOnClickListener {
            showDatePickerDialog(needByDate)
        }

        searchBtn.setOnClickListener {
            fetchPosts { posts ->
                // Initialize adapter with fetched data and logged-in user ID
                if (loggedInUserId != null) {
                    uPostAdapter = UserPostAdapter(posts, loggedInUserId)
                    recyclerView.adapter = uPostAdapter
                }
            }
        }

        cancelBtn.setOnClickListener {
            finish()
        }
    }

    private fun fetchPosts(onDataFetched: (List<UserPost>) -> Unit) {
        val bloodGroup = bloodGroupSpinner.selectedItem.toString().trim()
        val city = city.text.toString().trim()
        val needByText = needByDate.text.toString()
        Log.d(TAG, "NeedByDate: $needByText")
        val actualNeedByText = if (needByText.startsWith("Need Blood By: ")) {
            needByText.substring("Need Blood By: ".length).trim()
        } else {
            needByText.trim()
        }
        needByLocalDate = getLocalDateTimeFromString(actualNeedByText)
        println("Blood Group: $bloodGroup, City: $city, needByDate: ${needByLocalDate.toString()}")

        firestore.collection("posts")
            .whereEqualTo("bloodGroup", bloodGroup)
            .whereLessThanOrEqualTo("needByDate", needByLocalDate.toString())
            .get()
            .addOnSuccessListener { querySnapshot ->
                userPosts.clear()
                val regex = Regex(".*${city}.*", RegexOption.IGNORE_CASE)
                println("Data from dbbb: ${querySnapshot.documents}")
                for (document in querySnapshot) {
                    val userPostMap = document.data
                    val userPost = UserPost.fromMap(userPostMap)
                    if (regex.containsMatchIn(userPost.city)) {
                        userPosts.add(userPost)
                    }
                }
                onDataFetched(userPosts)
                Log.d("DonateActivity", "Fetched user posts: $userPosts")
            }
            .addOnFailureListener { exception ->
                Log.w("DonateActivity", "Error fetching user posts", exception)
            }
    }

    private fun showDatePickerDialog(dobTextView: TextView) {
        val calendar = Calendar.getInstance()
        val year = calendar.get(Calendar.YEAR)
        val month = calendar.get(Calendar.MONTH)
        val day = calendar.get(Calendar.DAY_OF_MONTH)

        val datePickerDialog = DatePickerDialog(this, { _, selectedYear, selectedMonth, selectedDay ->
            val selectedDate = "$selectedDay/${selectedMonth + 1}/$selectedYear"
            dobTextView.text = "Need Blood By: $selectedDate"
            dobTextView.setTextColor(resources.getColor(R.color.redLight, null))
        }, year, month, day)

        datePickerDialog.datePicker.minDate = calendar.timeInMillis
        datePickerDialog.show()
    }

    private fun getLocalDateTimeFromString(dateString: String): LocalDateTime {
        return try {
            val formatter = DateTimeFormatter.ofPattern("d/M/yyyy")
            val localDate = LocalDate.parse(dateString, formatter)
            localDate.atStartOfDay()
        } catch (e: Exception) {
            Log.e(TAG, "Error parsing date: $dateString", e)
            LocalDateTime.now()  // Default to the current date-time if parsing fails
        }
    }
}