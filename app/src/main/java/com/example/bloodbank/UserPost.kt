package com.example.bloodbank

import android.os.Parcel
import android.os.Parcelable
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
) : Parcelable {
    companion object {
        // Function to create an instance from a Map
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

        @JvmField
        val CREATOR = object : Parcelable.Creator<UserPost> {
            override fun createFromParcel(parcel: Parcel): UserPost {
                return UserPost(parcel)
            }

            override fun newArray(size: Int): Array<UserPost?> {
                return arrayOfNulls(size)
            }
        }
    }

    // Function to convert an instance to a Map
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

    constructor(parcel: Parcel) : this(
        parcel.readString() ?: "",
        parcel.readString() ?: "",
        parcel.readString() ?: "",
        LocalDateTime.parse(parcel.readString(), DateTimeFormatter.ISO_LOCAL_DATE_TIME),
        parcel.readString() ?: "",
        parcel.readString() ?: "",
        parcel.readString() ?: "",
        parcel.readString() ?: "",
        parcel.readString() ?: "",
        parcel.readString() ?: "",
        parcel.readString() ?: "",
        parcel.readString() ?: "",
        parcel.readInt(),
        parcel.readString() ?: "",
        parcel.readString() ?: "",
        LocalDateTime.parse(parcel.readString(), DateTimeFormatter.ISO_LOCAL_DATE_TIME)
    )

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(id)
        parcel.writeString(firstName)
        parcel.writeString(lastName)
        parcel.writeString(needByDate.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME))
        parcel.writeString(bloodGroup)
        parcel.writeString(city)
        parcel.writeString(province)
        parcel.writeString(country)
        parcel.writeString(profileImageUrl)
        parcel.writeString(description)
        parcel.writeString(priority)
        parcel.writeString(userId)
        parcel.writeInt(patientAge)
        parcel.writeString(email)
        parcel.writeString(phone)
        parcel.writeString(createdTime.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME))
    }

    override fun describeContents(): Int {
        return 0
    }
}