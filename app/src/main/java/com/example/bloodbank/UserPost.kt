package com.example.bloodbank

import java.util.Date

data class UserPost(
    val id: String,
    val firstName: String,
    val lastName: String,
    val needByDate: Date,
    val bloodGroup: String,
    val city: String,
    val province: String,
    val country: String,
    val profileImageUrl: String,
    val description: String,
    val priority: String,
    val userId: String,
    val patientAge: Int,
    val email: String,
    val phone: String,
    val createdTime: Date,
)
