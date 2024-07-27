package com.example.bloodbank

import android.app.Activity
import android.app.DatePickerDialog
import android.content.ContentValues.TAG
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.Spinner
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import java.time.LocalDateTime
import java.util.Calendar

class EditPostActivity : AppCompatActivity() {
    private lateinit var auth: FirebaseAuth
    private lateinit var firestore: FirebaseFirestore
    private lateinit var post: UserPost

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_create_post) // Reusing the same layout as CreatePostActivity

        // Find the ImageView within the included toolbar layout
        val toolbar: androidx.appcompat.widget.Toolbar = findViewById(R.id.toolbar)
        val logoHome: ImageView = toolbar.findViewById(R.id.logoHome)
        val logoAccount: ImageView = toolbar.findViewById(R.id.logoAccount)

        logoHome.setOnClickListener {
            val intent = Intent(this, HomeActivity::class.java)
            startActivity(intent)
        }

        logoAccount.setOnClickListener {
            val intent = Intent(this, ProfileActivity::class.java)
            startActivity(intent)
        }

        // Initialize Firebase Auth and Firestore
        auth = FirebaseAuth.getInstance()
        firestore = FirebaseFirestore.getInstance()

        val description: EditText = findViewById(R.id.descriptionTextField)
        val email: EditText = findViewById(R.id.emailTextField)
        val needByDate: TextView = findViewById(R.id.needByDate)
        val phoneNumber: EditText = findViewById(R.id.phone_numberTextField)
        val bloodGroup: Spinner = findViewById(R.id.blood_groupTextField)
        val city: EditText = findViewById(R.id.cityTextField)
        val province: EditText = findViewById(R.id.provinceTextField)
        val country: EditText = findViewById(R.id.countryTextField)
        val age: EditText = findViewById(R.id.ageTextField)
        val savePostButton: Button = findViewById(R.id.create_post_button)
        val cancelButton: Button = findViewById(R.id.cancel_button)
        var needByLocalDate: LocalDateTime = LocalDateTime.now()

        savePostButton.text = "Save Post" // Change button text to "Save Post"

        // Set up the Spinner for selecting the blood group
        val bloodGroups = arrayOf("Select Blood Group", "A+", "A-", "B+", "B-", "AB+", "AB-", "O+", "O-", "A2", "A2B", "CisAB")
        val adapter = BloodGroupAdapter(this, android.R.layout.simple_spinner_item, bloodGroups)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        bloodGroup.adapter = adapter

        // Get the post data passed from the adapter
        post = intent.getParcelableExtra("post")!!

        // Prepopulate fields with post data
        description.setText(post.description)
        email.setText(post.email)
        needByDate.text = "Need Blood By Date: ${post.needByDate}"
        phoneNumber.setText(post.phone)
        bloodGroup.setSelection(bloodGroups.indexOf(post.bloodGroup))
        city.setText(post.city)
        province.setText(post.province)
        country.setText(post.country)
        age.setText(post.patientAge.toString())

        // Function to setup a date picker
        needByDate.setOnClickListener {
            showDatePickerDialog(needByDate)
        }

        savePostButton.setOnClickListener {
            val needByText = needByDate.text.toString()
            val actualNeedByText = if (needByText.startsWith("Need Blood By Date: ")) {
                needByText.substring("Need Blood By Date: ".length).trim()
            } else {
                needByText.trim()
            }
            needByLocalDate = getLocalDateTimeFromString(actualNeedByText)

            val selectedBloodGroup = if (bloodGroup.selectedItem.toString() == "Select Blood Group") {
                ""
            } else {
                bloodGroup.selectedItem.toString()
            }

            val updatedPost = post.copy(
                needByDate = needByLocalDate,
                bloodGroup = selectedBloodGroup,
                city = city.text.toString().trim(),
                province = province.text.toString().trim(),
                country = country.text.toString().trim(),
                description = description.text.toString().trim(),
                patientAge = age.text.toString().trim().toInt(),
                email = email.text.toString().trim(),
                phone = phoneNumber.text.toString().trim()
            )

            firestore.collection("posts").document(updatedPost.id)
                .set(updatedPost.toMap())
                .addOnSuccessListener {
                    Toast.makeText(this, "Post updated successfully!", Toast.LENGTH_SHORT).show()
                    val resultIntent = Intent()
                    setResult(Activity.RESULT_OK, resultIntent)
                    finish()
                }
                .addOnFailureListener { e ->
                    Toast.makeText(this, "Error updating post: ${e.message}", Toast.LENGTH_SHORT).show()
                }
        }

        cancelButton.setOnClickListener {
            val resultIntent = Intent()
            setResult(Activity.RESULT_OK, resultIntent)
            finish()
        }
    }

    private fun showDatePickerDialog(dobTextView: TextView) {
        val calendar = Calendar.getInstance()
        val year = calendar.get(Calendar.YEAR)
        val month = calendar.get(Calendar.MONTH)
        val day = calendar.get(Calendar.DAY_OF_MONTH)

        val datePickerDialog = DatePickerDialog(this, { _, selectedYear, selectedMonth, selectedDay ->
            val selectedDate = "$selectedDay/${selectedMonth + 1}/$selectedYear"
            dobTextView.text = "Need Blood By Date: $selectedDate"
            dobTextView.setTextColor(resources.getColor(R.color.redLight, null))
        }, year, month, day)

        datePickerDialog.datePicker.minDate = calendar.timeInMillis
        datePickerDialog.show()
    }

    private fun getLocalDateTimeFromString(dateString: String): LocalDateTime {
        val parts = dateString.split("/")

        val date = parts[0].toInt()
        val month = parts[1].toInt()
        val year = parts[2].toInt()
        return LocalDateTime.of(year, month, date, 0, 0)
    }
}
