package com.example.bloodbank

import UserPostAdapter
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.bloodbank.UserPost
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class HomeActivity : AppCompatActivity() {
    private lateinit var auth: FirebaseAuth
    private lateinit var firestore: FirebaseFirestore
    private val userPosts = ArrayList<UserPost>()
    private lateinit var adapter: UserPostAdapter
    private lateinit var recyclerView: RecyclerView

    private val REQUEST_CODE_CREATE_POST = 1
    private val REQUEST_CODE_DONATE_PAGE = 1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)

        // Initialize Firebase Auth and Firestore
        auth = FirebaseAuth.getInstance()
        firestore = FirebaseFirestore.getInstance()

        // Toolbar
        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)

        // Setting the custom title
        val titleTextView = findViewById<TextView>(R.id.toolbarTitle)
        titleTextView.text = "Vitaly"

        // Alternatively, if you want to set the title dynamically from support action bar
        supportActionBar?.title = null

        // Getting the RecyclerView with ID recyclerViewPost and populating it with the users
        recyclerView = findViewById(R.id.recyclerViewPost)
        recyclerView.layoutManager = LinearLayoutManager(this)

        // Calls the UserAdapter.kt and passes the fetched users to it. UserAdapter will return the views with the populated user values.
        // Fetch user posts and initialize adapter after data is fetched
        fetchUserPosts { posts ->
            // Initialize adapter with fetched data
            adapter = UserPostAdapter(posts)
            recyclerView.adapter = adapter
        }

        //floating Button Action
        val floatingButton : LinearLayout = findViewById(R.id.fab_with_text)
        floatingButton.setOnClickListener{
            val intent = Intent(this, CreatePostActivity::class.java)
            startActivityForResult(intent, REQUEST_CODE_CREATE_POST)
        }

        val donateBtn: Button = findViewById(R.id.donateBtn)
        donateBtn.setOnClickListener{
            val intent = Intent(this,DonateActivity::class.java)
            startActivityForResult(intent,REQUEST_CODE_DONATE_PAGE)
        }

    }

    private fun fetchUserPosts(onDataFetched: (List<UserPost>) -> Unit) {
        firestore.collection("posts")
            .get()
            .addOnSuccessListener { result ->
                userPosts.clear()
                for (document in result) {
                    val userPost = UserPost.fromMap(document.data)
                    userPosts.add(userPost)
                }
                // Notify the callback that data has been fetched
                onDataFetched(userPosts)
                Log.d("HomeActivity", "Fetched user posts: $userPosts")
            }
            .addOnFailureListener { exception ->
                Log.w("HomeActivity", "Error fetching user posts", exception)
            }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_CODE_CREATE_POST && resultCode == RESULT_OK) {
            // Fetch user posts again if the result indicates success
            fetchUserPosts { posts ->
                // Initialize adapter with fetched data
                adapter = UserPostAdapter(posts)
                recyclerView.adapter = adapter
            }
        }
    }
}
