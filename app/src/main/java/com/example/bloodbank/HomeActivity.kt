package com.example.bloodbank

import UserAdapter
import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.blooddonationapp.User

class HomeActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)

        // Toolbar
        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)

        // Setting the custom title
        val titleTextView = findViewById<TextView>(R.id.toolbarTitle)
        titleTextView.text = "Vitaly"

        // Alternatively, if you want to set the title dynamically from support action bar
        supportActionBar?.title = null

        // Getting the RecyclerView and populating it with the users
        val recyclerView: RecyclerView = findViewById(R.id.recyclerView)
        recyclerView.layoutManager = LinearLayoutManager(this)

        val users = listOf(
            User("John", "Doe", "1990-01-01", "O+", "New York", "New York", "USA", "https://example.com/johndoe.jpg"),
            User("John2", "Doe2", "1990-01-01", "A+", "New York", "New York", "USA", "https://example.com/johndoe.jpg"),
            User("John3", "Doe3", "1990-01-01", "B+", "New York", "New York", "USA", "https://example.com/johndoe.jpg"),
            User("John4", "Doe4", "1990-01-01", "O-", "New York", "New York", "USA", "https://example.com/johndoe.jpg"),
            User("John5", "Doe5", "1990-01-01", "O+", "New York", "New York", "USA", "https://example.com/johndoe.jpg"),
            User("John6", "Doe5", "1990-01-01", "A+", "New York", "New York", "USA", "https://example.com/johndoe.jpg"),
            User("John7", "Doe5", "1990-01-01", "B+", "New York", "New York", "USA", "https://example.com/johndoe.jpg")
        )

        // Calls the UserAdapter.kt and passes the fetched users to it. UserAdapter will return the views with the populated user values.
        val adapter = UserAdapter(users)
        recyclerView.adapter = adapter
    }
}
