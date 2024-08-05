package com.example.bloodbank

import android.app.Activity
import android.app.AlertDialog
import android.app.DatePickerDialog
import android.content.Intent
import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.Calendar

class EditPostActivity : AppCompatActivity() {
    private lateinit var auth: FirebaseAuth
    private lateinit var firestore: FirebaseFirestore
    private lateinit var post: UserPost

    private lateinit var description: EditText
    private lateinit var email: EditText
    private lateinit var needByDate: TextView
    private lateinit var phoneNumber: EditText
    private lateinit var bloodGroup: Spinner
    private lateinit var city: EditText
    private lateinit var province: EditText
    private lateinit var country: EditText
    private lateinit var age: EditText

    private lateinit var initialDescription: String
    private lateinit var initialEmail: String
    private lateinit var initialNeedByDate: LocalDateTime
    private lateinit var initialPhoneNumber: String
    private lateinit var initialBloodGroup: String
    private lateinit var initialCity: String
    private lateinit var initialProvince: String
    private lateinit var initialCountry: String
    private var initialAge: Int = 0

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

        val heading: TextView = findViewById(R.id.textView)
        description = findViewById(R.id.descriptionTextField)
        email = findViewById(R.id.emailTextField)
        needByDate = findViewById(R.id.needByDate)
        phoneNumber = findViewById(R.id.phone_numberTextField)
        bloodGroup = findViewById(R.id.blood_groupTextField)
        city = findViewById(R.id.cityTextField)
        province = findViewById(R.id.provinceTextField)
        country = findViewById(R.id.countryTextField)
        age = findViewById(R.id.ageTextField)
        val savePostButton: Button = findViewById(R.id.create_post_button)
        val cancelButton: Button = findViewById(R.id.cancel_button)
        var needByLocalDate: LocalDateTime = LocalDateTime.now()

        savePostButton.text = getString(R.string.editPageBtn)
        heading.text = getString(R.string.editPageTitle)

        // Set up the Spinner for selecting the blood group
        val bloodGroups = arrayOf("Select Blood Group", "A+", "A-", "B+", "B-", "AB+", "AB-", "O+", "O-", "A2", "A2B", "CisAB")
        val adapter = BloodGroupAdapter(this, android.R.layout.simple_spinner_item, bloodGroups)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        bloodGroup.adapter = adapter

        // Get the post data passed from the adapter
        post = intent.getParcelableExtra<UserPost>("post")!!

        // Prepopulate fields with post data to help user to update it
        description.setText(post.description)
        email.setText(post.email)
        needByDate.text = "Need Blood By: ${post.needByDate.format(DateTimeFormatter.ofPattern("d/M/yyyy"))}"
        phoneNumber.setText(post.phone)
        bloodGroup.setSelection(bloodGroups.indexOf(post.bloodGroup))
        city.setText(post.city)
        province.setText(post.province)
        country.setText(post.country)
        age.setText(post.patientAge.toString())

        // Store initial values
        initialDescription = post.description
        initialEmail = post.email
        initialNeedByDate = post.needByDate
        initialPhoneNumber = post.phone
        initialBloodGroup = post.bloodGroup
        initialCity = post.city
        initialProvince = post.province
        initialCountry = post.country
        initialAge = post.patientAge

        // Function to setup a date picker
        needByDate.setOnClickListener {
            showDatePickerDialog(needByDate)
        }

        savePostButton.setOnClickListener {
            val needByText = needByDate.text.toString()
            val actualNeedByText = if (needByText.startsWith("Need Blood By: ")) {
                needByText.substring("Need Blood By: ".length).trim()
            } else {
                needByText.trim()
            }
            needByLocalDate = getLocalDateTimeFromString(actualNeedByText)

            val selectedBloodGroup = if (bloodGroup.selectedItem.toString() == "Select Blood Group") {
                ""
            } else {
                bloodGroup.selectedItem.toString()
            }

            val updatedPostMap = mutableMapOf<String, Any>()

            // Make the update only if there is a change in the value of the specific field
            if (post.description != description.text.toString().trim()) {
                updatedPostMap["description"] = description.text.toString().trim()
            }
            if (post.email != email.text.toString().trim()) {
                updatedPostMap["email"] = email.text.toString().trim()
            }
            if (post.needByDate != needByLocalDate) {
                updatedPostMap["needByDate"] = needByLocalDate.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME)
            }
            if (post.phone != phoneNumber.text.toString().trim()) {
                updatedPostMap["phone"] = phoneNumber.text.toString().trim()
            }
            if (post.bloodGroup != selectedBloodGroup) {
                updatedPostMap["bloodGroup"] = selectedBloodGroup
            }
            if (post.city != city.text.toString().trim()) {
                updatedPostMap["city"] = city.text.toString().trim()
            }
            if (post.province != province.text.toString().trim()) {
                updatedPostMap["province"] = province.text.toString().trim()
            }
            if (post.country != country.text.toString().trim()) {
                updatedPostMap["country"] = country.text.toString().trim()
            }
            if (post.patientAge != age.text.toString().trim().toInt()) {
                updatedPostMap["patientAge"] = age.text.toString().trim().toInt()
            }

            // If there are any updates, update the database
            if (updatedPostMap.isNotEmpty()) {
                firestore.collection("posts").document(post.id)
                    .update(updatedPostMap)
                    .addOnSuccessListener {
                        Toast.makeText(this, "Post updated successfully!", Toast.LENGTH_SHORT).show()
                        val resultIntent = Intent()
                        setResult(Activity.RESULT_OK, resultIntent)
                        finish()
                    }
                    .addOnFailureListener { e ->
                        Toast.makeText(this, "Error updating post: ${e.message}", Toast.LENGTH_SHORT).show()
                    }
            } else {
                Toast.makeText(this, "No changes to update.", Toast.LENGTH_SHORT).show()
            }
        }

        cancelButton.setOnClickListener {
            if (isFormChanged()) {
                showCancelConfirmationDialog()
            } else {
                finish()
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
            dobTextView.text = "Need Blood By: $selectedDate"
            dobTextView.setTextColor(resources.getColor(R.color.redLight, null))
        }, year, month, day)

        datePickerDialog.datePicker.minDate = calendar.timeInMillis
        datePickerDialog.show()
    }

    private fun getLocalDateTimeFromString(dateString: String): LocalDateTime {
        return try {
            val formatter = DateTimeFormatter.ofPattern("d/M/yyyy")
            val localDate = LocalDate.parse(dateString, formatter)
            localDate.atStartOfDay()
        } catch (e: Exception) {
            LocalDateTime.now()
        }
    }

    private fun isFormChanged(): Boolean {
        return initialDescription != description.text.toString().trim() ||
                initialEmail != email.text.toString().trim() ||
                initialNeedByDate != getLocalDateTimeFromString(needByDate.text.toString().replace("Need Blood By: ", "").trim()) ||
                initialPhoneNumber != phoneNumber.text.toString().trim() ||
                initialBloodGroup != bloodGroup.selectedItem.toString() ||
                initialCity != city.text.toString().trim() ||
                initialProvince != province.text.toString().trim() ||
                initialCountry != country.text.toString().trim() ||
                initialAge != age.text.toString().trim().toInt()
    }

    private fun showCancelConfirmationDialog() {
        val dialogView = layoutInflater.inflate(R.layout.dialog_custom_popup, null)
        val dialog = AlertDialog.Builder(this)
            .setView(dialogView)
            .create()

        dialogView.findViewById<TextView>(R.id.dialog_title).text = getString(R.string.updateDialogTitle)
        dialogView.findViewById<TextView>(R.id.dialog_message).text = getString(R.string.updateDialogText)

        dialogView.findViewById<Button>(R.id.dialog_confirm).setOnClickListener {
            val resultIntent = Intent()
            setResult(Activity.RESULT_OK, resultIntent)
            finish()
            dialog.dismiss()
        }

        dialogView.findViewById<Button>(R.id.dialog_cancel).setOnClickListener {
            dialog.dismiss()
        }

        dialog.show()
    }
}
