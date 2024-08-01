package com.example.bloodbank

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.Spinner
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatActivity.RESULT_OK
import androidx.appcompat.widget.SwitchCompat
import androidx.appcompat.widget.Toolbar
import com.bumptech.glide.Glide
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import de.hdodenhof.circleimageview.CircleImageView

class DetailedUserPost : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    private lateinit var firestore: FirebaseFirestore
    private lateinit var storage: FirebaseStorage
    private lateinit var profileImageView: CircleImageView
    private lateinit var fullNameText: TextView
    private lateinit var phoneNumberText: TextView
    private lateinit var bloodGroupText: TextView
    private lateinit var cityText: TextView
    private lateinit var provinceText: TextView
    private lateinit var countryText: TextView
    private lateinit var needByText: TextView
    private lateinit var emailText: TextView
    private lateinit var descriptionText: TextView
    private lateinit var priorityText: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detailed_user_post)

        auth = FirebaseAuth.getInstance()
        firestore = FirebaseFirestore.getInstance()
        storage = FirebaseStorage.getInstance()

        profileImageView = findViewById(R.id.profileImageView)
        fullNameText = findViewById(R.id.fullName)
        phoneNumberText = findViewById(R.id.phoneNumber)
        bloodGroupText = findViewById(R.id.bloodGroup)
        cityText = findViewById(R.id.city)
        provinceText = findViewById(R.id.province)
        countryText = findViewById(R.id.country)
        needByText = findViewById(R.id.needByDate)
        descriptionText = findViewById(R.id.description)
        emailText = findViewById(R.id.email)
        priorityText = findViewById(R.id.priority)

        val firstName = intent.getStringExtra("firstName") ?: ""
        val lastName = intent.getStringExtra("lastName") ?: ""
        val bloodGroup = intent.getStringExtra("bloodGroup") ?: ""
        val city = intent.getStringExtra("city") ?: ""
        val province = intent.getStringExtra("province") ?: ""
        val country = intent.getStringExtra("country") ?: ""
        val email = intent.getStringExtra("email") ?: ""
        val phoneNumber = intent.getStringExtra("phoneNumber") ?: ""
        val profilePic = intent.getStringExtra("profilePic")?:""
        val description = intent.getStringExtra("description") ?: ""
        val priority = intent.getStringExtra("priority") ?: ""
        val needByDate = intent.getStringExtra("needByDate") ?: ""

        Log.d("priority", "Testing priority : $priority")
        Log.d("needByDate", "Testing needByDate : $needByDate")


        fullNameText.text = "$firstName $lastName"
        phoneNumberText.text = phoneNumber
        bloodGroupText.text =  bloodGroup
        provinceText.text = province
        cityText.text = city
        countryText.text = country
        descriptionText.text = description
        emailText.text = email
        priorityText.text = priority
        needByText.text = needByDate



        // Set up the toolbar
        val toolbar: Toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)

        val logoHome: ImageView = findViewById(R.id.logoHome)

        logoHome.setOnClickListener {
            val intent = Intent(this, HomeActivity::class.java)
            setResult(RESULT_OK) // Set result as RESULT_OK to indicate changes
            startActivity(intent)
        }

        if (profilePic.isNotEmpty()) {
            Glide.with(this)
                .load(profilePic)
                .placeholder(R.drawable.ic_profile_placeholder)
                .error(R.drawable.ic_profile_placeholder)
                .into(profileImageView)
        } else {
            profileImageView.setImageResource(R.drawable.ic_profile_placeholder)
        }
    }

}

