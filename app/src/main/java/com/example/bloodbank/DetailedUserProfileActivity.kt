package com.example.bloodbank

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import android.widget.ImageView
import android.widget.TextView
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.bumptech.glide.Glide
import java.time.LocalDate
import java.time.Period
import java.time.format.DateTimeFormatter
import java.time.format.DateTimeParseException

class DetailedUserProfileActivity : AppCompatActivity() {

    companion object {
        private const val REQUEST_CALL_PERMISSION = 1
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.d("DetailedUserProfile", "onCreate called")
        setContentView(R.layout.activity_detailed_user_profile)


        // Find the ImageView within the included toolbar layout
        val toolbar: androidx.appcompat.widget.Toolbar = findViewById(R.id.toolbar)
        val logoHome: ImageView = toolbar.findViewById(R.id.logoHome)
        val logoAccount: ImageView =  toolbar.findViewById(R.id.logoAccount)

        logoHome.setOnClickListener {
            val intent = Intent(this, HomeActivity::class.java)
            startActivity(intent)
        }

        logoAccount.setOnClickListener {
            val intent = Intent(this, ProfileActivity::class.java)
            startActivity(intent)
        }

        val profileImageView: ImageView = findViewById(R.id.imageView)
        val activeDonor: TextView = findViewById(R.id.activeDonor)
        val nameTextView: TextView = findViewById(R.id.userFullName)
        val ageTextView: TextView = findViewById(R.id.userAge)
        val bloodGroupTextView: TextView = findViewById(R.id.bloodGroup)
        val provinceTextView: TextView = findViewById(R.id.userProvince)
        val cityTextView: TextView = findViewById(R.id.userCity)
        val countryTextView: TextView = findViewById(R.id.userCountry)
        val emailIcon: ImageView = findViewById(R.id.emailIcon)
        val messageIcon: ImageView = findViewById(R.id.messageIcon)
        val callIcon: ImageView = findViewById(R.id.callIcon)

        val firstName = intent.getStringExtra("firstName") ?: ""
        val lastName = intent.getStringExtra("lastName") ?: ""
        val dob = intent.getStringExtra("dob") ?: ""
        val bloodGroup = intent.getStringExtra("bloodGroup") ?: ""
        val city = intent.getStringExtra("city") ?: ""
        val province = intent.getStringExtra("province") ?: ""
        val country = intent.getStringExtra("country") ?: ""
        val email = intent.getStringExtra("email") ?: ""
        val phoneNumber = intent.getStringExtra("phoneNumber") ?: ""
        val isActive = intent.getBooleanExtra("isActive", false)
        val profilePic = intent.getStringExtra("profilePic") ?: ""

        nameTextView.text = "$firstName $lastName"
        bloodGroupTextView.text = bloodGroup
        provinceTextView.text = province
        cityTextView.text = city
        countryTextView.text = country

        val age = calculateAge(dob)
        val stringAge = age.toString()
        val years = "years"
        ageTextView.text = "$stringAge $years"

        activeDonor.text = if (isActive) "Active Donor" else "Inactive"

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
        if (requestCode == REQUEST_CALL_PERMISSION) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                val phoneNumber = intent.getStringExtra("phoneNumber") ?: ""
                makePhoneCall(phoneNumber)
            } else {
                Log.e("DetailedUserProfile", "CALL_PHONE permission denied")
            }
        }
    }

    private fun calculateAge(dobString: String): Int {
        Log.d("DetailedUserProfile", "Date of Birth String: $dobString")
        val formatters = listOf(
            DateTimeFormatter.ofPattern("d/M/yyyy"),
            DateTimeFormatter.ofPattern("dd/MM/yyyy"),
            DateTimeFormatter.ofPattern("d/MM/yyyy"),
            DateTimeFormatter.ofPattern("dd/M/yyyy")
        )

        for (formatter in formatters) {
            try {
                Log.d("DetailedUserProfile", "Trying formatter: $formatter")
                val dob = LocalDate.parse(dobString, formatter)
                val currentDate = LocalDate.now()
                val age = Period.between(dob, currentDate).years
                Log.d("DetailedUserProfile", "Parsed Date of Birth: $dob, Current Date: $currentDate, Calculated Age: $age")
                return age
            } catch (e: DateTimeParseException) {
                Log.e("DetailedUserProfile", "Failed to parse date of birth: $dobString with formatter $formatter", e)
            }
        }

        Log.e("DetailedUserProfile", "Failed to parse date of birth: $dobString with all formatters")
        return 0 // Default age in case of parsing failure
    }
}