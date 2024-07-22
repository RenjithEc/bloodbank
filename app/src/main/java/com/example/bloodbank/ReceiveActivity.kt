package com.example.bloodbank

import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.Spinner
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import UserAdapter
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.FirebaseApp

class ReceiveActivity : AppCompatActivity() {
    private lateinit var bloodGroupSpinner: Spinner
    private lateinit var locationEditText: EditText
    private lateinit var searchButton: Button
    private lateinit var usersRecyclerView: RecyclerView
    private lateinit var userAdapter: UserAdapter
    private val usersList = mutableListOf<User>()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.receive_page)
        FirebaseApp.initializeApp(this)

        bloodGroupSpinner = findViewById(R.id.blood_group)
        locationEditText = findViewById(R.id.location)
        searchButton = findViewById(R.id.button)
        usersRecyclerView = findViewById(R.id.usersRecyclerView)

        userAdapter = UserAdapter(usersList)
        usersRecyclerView.layoutManager = LinearLayoutManager(this)
        usersRecyclerView.adapter = userAdapter

        searchButton.setOnClickListener {
//            val bloodGroup = bloodGroupSpinner.selectedItem.toString().trim()
//            val location = locationEditText.text.toString().trim()
//            fetchUsers(bloodGroup, location)
            fetchUsers()
        }
    }

    private fun fetchUsers() {
        val database = FirebaseDatabase.getInstance().getReference("users").orderByChild("bloodGroup")
        Log.d("FirebaseData", "Value is: $database")

//        val usersRef = database.getReference("users")
//
//        usersRef.orderByChild("bloodGroup").equalTo(bloodGroup)
//            .addListenerForSingleValueEvent(object : ValueEventListener {
//                override fun onDataChange(dataSnapshot: DataSnapshot) {
//                    usersList.clear()
//                    for (userSnapshot in dataSnapshot.children) {
//                        val userLocation = userSnapshot.child("location").getValue(String::class.java)
//                        if (userLocation == location) {
//                            val user = userSnapshot.getValue(User::class.java)
//                            user?.let { usersList.add(it) }
//                        }
//                    }
//                    userAdapter.notifyDataSetChanged()
//                }
//
//                override fun onCancelled(databaseError: DatabaseError) {
//                    Log.w("MainActivity", "loadPost:onCancelled", databaseError.toException())
//                }
//            })
    }
}

