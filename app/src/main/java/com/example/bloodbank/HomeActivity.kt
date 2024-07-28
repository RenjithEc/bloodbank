package com.example.bloodbank

import UserPostAdapter
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.ImageView
import android.widget.LinearLayout
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class HomeActivity : AppCompatActivity() {
    private lateinit var auth: FirebaseAuth
    private lateinit var firestore: FirebaseFirestore
    private val userPosts = ArrayList<UserPost>()
    private lateinit var adapter: UserPostAdapter
    private lateinit var recyclerView: RecyclerView

    private val REQUEST_CODE_CREATE_POST = 1
    private val REQUEST_CODE_EDIT_POST = 2

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)

        // Find the ImageView within the included toolbar layout
        val toolbar: Toolbar = findViewById(R.id.toolbar)
        val logoAccount: ImageView = toolbar.findViewById(R.id.logoAccount)

        logoAccount.setOnClickListener {
            val intent = Intent(this, ProfileActivity::class.java)
            startActivity(intent)
        }

        // Initialize Firebase Auth and Firestore
        auth = FirebaseAuth.getInstance()
        firestore = FirebaseFirestore.getInstance()

        // Get the logged-in user's ID
        val loggedInUserId = auth.currentUser?.uid

        // Alternatively, if you want to set the title dynamically from support action bar
        supportActionBar?.title = null

        // Getting the RecyclerView with ID recyclerViewPost and populating it with the users
        recyclerView = findViewById(R.id.recyclerViewPost)
        recyclerView.layoutManager = LinearLayoutManager(this)

        // Fetch user posts and initialize adapter after data is fetched
        fetchUserPosts { posts ->
            // Initialize adapter with fetched data and logged-in user ID
            if (loggedInUserId != null) {
                adapter = UserPostAdapter(posts, loggedInUserId, "HomeActivity")
                recyclerView.adapter = adapter
            }
        }

        // Floating Button Action
        val floatingButton: LinearLayout = findViewById(R.id.fab_with_text)
        floatingButton.setOnClickListener {
            val intent = Intent(this, CreatePostActivity::class.java)
            startActivityForResult(intent, REQUEST_CODE_CREATE_POST)
        }

        val receiveBtn: Button = findViewById(R.id.receiveBtnHome)
        receiveBtn.setOnClickListener {
            val intent = Intent(this, ReceiveActivity::class.java)
            startActivity(intent)
        }

        val donateBtn: Button = findViewById(R.id.donateBtnHome)
        donateBtn.setOnClickListener {
            val intent = Intent(this, DonateActivity::class.java)
            startActivity(intent)
        }
    }

    private fun fetchUserPosts(onDataFetched: (List<UserPost>) -> Unit) {
        firestore.collection("posts")
            .orderBy("createdTime", com.google.firebase.firestore.Query.Direction.DESCENDING)
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
        if ((requestCode == REQUEST_CODE_CREATE_POST || requestCode == REQUEST_CODE_EDIT_POST) && resultCode == RESULT_OK) {
            // Fetch user posts again if the result indicates success
            fetchUserPosts { posts ->
                // Initialize adapter with fetched data and logged-in user ID
                val loggedInUserId = auth.currentUser?.uid
                if (loggedInUserId != null) {
                    adapter = UserPostAdapter(posts, loggedInUserId,"HomeActivity")
                    recyclerView.adapter = adapter
                }
            }
        }
    }

}
