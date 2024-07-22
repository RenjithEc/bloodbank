package com.example.bloodbank

data class User(
    val bloodGroup: String,
    val city: String,
    val country: String,
    val dob: String,
    val email: String,
    val firstName: String,
    val id: String,
    val isActive: Boolean,
    val lastName: String,
    val phoneNumber: String,
    val profilePic: String,
    val province: String
){
    companion object {
        // Function to create a instance from a Map
        fun fromMap(map: Map<String, Any>): User {
            return User(
                bloodGroup = map["bloodGroup"] as? String ?: "",
                city = map["city"] as? String ?: "",
                country = map["country"] as? String ?: "",
                dob = map["dob"] as? String ?: "",
                email = map["email"] as? String ?: "",
                firstName = map["firstName"] as? String ?: "",
                id = map["id"] as? String ?: "",
                isActive = map["isActive"] as? Boolean ?: false,
                lastName = map["lastName"] as? String ?: "",
                phoneNumber = map["phoneNumber"] as? String ?: "",
                profilePic = map["profilePic"] as? String ?: "",
                province = map["province"] as? String ?: ""
            )
        }
    }

    // Function to convert a instance to a Map
    fun toMap(): Map<String, Any> {
        return hashMapOf(
            "bloodGroup" to bloodGroup,
            "city" to city,
            "country" to country,
            "dob" to dob,
            "email" to email,
            "firstName" to firstName,
            "id" to id,
            "isActive" to isActive,
            "lastName" to lastName,
            "phoneNumber" to phoneNumber,
            "profilePic" to profilePic,
            "province" to province
        )
    }
}
