package com.example.bloodbank

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import android.widget.*
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore


class MainActivity : AppCompatActivity() {

    private lateinit var firebaseAnalytics: FirebaseAnalytics
    private lateinit var auth: FirebaseAuth
    private lateinit var firestore: FirebaseFirestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        firebaseAnalytics = FirebaseAnalytics.getInstance(this)
        // Initialize Firebase Auth
        auth = FirebaseAuth.getInstance()
        // Initialize Firestore
        firestore = FirebaseFirestore.getInstance()

        // Check if user is already signed in
        checkIfUserLoggedIn()

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