package com.akih.matarak.data

import com.google.firebase.database.Exclude
import com.google.firebase.database.IgnoreExtraProperties

@IgnoreExtraProperties
data class User(
    val username: String,
    val name: String,
    val imageUrl: String,
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
                "imageUrl" to imageUrl,
                "email" to email,
                "ttl" to ttl,
                "alamat" to alamat,
                "gender" to gender
        )
    }
}