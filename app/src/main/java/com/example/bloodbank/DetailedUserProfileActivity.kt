package com.example.bloodbank

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import android.widget.ImageView
import android.widget.TextView
import com.example.bloodbank.R


class UserProfileActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detailed_user_profile)

        val profileImageView: ImageView = findViewById(R.id.profileImageView)
        val activeDonor: TextView = findViewById(R.id.activeDonor)
        val nameTextView: TextView = findViewById(R.id.userFullName)
        val ageTextView: TextView = findViewById(R.id.userAge)
        val bloodGroupTextView: TextView = findViewById(R.id.bloodGroup)
        val provinceTextView: TextView = findViewById(R.id.userProvince)
        val emailTextView: TextView = findViewById(R.id.userEmail)
        //val phoneNumberTextView: TextView = findViewById(R.id.phoneNumberTextView)

        val firstName = intent.getStringExtra("firstName")
        val lastName = intent.getStringExtra("lastName")
        val age = intent.getIntExtra("age", 0)
        val bloodGroup = intent.getStringExtra("bloodGroup")
        val province = intent.getStringExtra("province")
        val email = intent.getStringExtra("email")
        val phoneNumber = intent.getStringExtra("phoneNumber")

        nameTextView.text = "$firstName $lastName"
        ageTextView.text = age.toString()
        bloodGroupTextView.text = bloodGroup
        provinceTextView.text = province
        emailTextView.text = email
        //phoneNumberTextView.text = phoneNumber

        // Uncomment the below line when you actually get a URL for the profile pic from firebase
        // Picasso.get().load(intent.getStringExtra("profileImageUrl")).into(profileImageView)

        emailTextView.setOnClickListener {
            val emailIntent = Intent(Intent.ACTION_SENDTO).apply {
                data = Uri.parse("mailto:$email")
                putExtra(Intent.EXTRA_EMAIL, arrayOf(email))
            }
            startActivity(emailIntent)
        }
    }
}