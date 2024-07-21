package com.example.bloodbank

import android.app.Activity
import android.app.DatePickerDialog
import android.content.ContentValues.TAG
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.Spinner
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.toObject
import java.time.LocalDateTime
import java.util.Calendar
import java.util.UUID

class CreatePostActivity : AppCompatActivity(){
    private lateinit var auth: FirebaseAuth
    private lateinit var firestore: FirebaseFirestore

    override fun onCreate(savedInstanceState: Bundle?){
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_create_post)

        // Initialize Firebase Auth and Firestore
        auth = FirebaseAuth.getInstance()
        firestore = FirebaseFirestore.getInstance()

        val description: EditText = findViewById(R.id.descriptionTextField)
        val email: EditText = findViewById(R.id.emailTextField)
        val needByDate: TextView = findViewById(R.id.needByDate)
        val phoneNumber: EditText = findViewById(R.id.phone_numberTextField)
        val bloodGroup: Spinner = findViewById(R.id.blood_groupTextField) // Changed to Spinner
        val city: EditText = findViewById(R.id.cityTextField)
        val province: EditText = findViewById(R.id.provinceTextField)
        val country: EditText = findViewById(R.id.countryTextField)
        val age: EditText = findViewById(R.id.ageTextField)
        val createPostButton: Button = findViewById(R.id.create_post_button)
        val cancelButton: Button = findViewById(R.id.cancel_button)
        var needByLocalDate: LocalDateTime = LocalDateTime.now()

        // Set up the Spinner for selecting the blood group
        val bloodGroups = arrayOf("Select Blood Group", "A+", "A-", "B+", "B-", "AB+", "AB-", "O+", "O-", "A2", "A2B", "CisAB")

        // simple_spinner_item and simple_spinner_dropdown_item are custom made resource files which manages the color and style of the spinner values
        // BloodGroupAdapter kotlin files manages the selected and non-selected state of the spinner (different colors for the text for both states)
        val adapter = BloodGroupAdapter(this, android.R.layout.simple_spinner_item, bloodGroups)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        bloodGroup.adapter = adapter

        // Function to setup a date picker
        needByDate.setOnClickListener {
            showDatePickerDialog(needByDate)
        }

        createPostButton.setOnClickListener{


                //Format Need By Date
                val needByText = needByDate.text.toString()
                Log.d(TAG, "NeedByDate: $needByText")
                val actualNeedByText = if (needByText.startsWith("Need Blood By Date: ")) {
                    needByText.substring("Need Blood By Date: ".length).trim()
                } else {
                    needByText.trim()
                }
                needByLocalDate = getLocalDateTimeFromString(actualNeedByText)



            // If the default option is selected instead of a specific blood group, the database is not updated with any value
            val selectedBloodGroup = if (bloodGroup.selectedItem.toString() == "Select Blood Group") {
                ""
            } else {
                bloodGroup.selectedItem.toString()
            }


            //Check & retreive logged in user
            val user = auth.currentUser
            if(user!=null){
                //Fetch User From Firebase
                val documentId = user.uid
                val userRef = firestore.collection("users").document(documentId)
                userRef.get()
                    .addOnSuccessListener { documentSnapshot ->
                        if (documentSnapshot.exists()) {
                            // Convert the document to a User object
                            val data = documentSnapshot.data
                            if (data != null) {
                                val loggedInUser = User.fromMap(data)
                                Log.d(TAG, "loggedInUser: $loggedInUser")
                                // Create Post Data Model
                                val post: UserPost = UserPost(
                                    id = UUID.randomUUID().toString(),
                                    firstName  = loggedInUser.firstName,
                                    lastName = loggedInUser.lastName,
                                    needByDate = needByLocalDate,
                                    bloodGroup = selectedBloodGroup,
                                    city = city.text.toString().trim(),
                                    province = province.text.toString().trim(),
                                    country = country.text.toString().trim(),
                                    profileImageUrl = loggedInUser.profilePic,
                                    description = description.text.toString().trim(),
                                    priority ="",
                                    userId = loggedInUser.id,
                                    patientAge = age.text.toString().trim().toInt(),
                                    email = email.text.toString().trim(),
                                    phone = phoneNumber.text.toString().trim(),
                                    createdTime = LocalDateTime.now()
                                )
                                val postMap = post.toMap()
                                //Add Post to firestore
                                firestore.collection("posts").document(post.id)
                                    .set(postMap)
                                    .addOnSuccessListener {
                                        Toast.makeText(this, "Post created successfully!", Toast.LENGTH_SHORT).show()
                                        val resultIntent = Intent()
                                        setResult(Activity.RESULT_OK, resultIntent)
                                        finish()
                                    }
                                    .addOnFailureListener { e ->
                                        Toast.makeText(this, "Error creating post: ${e.message}", Toast.LENGTH_SHORT).show()
                                    }

                            }
                        } else {
                            Log.d(TAG, "No such document")
                        }
                    }
                    .addOnFailureListener { exception ->
                        Log.w(TAG, "Error getting document", exception)
                    }
            }

        }

        cancelButton.setOnClickListener {
            val resultIntent = Intent()
            setResult(Activity.RESULT_OK, resultIntent)
            finish() // Go back to the previous activity
        }

    }

    private fun showDatePickerDialog(dobTextView: TextView)  {
        val calendar = Calendar.getInstance()
        val year = calendar.get(Calendar.YEAR)
        val month = calendar.get(Calendar.MONTH)
        val day = calendar.get(Calendar.DAY_OF_MONTH)

        val datePickerDialog = DatePickerDialog(this, { _, selectedYear, selectedMonth, selectedDay ->

            val selectedDate = "$selectedDay/${selectedMonth + 1}/$selectedYear"
            dobTextView.text = "Need Blood By Date:                                  $selectedDate"
            dobTextView.setTextColor(resources.getColor(R.color.redLight, null))
        }, year, month, day)

        datePickerDialog.show()
    }

    private fun getLocalDateTimeFromString(dateString: String): LocalDateTime {
        val parts = dateString.split("/")

        val date = parts[0].toInt()
        val month = parts[1].toInt()
        val year = parts[2].toInt()
        val result: LocalDateTime =LocalDateTime.of(year,month,date,0,0)
        return result
    }
}