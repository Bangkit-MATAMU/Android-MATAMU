package com.akih.matarak.data

import com.google.firebase.database.Exclude
import com.google.firebase.database.IgnoreExtraProperties
import java.util.*

@IgnoreExtraProperties
data class User(
    val username: String,
    val name: String,
    val email: String,
    val ttl: String,
    val alamat: String,
    val gender: Int
){
    @Exclude
    fun toMap(): Map<String, Any?> {
        return mapOf(
                "username" to username,
                "name" to name,
                "email" to email,
                "ttl" to ttl,
                "alamat" to alamat,
                "gender" to gender
        )
    }
}