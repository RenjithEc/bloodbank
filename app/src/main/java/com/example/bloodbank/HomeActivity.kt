package com.example.bloodbank

import UserPostAdapter
import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.blooddonationapp.UserPost

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

        // Getting the RecyclerView with ID recyclerViewPost and populating it with the users
        val recyclerView: RecyclerView = findViewById(R.id.recyclerViewPost)
        recyclerView.layoutManager = LinearLayoutManager(this)

        val userPostsData = listOf(
            UserPost("John", "Doe", "1990-01-01", "O+", "New York",
                "New York", "USA", "https://example.com/johndoe.jpg", "Hi I need blood type A+ve immediately, please contact me for more information Hi I need blood type A+ve immediately, please contact me for more information","Today"),
            UserPost("John2", "Doe2", "1990-01-01", "O+", "New York",
                "New York", "USA", "https://example.com/johndoe.jpg", "Hi I need blood type A+ve immediately","Today"),
            UserPost("John3", "Doe3", "1990-01-01", "O+", "New York",
                "New York", "USA", "https://example.com/johndoe.jpg", "Hi I need blood type A+ve immediately","Today"),
            UserPost("John4", "Doe4", "1990-01-01", "O+", "New York",
                "New York", "USA", "https://example.com/johndoe.jpg", "Hi I need blood type A+ve immediately, please contact me for more information","Today"),
            UserPost("John5", "Doe5", "1990-01-01", "O+", "New York",
                "New York", "USA", "https://example.com/johndoe.jpg", "Hi I need blood type A+ve immediately","Today"),
            UserPost("John6", "Doe6", "1990-01-01", "O+", "New York",
                "New York", "USA", "https://example.com/johndoe.jpg", "Hi I need blood type A+ve immediately","Today"),
            UserPost("John7", "Doe7", "1990-01-01", "O+", "New York",
                "New York", "USA", "https://example.com/johndoe.jpg", "Hi I need blood type A+ve immediately, please contact me for more information","Today"),
            UserPost("John8", "Doe8", "1990-01-01", "O+", "New York",
                "New York", "USA", "https://example.com/johndoe.jpg", "Hi I need blood type A+ve immediately","Today"),

        )

        // Calls the UserAdapter.kt and passes the fetched users to it. UserAdapter will return the views with the populated user values.
        val adapter = UserPostAdapter(userPostsData)
        recyclerView.adapter = adapter
    }
}
