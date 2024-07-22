package com.example.bloodbank

import java.time.LocalDateTime
import java.time.format.DateTimeFormatter


data class UserPost(
    val id: String,
    val firstName: String,
    val lastName: String,
    val needByDate: LocalDateTime,
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
    val createdTime: LocalDateTime,
){
    companion object {
        // Function to create a instance from a Map
        fun fromMap(map: Map<String, Any>): UserPost {
            val formatter = DateTimeFormatter.ISO_LOCAL_DATE_TIME

            return UserPost(
                id = map["id"] as? String ?: "",
                firstName = map["firstName"] as? String ?: "",
                lastName = map["lastName"] as? String ?: "",
                needByDate = LocalDateTime.parse(map["needByDate"] as? String ?: "", formatter),
                bloodGroup = map["bloodGroup"] as? String ?: "",
                city = map["city"] as? String ?: "",
                province = map["province"] as? String ?: "",
                country = map["country"] as? String ?: "",
                profileImageUrl = map["profileImageUrl"] as? String ?: "",
                description = map["description"] as? String ?: "",
                priority = map["priority"] as? String ?: "",
                userId = map["userId"] as? String ?: "",
                patientAge = (map["patientAge"] as? Number)?.toInt() ?: 0,
                email = map["email"] as? String ?: "",
                phone = map["phone"] as? String ?: "",
                createdTime = LocalDateTime.parse(map["createdTime"] as? String ?: "", formatter)
            )
        }
    }

    // Function to convert a instance to a Map
    fun toMap(): HashMap<String, Any> {
        val formatter = DateTimeFormatter.ISO_LOCAL_DATE_TIME

        return hashMapOf(
            "id" to id,
            "firstName" to firstName,
            "lastName" to lastName,
            "needByDate" to needByDate.format(formatter),
            "bloodGroup" to bloodGroup,
            "city" to city,
            "province" to province,
            "country" to country,
            "profileImageUrl" to profileImageUrl,
            "description" to description,
            "priority" to priority,
            "userId" to userId,
            "patientAge" to patientAge,
            "email" to email,
            "phone" to phone,
            "createdTime" to createdTime.format(formatter)
        )
    }
}
