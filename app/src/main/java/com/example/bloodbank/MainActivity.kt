package com.example.bloodbank

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.ui.*
import android.view.*
import android.widget.*
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.widget.Toolbar
import androidx.core.content.ContextCompat
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.SignInButton
import com.google.android.gms.common.api.ApiException
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.firestore.FirebaseFirestore


class MainActivity : AppCompatActivity() {

    private lateinit var firebaseAnalytics: FirebaseAnalytics
    private lateinit var auth: FirebaseAuth
    private lateinit var googleSignInClient: GoogleSignInClient
    private lateinit var firestore: FirebaseFirestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        firebaseAnalytics = FirebaseAnalytics.getInstance(this)
        // Initialize Firebase Auth
        auth = FirebaseAuth.getInstance()
        // Initialize Firestore
        firestore = FirebaseFirestore.getInstance()
        // Configure Google Sign-In
//        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
//            .requestIdToken(getString(R.string.default_web_client_id))
//            .requestEmail()
//            .build()
//
//        googleSignInClient = GoogleSignIn.getClient(this, gso)

        // Check if user is already signed in
        checkIfUserLoggedIn()

        // Set a click listener for the sign-in button
//        val signInButton: Button = findViewById(R.id.loginBtn)
//        signInButton.setOnClickListener {
//            signIn()
//        }
    }

    private fun checkIfUserLoggedIn() {
        val currentUser = auth.currentUser
        if (currentUser != null) {
            // User is signed in, check Firestore for document
            checkDocumentExists(currentUser.uid)
        } else {
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
            finish()
        }
    }


//    private fun signIn() {
//        val signInIntent = googleSignInClient.signInIntent
//        resultLauncher.launch(signInIntent)
//    }
//
//    private val resultLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
//        val task = GoogleSignIn.getSignedInAccountFromIntent(result.data)
//        try {
//            // Google Sign-In was successful, authenticate with Firebase
//            val account = task.getResult(ApiException::class.java)!!
//            firebaseAuthWithGoogle(account.idToken!!)
//        } catch (e: ApiException) {
//            // Google Sign-In failed
//            Toast.makeText(this, "Google sign-in failed.", Toast.LENGTH_SHORT).show()
//            e.printStackTrace()
//        }
//    }
//
//    private fun firebaseAuthWithGoogle(idToken: String) {
//        val credential = GoogleAuthProvider.getCredential(idToken, null)
//        auth.signInWithCredential(credential)
//            .addOnCompleteListener(this) { task ->
//                if (task.isSuccessful) {
//                    // Sign in success
//                    val user = auth.currentUser
//                    val uID: String? = user?.uid
//                    Toast.makeText(this, "Sign in successful.", Toast.LENGTH_SHORT).show()
//                    // Update UI with user info if needed
//                    checkDocumentExists(user?.uid)
//                } else {
//                    // Sign in failed
//                    Toast.makeText(this, "Firebase authentication failed.", Toast.LENGTH_SHORT).show()
//                }
//            }
//    }

    private fun checkDocumentExists(uid: String?) {
        if (uid == null) return

        val docRef = firestore.collection("users").document(uid)
        docRef.get()
            .addOnSuccessListener { document ->
                if (document.exists()) {
                    // Document exists, redirect to HomeActivity
                    val intent = Intent(this, HomeActivity::class.java)
                    startActivity(intent)
                    finish()  // Close the MainActivity
                } else {
                    // Document does not exist, redirect to SignUpActivity
                    val intent = Intent(this, SignUpActivity::class.java)
                    startActivity(intent)
                    finish()
                }
            }
            .addOnFailureListener { exception ->
                Toast.makeText(this, "Failed to check document: ${exception.message}", Toast.LENGTH_SHORT).show()
            }
    }
}