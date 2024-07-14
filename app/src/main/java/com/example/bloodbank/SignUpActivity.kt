package com.example.bloodbank

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class SignUpActivity : AppCompatActivity() {
    private lateinit var auth: FirebaseAuth
    private lateinit var firestore: FirebaseFirestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign_up)

        // Initialize Firebase Auth and Firestore
        auth = FirebaseAuth.getInstance()
        firestore = FirebaseFirestore.getInstance()

        val firstName: EditText = findViewById(R.id.first_name)
        val lastName: EditText = findViewById(R.id.last_name)
        val dob: EditText = findViewById(R.id.dob)
        val phoneNumber: EditText = findViewById(R.id.phone_number)
        val bloodGroup: EditText = findViewById(R.id.blood_group)
        val city: EditText = findViewById(R.id.city)
        val province: EditText = findViewById(R.id.province)
        val country: EditText = findViewById(R.id.country)
        val signUpButton: Button = findViewById(R.id.sign_up_button)

        signUpButton.setOnClickListener {
            val user = auth.currentUser
            if (user != null) {
                val userData = hashMapOf(
                    "id" to user.uid,
                    "email" to user.email,
                    "firstName" to firstName.text.toString(),
                    "lastName" to lastName.text.toString(),
                    "dob" to dob.text.toString(),
                    "phoneNumber" to phoneNumber.text.toString(),
                    "bloodGroup" to bloodGroup.text.toString(),
                    "city" to city.text.toString(),
                    "province" to province.text.toString(),
                    "country" to country.text.toString(),
                    "isActive" to true,
                    "profilePic" to ""
                )

                firestore.collection("users").document(user.uid)
                    .set(userData)
                    .addOnSuccessListener {
                        Toast.makeText(this, "Sign up successful!", Toast.LENGTH_SHORT).show()
                        val intent = Intent(this, HomeActivity::class.java)
                        startActivity(intent)
                        finish()
                    }
                    .addOnFailureListener { e ->
                        Toast.makeText(this, "Error signing up: ${e.message}", Toast.LENGTH_SHORT).show()
                    }
            }
        }
    }
}
