package com.example.bloodbank

import com.google.firebase.firestore.FirebaseFirestoreException
import com.google.firebase.firestore.PropertyName
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.QueryDocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestoreSettings
import com.google.firebase.firestore.QuerySnapshot
import com.google.firebase.firestore.ServerTimestamp
import com.google.firebase.firestore.DocumentId
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.IgnoreExtraProperties
import com.google.firebase.firestore.auth.User
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.*
import com.google.firebase.firestore.DocumentReference

object LocalDateTimeConverter {
    private val formatter: DateTimeFormatter = DateTimeFormatter.ISO_LOCAL_DATE_TIME

    fun toString(dateTime: LocalDateTime): String {
        return dateTime.format(formatter)
    }

    fun fromString(dateTimeString: String?): LocalDateTime {
        return LocalDateTime.parse(dateTimeString, formatter)
    }
}
