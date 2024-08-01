package com.example.bloodbank

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.bumptech.glide.Glide
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import de.hdodenhof.circleimageview.CircleImageView

class DetailedUserPostActivity : AppCompatActivity() {

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
    private lateinit var emailIcon:ImageView
    private lateinit var messageIcon:ImageView
    private lateinit var callIcon:ImageView
    private lateinit var goBackButton: Button

    companion object {
        private const val REQUEST_CALL_PERMISSION = 1
    }


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
        emailIcon= findViewById(R.id.emailIcon)
        messageIcon = findViewById(R.id.messageIcon)
        callIcon= findViewById(R.id.callIcon)
        goBackButton = findViewById(R.id.goBackButton)

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

        if (profilePic.isNotEmpty()) {
            Glide.with(this)
                .load(profilePic)
                .placeholder(R.drawable.ic_profile_placeholder)
                .error(R.drawable.ic_profile_placeholder)
                .into(profileImageView)
        } else {
            profileImageView.setImageResource(R.drawable.ic_profile_placeholder)
        }
        emailIcon.setOnClickListener {
            val emailIntent = Intent(Intent.ACTION_SENDTO).apply {
                data = Uri.parse("mailto:$email")
            }
            startActivity(Intent.createChooser(emailIntent, "Send email"))
        }

        messageIcon.setOnClickListener {
            val messageIntent = Intent(Intent.ACTION_SENDTO).apply {
                data = Uri.parse("smsto:$phoneNumber")
                putExtra("sms_body", "Hello")
            }
            startActivity(messageIntent)
        }

        callIcon.setOnClickListener {
            makePhoneCall(phoneNumber)
        }

        goBackButton.setOnClickListener{
            finish()
        }
    }

    private fun makePhoneCall(phoneNumber: String) {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.CALL_PHONE), REQUEST_CALL_PERMISSION)
        } else {
            val callIntent = Intent(Intent.ACTION_DIAL).apply {
                data = Uri.parse("tel:$phoneNumber")
            }
            startActivity(callIntent)
        }
    }


    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == DetailedUserProfileActivity.REQUEST_CALL_PERMISSION) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                val phoneNumber = intent.getStringExtra("phoneNumber") ?: ""
                makePhoneCall(phoneNumber)
            } else {
                Log.e("DetailedUserProfile", "CALL_PHONE permission denied")
            }
        }
    }

}

