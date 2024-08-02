package com.example.bloodbank

import android.app.AlertDialog
import android.app.DatePickerDialog
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.Spinner
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SwitchCompat
import androidx.appcompat.widget.Toolbar
import com.bumptech.glide.Glide
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import de.hdodenhof.circleimageview.CircleImageView
import java.util.Calendar

class ProfileActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    private lateinit var firestore: FirebaseFirestore
    private lateinit var storage: FirebaseStorage
    private lateinit var profileImageView: CircleImageView
    private lateinit var changeProfilePictureButton: Button
    private lateinit var saveButton: Button

    private lateinit var firstNameTextView: TextView
    private lateinit var firstNameEditText: EditText
    private lateinit var firstNameEditButton: ImageButton

    private lateinit var lastNameTextView: TextView
    private lateinit var lastNameEditText: EditText
    private lateinit var lastNameEditButton: ImageButton

    private lateinit var dobTextView: TextView
    private lateinit var dobEditText: EditText
    private lateinit var dobEditButton: ImageButton

    private lateinit var phoneNumberTextView: TextView
    private lateinit var phoneNumberEditText: EditText
    private lateinit var phoneNumberEditButton: ImageButton

    private lateinit var bloodGroupSpinner: Spinner

    private lateinit var cityTextView: TextView
    private lateinit var cityEditText: EditText
    private lateinit var cityEditButton: ImageButton

    private lateinit var provinceTextView: TextView
    private lateinit var provinceEditText: EditText
    private lateinit var provinceEditButton: ImageButton

    private lateinit var countryTextView: TextView
    private lateinit var countryEditText: EditText
    private lateinit var countryEditButton: ImageButton

    private lateinit var isActiveSwitch: SwitchCompat
    private var currentProfilePic: String? = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile)

        auth = FirebaseAuth.getInstance()
        firestore = FirebaseFirestore.getInstance()
        storage = FirebaseStorage.getInstance()

        profileImageView = findViewById(R.id.profileImageView)
        changeProfilePictureButton = findViewById(R.id.changeProfilePictureButton)
        saveButton = findViewById(R.id.save_button)
        val logoutButton : Button = findViewById(R.id.logout_button)

        firstNameTextView = findViewById(R.id.text_first_name)
        firstNameEditText = findViewById(R.id.edit_first_name)
        firstNameEditButton = findViewById(R.id.edit_first_name_button)

        lastNameTextView = findViewById(R.id.text_last_name)
        lastNameEditText = findViewById(R.id.edit_last_name)
        lastNameEditButton = findViewById(R.id.edit_last_name_button)

        dobTextView = findViewById(R.id.text_dob)
        dobEditText = findViewById(R.id.edit_dob)
        dobEditButton = findViewById(R.id.edit_dob_button)

        phoneNumberTextView = findViewById(R.id.text_phone_number)
        phoneNumberEditText = findViewById(R.id.edit_phone_number)
        phoneNumberEditButton = findViewById(R.id.edit_phone_number_button)

        bloodGroupSpinner = findViewById(R.id.edit_blood_group)

        cityTextView = findViewById(R.id.text_city)
        cityEditText = findViewById(R.id.edit_city)
        cityEditButton = findViewById(R.id.edit_city_button)

        provinceTextView = findViewById(R.id.text_province)
        provinceEditText = findViewById(R.id.edit_province)
        provinceEditButton = findViewById(R.id.edit_province_button)

        countryTextView = findViewById(R.id.text_country)
        countryEditText = findViewById(R.id.edit_country)
        countryEditButton = findViewById(R.id.edit_country_button)

        isActiveSwitch = findViewById(R.id.isActiveSwitch)

        // Set up the toolbar
        val toolbar: Toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)

        val logoHome: ImageView = findViewById(R.id.logoHome)

        logoHome.setOnClickListener {
            setResult(RESULT_OK) // Set result as RESULT_OK to indicate changes
            finish()
        }


        changeProfilePictureButton.setOnClickListener {
            // Open the image picker
            pickImage()
        }

        firstNameEditButton.setOnClickListener {
            toggleEditMode(firstNameTextView, firstNameEditText)
        }

        lastNameEditButton.setOnClickListener {
            toggleEditMode(lastNameTextView, lastNameEditText)
        }

        dobEditButton.setOnClickListener {
            toggleEditMode(dobTextView, dobEditText)
            showDatePickerDialog()
        }

        dobEditText.setOnClickListener {
            toggleEditMode(dobTextView, dobEditText)
            showDatePickerDialog()
        }

        phoneNumberEditButton.setOnClickListener {
            toggleEditMode(phoneNumberTextView, phoneNumberEditText)
        }

        cityEditButton.setOnClickListener{
            toggleEditMode(cityTextView, cityEditText)
        }

        provinceEditButton.setOnClickListener{
            toggleEditMode(provinceTextView, provinceEditText)
        }

        countryEditButton.setOnClickListener{
            toggleEditMode(countryTextView, countryEditText)
        }

        saveButton.setOnClickListener {
            saveProfileData(false)
        }

        logoutButton.setOnClickListener {
            val dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_custom_popup, null)
            val dialog = AlertDialog.Builder(this)
                .setView(dialogView)
                .create()

            dialogView.findViewById<TextView>(R.id.dialog_title).text = getString(R.string.logoutDialogTitle)
            dialogView.findViewById<TextView>(R.id.dialog_message).text = getString(R.string.logoutDialogText)

            dialogView.findViewById<Button>(R.id.dialog_confirm).setOnClickListener {
                FirebaseAuth.getInstance().signOut()
                val intent = Intent(this, LoginActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                startActivity(intent)
                finish()
                dialog.dismiss()
            }

            dialogView.findViewById<Button>(R.id.dialog_cancel).setOnClickListener {
                dialog.dismiss()
            }
            dialog.show()
        }


        // Load existing profile data, including profile picture, from Firestore
        loadProfileData()

        // Set custom adapter for the spinner
        val adapter = ArrayAdapter.createFromResource(
            this,
            R.array.allBloodGroups,
            R.layout.spinner_item
        )
        adapter.setDropDownViewResource(R.layout.spinner_dropdown_item)
        bloodGroupSpinner.adapter = adapter
    }

    private fun toggleEditMode(textView: TextView, editText: EditText) {
            if (textView.visibility == View.VISIBLE) {
                textView.visibility = View.GONE
                editText.visibility = View.VISIBLE
                editText.requestFocus() // Focus on the EditText
            }
    }

    private fun pickImage() {
        val intent = Intent(Intent.ACTION_PICK)
        intent.type = "image/*"
        resultLauncher.launch(intent)
    }

    private val resultLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == RESULT_OK) {
            val data: Intent? = result.data
            val imageUri: Uri? = data?.data
            if (imageUri != null) {
                uploadProfileImageToFirebase(imageUri)
            }
        }
    }

    private fun uploadProfileImageToFirebase(imageUri: Uri) {
        val storageRef = storage.reference.child("profile_pictures/${auth.currentUser?.uid}.jpg")
        val uploadTask = storageRef.putFile(imageUri)

        uploadTask.continueWithTask { task ->
            if (!task.isSuccessful) {
                task.exception?.let {
                    throw it
                }
            }
            storageRef.downloadUrl
        }.addOnCompleteListener { task ->
            if (task.isSuccessful) {
                val downloadUri = task.result
                currentProfilePic = downloadUri.toString()
                Glide.with(this).load(downloadUri).into(profileImageView)
                // Save profile data with new image URL
                saveProfileData(true)
            } else {
                Toast.makeText(this, "Failed to upload image", Toast.LENGTH_SHORT).show()
            }
        }.addOnFailureListener {
            Toast.makeText(this, "Failed to upload image", Toast.LENGTH_SHORT).show()
        }
    }

    private fun saveProfileData(isProfilePicUpdate: Boolean) {

        val user = auth.currentUser ?: return
        val userRef = firestore.collection("users").document(user.uid)

        val profileData = hashMapOf(
            "firstName" to firstNameEditText.text.toString(),
            "lastName" to lastNameEditText.text.toString(),
            "dob" to dobEditText.text.toString(),
            "phoneNumber" to phoneNumberEditText.text.toString(),
            "bloodGroup" to bloodGroupSpinner.selectedItem.toString(),
            "city" to cityEditText.text.toString(),
            "email" to auth.currentUser?.email,
            "id" to auth.currentUser?.uid,
            "province" to provinceEditText.text.toString(),
            "country" to countryEditText.text.toString(),
            "isActive" to isActiveSwitch.isChecked,
            "profilePic" to currentProfilePic.orEmpty() // Ensure currentProfilePic is not null
        )

        userRef.set(profileData)
            .addOnSuccessListener {
                Toast.makeText(this, "Profile updated successfully", Toast.LENGTH_SHORT).show()
                if(!isProfilePicUpdate){
                    setResult(RESULT_OK) // Set result as RESULT_OK to indicate changes
                    finish() // Close the activity
                }
            }
            .addOnFailureListener {
                Toast.makeText(this, "Failed to update profile", Toast.LENGTH_SHORT).show()
            }
    }

    private fun loadProfileData() {
        val user = auth.currentUser ?: return
        val userRef = firestore.collection("users").document(user.uid)

        userRef.get().addOnSuccessListener { document ->
            if (document != null) {
                firstNameTextView.text = document.getString("firstName")
                firstNameEditText.setText(document.getString("firstName"))

                lastNameTextView.text = document.getString("lastName")
                lastNameEditText.setText(document.getString("lastName"))

                dobTextView.text = document.getString("dob")
                dobEditText.setText(document.getString("dob"))

                phoneNumberTextView.text = document.getString("phoneNumber")
                phoneNumberEditText.setText(document.getString("phoneNumber"))

                val bloodGroup = document.getString("bloodGroup")
                val bloodGroupIndex =
                    resources.getStringArray(R.array.allBloodGroups).indexOf(bloodGroup)
                bloodGroupSpinner.setSelection(bloodGroupIndex)

                cityTextView.text = document.getString("city")
                cityEditText.setText(document.getString("city"))

                provinceTextView.text = document.getString("province")
                provinceEditText.setText(document.getString("province"))

                countryTextView.text = document.getString("country")
                countryEditText.setText(document.getString("country"))

                isActiveSwitch.isChecked = document.getBoolean("isActive") ?: false

                val profilePic = document.getString("profilePic")
                if (!profilePic.isNullOrEmpty()) {
                    currentProfilePic = profilePic
                    Glide.with(this).load(profilePic).into(profileImageView)
                }
            }
        }
            .addOnFailureListener {
                Toast.makeText(this, "Failed to load profile data", Toast.LENGTH_SHORT).show()
            }
    }

    private fun showDatePickerDialog() {
        val calendar = Calendar.getInstance()
        val year = calendar.get(Calendar.YEAR)
        val month = calendar.get(Calendar.MONTH)
        val day = calendar.get(Calendar.DAY_OF_MONTH)

        val datePickerDialog = DatePickerDialog(this, { _, selectedYear, selectedMonth, selectedDay ->
            val selectedDate = Calendar.getInstance()
            selectedDate.set(selectedYear, selectedMonth, selectedDay)

            if (selectedDate.after(calendar)) {
                Toast.makeText(this, "Invalid Date of Birth", Toast.LENGTH_SHORT).show()
            } else {
                dobTextView.text =
                    getString(R.string.date_format, selectedDay, selectedMonth + 1, selectedYear)
                dobEditText.setText(getString(R.string.date_format, selectedDay, selectedMonth + 1, selectedYear))
            }
        }, year, month, day)

        datePickerDialog.show()
    }
}



