package com.example.bloodbank

import android.app.DatePickerDialog
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Spinner
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import java.util.Calendar

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
        val createPostButton: Button = findViewById(R.id.create_post_button)

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

        }

    }

    private fun showDatePickerDialog(dobTextView: TextView) {
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
}