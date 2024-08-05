package com.example.bloodbank

import UserPostAdapter
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.*
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

    private val REQUEST_CODE_UPDATE_POST = 1
    private val REQUEST_CODE_UPDATE_USER = 2

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)

        // Find the ImageView within the included toolbar layout
        val toolbar: Toolbar = findViewById(R.id.toolbar)

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

        val logoAccount: ImageView = toolbar.findViewById(R.id.logoAccount)
        logoAccount.setOnClickListener {
            val intent = Intent(this, ProfileActivity::class.java)
            startActivityForResult(intent, REQUEST_CODE_UPDATE_USER)
        }

        // Floating Button Action
        val floatingButton: LinearLayout = findViewById(R.id.fab_with_text)
        floatingButton.setOnClickListener {
            val intent = Intent(this, CreatePostActivity::class.java)
            startActivityForResult(intent, REQUEST_CODE_UPDATE_POST)
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

        // Check and update user details
//        checkAndUpdateUserDetails()
    }

    private fun fetchUserPosts(onDataFetched: (List<UserPost>) -> Unit) {
        firestore.collection("posts")
            .orderBy("createdTime", com.google.firebase.firestore.Query.Direction.DESCENDING)
            .get()
            .addOnSuccessListener { result ->
                userPosts.clear()
                for (document in result) {
                    val data = document.data
                    val userPost = UserPost.fromMap(data)
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
        if (requestCode == REQUEST_CODE_UPDATE_POST && resultCode == RESULT_OK) {
            Log.d("HomeActivity", "Return from REQUEST_CODE_UPDATE_POST")
            // Fetch user posts again if the result indicates success
            fetchUserPosts { posts ->
                // Initialize adapter with fetched data and logged-in user ID
                val loggedInUserId = auth.currentUser?.uid
                if (loggedInUserId != null) {
                    adapter = UserPostAdapter(posts, loggedInUserId, "HomeActivity")
                    recyclerView.adapter = adapter
                }
            }

        } else if (requestCode == REQUEST_CODE_UPDATE_USER && resultCode == RESULT_OK){
            Log.d("HomeActivity", "Return from REQUEST_CODE_UPDATE_USER")
            // Check and update user details
            checkAndUpdateUserDetails{ value ->
                if(value){
                    fetchUserPosts { posts ->
                        // Initialize adapter with fetched data and logged-in user ID
                        val loggedInUserId = auth.currentUser?.uid
                        if (loggedInUserId != null) {
                            adapter = UserPostAdapter(posts, loggedInUserId, "HomeActivity")
                            recyclerView.adapter = adapter
                        }
                    }
                }
            }
        }
    }

    private fun checkAndUpdateUserDetails(onDataUpdated: (value : Boolean) -> Unit) {
        val userId = auth.currentUser?.uid ?: return

        firestore.collection("users").document(userId).get()
            .addOnSuccessListener { userDocument ->
                val user = userDocument.data
                if (user != null) {
                    val userFirstName = user["firstName"] as? String
                    val userLastName = user["lastName"] as? String
                    val userProfilePic = user["profilePic"] as? String

                    if (userFirstName != null && userLastName != null && userProfilePic != null) {
                        firestore.collection("posts")
                            .whereEqualTo("userId", userId)
                            .get()
                            .addOnSuccessListener { postsQuerySnapshot ->
                                val batch = firestore.batch()
                                for (postDocument in postsQuerySnapshot.documents) {
                                    val postFirstName = postDocument.getString("firstName")
                                    val postLastName = postDocument.getString("lastName")
                                    val postProfilePic = postDocument.getString("profileImageUrl")

                                    if (postFirstName != userFirstName || postLastName != userLastName || postProfilePic != userProfilePic) {
                                        val postRef = postDocument.reference
                                        batch.update(postRef, "firstName", userFirstName)
                                        batch.update(postRef, "lastName", userLastName)
                                        batch.update(postRef, "profileImageUrl", userProfilePic)
                                    }
                                }
                                batch.commit()
                                    .addOnSuccessListener {
                                        Log.d("HomeActivity", "User details updated in posts")
                                        onDataUpdated(true)
                                    }
                                    .addOnFailureListener { e ->
                                        Log.w("HomeActivity", "Error updating user details in posts", e)
                                        onDataUpdated(false)
                                    }
                            }
                            .addOnFailureListener { e ->
                                Log.w("HomeActivity", "Error getting user posts for update", e)
                                onDataUpdated(false)
                            }
                    } else {
                        Log.w("HomeActivity", "User details are missing")
                        onDataUpdated(false)
                    }
                }
            }
            .addOnFailureListener { e ->
                Log.w("HomeActivity", "Error getting user details", e)
                onDataUpdated(false)
            }
    }
}
