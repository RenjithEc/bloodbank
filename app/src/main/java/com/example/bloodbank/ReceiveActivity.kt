package com.example.bloodbank

import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.Spinner
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import UserAdapter
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.firestore.FirebaseFirestore

class ReceiveActivity : AppCompatActivity() {
    private lateinit var auth: FirebaseAuth
    private lateinit var firestore: FirebaseFirestore
    private lateinit var bloodGroupSpinner: Spinner
    private lateinit var locationEditText: EditText
    private lateinit var searchButton: Button
    private lateinit var usersRecyclerView: RecyclerView
    private lateinit var userAdapter: UserAdapter
    private val usersList = mutableListOf<User>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.receive_page)

        // Initialize Firebase Auth and Firestore
        auth = FirebaseAuth.getInstance()
        firestore = FirebaseFirestore.getInstance()

        bloodGroupSpinner = findViewById(R.id.blood_group)
        locationEditText = findViewById(R.id.location)
        searchButton = findViewById(R.id.button)
        usersRecyclerView = findViewById(R.id.usersRecyclerView)

        userAdapter = UserAdapter(usersList)
        usersRecyclerView.layoutManager = LinearLayoutManager(this)
        usersRecyclerView.adapter = userAdapter

        val bloodGroups = arrayOf("Select Blood Group", "A+", "A-", "B+", "B-", "AB+", "AB-", "O+", "O-", "A2", "A2B", "CisAB")

        // simple_spinner_item and simple_spinner_dropdown_item are custom-made resource files which manage the color and style of the spinner values
        // BloodGroupAdapter Kotlin file manages the selected and non-selected state of the spinner (different colors for the text for both states)
        val adapter = BloodGroupAdapter(this, android.R.layout.simple_spinner_item, bloodGroups)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        bloodGroupSpinner.adapter = adapter

        searchButton.setOnClickListener {
            fetchUsers { users ->
                // Initialize adapter with fetched data
                userAdapter = UserAdapter(usersList)
                usersRecyclerView.adapter = userAdapter
            }
        }
    }

    private fun fetchUsers(onDataFetched: (List<User>) -> Unit) {
        val bloodGroup = bloodGroupSpinner.selectedItem.toString().trim()
        val location = locationEditText.text.toString().trim()

        firestore.collection("users")
            .whereEqualTo("bloodGroup", bloodGroup)
            .get()
            .addOnSuccessListener { result ->
                usersList.clear()
                for (document in result) {
                    val userMap = document.data
                    val user = User.fromMap(userMap)
                    if (user.city == location) {
                        usersList.add(user)
                    }
                }
                // Notify the callback that data has been fetched
                onDataFetched(usersList)
                Log.d("ReceiveActivity", "Fetched users: $usersList")
            }
            .addOnFailureListener { exception ->
                Log.w("ReceiveActivity", "Error fetching users", exception)
            }
    }
}