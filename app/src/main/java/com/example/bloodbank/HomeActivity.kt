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
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

class HomeActivity : AppCompatActivity() {
    private lateinit var auth: FirebaseAuth
    private lateinit var firestore: FirebaseFirestore
    private val userPosts = ArrayList<UserPost>()
    private lateinit var adapter: UserPostAdapter
    private lateinit var recyclerView: RecyclerView

    private val REQUEST_CODE_CREATE_POST = 1
    private val REQUEST_CODE_EDIT_POST = 2
    private val REQUEST_CODE_PROFILE_PAGE = 3

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
            startActivityForResult(intent, REQUEST_CODE_PROFILE_PAGE)
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

        // Check and update user details
        checkAndUpdateUserDetails()
    }

    private fun fetchUserPosts(onDataFetched: (List<UserPost>) -> Unit) {
        firestore.collection("posts")
            .orderBy("createdTime", com.google.firebase.firestore.Query.Direction.DESCENDING)
            .get()
            .addOnSuccessListener { result ->
                userPosts.clear()
                for (document in result) {
                    val data = document.data
                    val userPost = UserPost(
                        id = data["id"] as String,
                        firstName = data["firstName"] as String,
                        lastName = data["lastName"] as String,
                        needByDate = LocalDateTime.parse(data["needByDate"] as String, DateTimeFormatter.ISO_LOCAL_DATE_TIME),
                        bloodGroup = data["bloodGroup"] as String,
                        city = data["city"] as String,
                        province = data["province"] as String,
                        country = data["country"] as String,
                        profileImageUrl = data["profileImageUrl"] as String,
                        description = data["description"] as String,
                        priority = data["priority"] as String,
                        userId = data["userId"] as String,
                        patientAge = (data["patientAge"] as Long).toInt(),
                        email = data["email"] as String,
                        phone = data["phone"] as String,
                        createdTime = LocalDateTime.parse(data["createdTime"] as String, DateTimeFormatter.ISO_LOCAL_DATE_TIME)
                    )
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
        if ((requestCode == REQUEST_CODE_CREATE_POST || requestCode == REQUEST_CODE_EDIT_POST || requestCode == REQUEST_CODE_PROFILE_PAGE) && resultCode == RESULT_OK) {
            // Fetch user posts again if the result indicates success
            fetchUserPosts { posts ->
                // Initialize adapter with fetched data and logged-in user ID
                val loggedInUserId = auth.currentUser?.uid
                if (loggedInUserId != null) {
                    adapter = UserPostAdapter(posts, loggedInUserId, "HomeActivity")
                    recyclerView.adapter = adapter
                }
            }
            // Check and update user details
            checkAndUpdateUserDetails()
        }
    }

    private fun checkAndUpdateUserDetails() {
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
                                    }
                                    .addOnFailureListener { e ->
                                        Log.w("HomeActivity", "Error updating user details in posts", e)
                                    }
                            }
                            .addOnFailureListener { e ->
                                Log.w("HomeActivity", "Error getting user posts for update", e)
                            }
                    } else {
                        Log.w("HomeActivity", "User details are missing")
                    }
                }
            }
            .addOnFailureListener { e ->
                Log.w("HomeActivity", "Error getting user details", e)
            }
    }

}
