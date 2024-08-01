package com.example.bloodbank

import android.app.DatePickerDialog
import android.content.Intent
import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import java.util.*

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
        val dob: TextView = findViewById(R.id.dob) // Changed to TextView
        val phoneNumber: EditText = findViewById(R.id.phone_number)
        val bloodGroup: Spinner = findViewById(R.id.blood_group) // Changed to Spinner
        val city: EditText = findViewById(R.id.city)
        val province: EditText = findViewById(R.id.province)
        val country: EditText = findViewById(R.id.country)
        val signUpButton: Button = findViewById(R.id.sign_up_button)

        // Function to setup a date picker
        dob.setOnClickListener {
            showDatePickerDialog(dob)
        }

        // Set up the Spinner for selecting the blood group
        val bloodGroups = arrayOf("Select Blood Group", "A+", "A-", "B+", "B-", "AB+", "AB-", "O+", "O-", "A2", "A2B", "CisAB")

        // simple_spinner_item and simple_spinner_dropdown_item are custom made resource files which manages the color and style of the spinner values
        // BloodGroupAdapter kotlin files manages the selected and non-selected state of the spinner (different colors for the text for both states)
        val adapter = BloodGroupAdapter(this, android.R.layout.simple_spinner_item, bloodGroups)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        bloodGroup.adapter = adapter

        signUpButton.setOnClickListener {
            val user = auth.currentUser

            // String handling before storing the DOB to the data to make sure if it gets saved in right format
            if (user != null) {
                val dobText = dob.text.toString()
                val actualDob = if (dobText.startsWith("Date of Birth: ")) {
                    dobText.substring("Date of Birth: ".length).trim()
                } else {
                    dobText.trim()
                }

                // If the default option is selected instead of a specific blood group, the database is not updated with any value
                val selectedBloodGroup = if (bloodGroup.selectedItem.toString() == "Select Blood Group") {
                    ""
                } else {
                    bloodGroup.selectedItem.toString()
                }

                val userData = hashMapOf(
                    "id" to user.uid,
                    "email" to user.email,
                    "firstName" to firstName.text.toString().trim(),
                    "lastName" to lastName.text.toString().trim(),
                    "dob" to actualDob,
                    "phoneNumber" to phoneNumber.text.toString().trim(),
                    "bloodGroup" to selectedBloodGroup,
                    "city" to city.text.toString().trim(),
                    "province" to province.text.toString().trim(),
                    "country" to country.text.toString().trim(),
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

    private fun showDatePickerDialog(dobTextView: TextView) {
        val calendar = Calendar.getInstance()
        val year = calendar.get(Calendar.YEAR)
        val month = calendar.get(Calendar.MONTH)
        val day = calendar.get(Calendar.DAY_OF_MONTH)

        val datePickerDialog = DatePickerDialog(this, { _, selectedYear, selectedMonth, selectedDay ->
            val selectedDate = "$selectedDay/${selectedMonth + 1}/$selectedYear"
            dobTextView.text = "Date of Birth:                                  $selectedDate"
            dobTextView.setTextColor(resources.getColor(R.color.redLight, null))
        }, year, month, day)

        datePickerDialog.show()
    }
}
