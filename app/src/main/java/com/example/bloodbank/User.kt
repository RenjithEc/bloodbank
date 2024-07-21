package com.example.bloodbank

data class User(
    val firstName: String,
    val lastName: String,
    val dateOfBirth: String,
    val bloodGroup: String,
    val city: String,
    val province: String,
    val country: String,
    val profileImageUrl: String
)
